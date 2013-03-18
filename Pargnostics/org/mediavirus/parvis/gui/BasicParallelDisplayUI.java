/*

Copyright (c) 2001, 2002, 2003 Flo Ledermann <flo@subnet.at>

This file is part of parvis - a parallel coordiante based data visualisation
tool written in java. You find parvis and additional information on its
website at http://www.mediavirus.org/parvis.

parvis is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

parvis is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with parvis (in the file LICENSE.txt); if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 */

package org.mediavirus.parvis.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import org.mediavirus.parvis.gui.analysis.AxisPair;
import org.mediavirus.parvis.gui.analysis.AxisPair.Metrics;
import org.mediavirus.parvis.model.Brush;

/**
 * The UI Delegate, responsible for rendering the ParallelDisplay component.
 * 
 * @author Flo Ledermann flo@subnet.at
 * @version 0.1
 */
public class BasicParallelDisplayUI extends ComponentUI implements MouseListener, MouseMotionListener {
	
	public static final float RECORD_COLOR_HIGH[] = {251/255f, 106/255f, 74/255f, .9f};

	public static final float RECORD_COLOR_MID[] = {0.874509804f, 0.760784314f, 0.490196078f, .1f};

	public static final float RECORD_COLOR_LOW[] = {49/255f, 130/255f, 189/255f, .9f};

	int numDimensions;

	int numRecords;

	int stepx;

	boolean flag=true;

	int hoverAxis = -1;

	int hoverRecord = -1;

	float axisScale[];

	int borderH = 20;

	int borderV = 40;

	int width = 0, height = 0;

	String metaText = null;

	int metaX = 0, metaY = 0;

	boolean dragAxis = false;

	int dragX = 0;
	
	RenderedImage renderedImg=null;

	BufferedImage bufferImg = null;

	BufferedImage brushImg = null;

	boolean needsDeepRepaint = true;

	boolean renderQuality = false;

	int brushHoverStart = 0;

	int brushHoverEnd = 0;

	int brushHoverX = 0;

	boolean inBrush = false;

	Brush tempBrush = null;

	Brush dragBrush = null;

	//	private DisplayMetricBarsPanel metricsDisplay;

	// maximum overplotting across all dimensions
	int maxOverplotting;

	//maximum entropy across all dimensions
	float maxEntropy;

	boolean pairwiseClustering= true;

	boolean manhattanDistanceMetric =true;

	private boolean alphaFlag = false;

	/**
	 * Default Constructor. Creates a new BasicParallelDisplayUI.
	 */
	public BasicParallelDisplayUI() {

	}

	/**
	 * Swing method. Returns a new instance.
	 */
	public static ComponentUI createUI(JComponent c) {
		return new BasicParallelDisplayUI();
	}

	/**
	 * Installs this instance as UI delegate for the given component.
	 * 
	 * @param c
	 *            The component, a ParallelDisplay in our case.
	 */
	public void installUI(JComponent c) {

		parallelDisplay = (ParallelDisplay) c;
		parallelDisplay.addMouseListener(this);
		parallelDisplay.addMouseMotionListener(this);

	}

	/**
	 * Uninstalls this instance from its component.
	 * 
	 * @param c
	 *            The component, a ParallelDisplay in our case.
	 */
	public void uninstallUI(JComponent c) {
		ParallelDisplay pd = (ParallelDisplay) c;

		pd.removeMouseListener(this);
		pd.removeMouseMotionListener(this);

		numDimensions = 0;
		numRecords = 0;
	}


	/** RenderThread instance to render the dataset. */
	RenderThread renderThread = null;

	/** RenderThread instance to render the brushed records. */
	RenderThread brushThread = null;

	private float alpha = 1;

	private float stepSize = 0.05f;

	enum RenderMode {
		INITIAL, DRAFT, FINAL
	}
	
	private RenderMode renderMode = RenderMode.INITIAL;

	private float[] pixelBasedEntropyArray;

	/**
	 * Renders the component on the screen.
	 * 
	 * @param g
	 *            The graphics object to draw on.
	 * @param c
	 *            The Component, our ParallelDisplay.
	 */


	public void paint(Graphics g, JComponent c) {

		ParallelDisplay comp = (ParallelDisplay) c;

		//getMaxOverplotting(comp);

		// start our renderThread
		if (renderThread == null) {
			renderThread = new RenderThread(this);
			renderThread.setQuality(false, true);
			renderThread.setStyle(new BasicStroke(0.5f), comp
					.getColorPreference("recordColor"));
			renderThread.start();

		}

		if (brushThread == null) {
			brushThread = new RenderThread(this);
			brushThread.setQuality(false, true);
			brushThread.setStyle(new BasicStroke(1.5f), comp
					.getColorPreference("brushColor"));
			brushThread.start();
		}

		// set up the environment
		Graphics2D g2 = (Graphics2D) g;

		RenderingHints qualityHints = new RenderingHints(null);

		qualityHints.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		qualityHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		g2.setRenderingHints(qualityHints);

		// workaround flag for model change, resize,...

		if (comp.deepRepaint) {
			// throw away buffered image -> complete repaint


			//	MetricsDisplayPanel.getInstance().createMetricsDisplay();
			flag=!flag;

			width = c.getWidth() - 2 * borderH;
			height = c.getHeight() - 2 * borderV;

			numDimensions = comp.getNumAxes();
			numRecords = comp.getNumRecords();
			if (numDimensions!=1)
				stepx = width / (numDimensions - 1);

//			System.out.println("Setting numDimensions to " + numDimensions);
			needsDeepRepaint = true;

			bufferImg = new BufferedImage(c.getWidth(), c.getHeight(),
					BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D ig = bufferImg.createGraphics();
			ig.setColor(c.getBackground());
			ig.fillRect(0, 0, c.getWidth(), c.getHeight());
			// brushImg = null;

			renderThread.reset();
			brushThread.reset();
			renderThread.setCurrentComponent(comp);
			brushThread.setCurrentComponent(comp);

			brushThread.setBrush(comp.getCurrentBrush());
			if (comp.getCurrentBrush() == null) {
				brushImg = null;
			} else {
				renderBrush();
			}
			renderAll();

			comp.deepRepaint = false;
		} else if (comp.brushChanged) {
			if (!tempBrushSwap) {
				brushImg = null;
				brushThread.reset();

				brushThread.setBrush(comp.getCurrentBrush());
				renderBrush();
			} else {
				// we just came from interactive brushing -> no need for
				// re-rendering
				tempBrushSwap = false;
			}

			comp.brushChanged = false;
		}

		g2.setColor(c.getBackground());
		g2.fillRect(0, 0, comp.getWidth(), comp.getHeight());

		g2.translate(borderH, borderV);

		// save rendered image in new buffer
		if (renderThread.getRenderedImage() != null) {
			// we cant do this becase the renderedImage is only a part of the
			// whole
			// bufferImg = (BufferedImage)renderThread.getRenderedImage();
			
			Graphics2D ig = bufferImg.createGraphics();
			ig.setColor(comp.getBackground());
			int startAxis = renderThread.getRenderedRegionStart();
			int stopAxis = renderThread.getRenderedRegionStop();

			// delete area that has been rendered
			ig.fillRect(startAxis * stepx, 0, (stopAxis - startAxis) * stepx,
					comp.getHeight());
			// and paint it new
			ig.drawImage(renderThread.getRenderedImage(), 0, 0, comp);
			
		//	if (renderMode == RenderMode.FINAL || alpha >= 1)
				//System.err.println(alpha+"\t"+computeEntropy(bufferImg));

			
		}

		if (brushThread.getRenderedImage() != null) {
			brushImg = brushThread.getRenderedImage();
		}

		if ((comp.getCurrentBrush() == null) && (brushImg != null)
				&& (!inBrush)) {
			brushImg = null;
		}

		if ((comp.getCurrentBrush() == null) && (!inBrush)) {
			synchronized (bufferImg) {
				g2.drawImage(bufferImg, 0, 0, comp);
			}
		} else {
			Composite oldcomp = g2.getComposite();

			AlphaComposite ac = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.15f);
			g2.setComposite(ac);

			g2.drawImage(bufferImg, 0, 0, comp);

			g2.setComposite(oldcomp);
			if (brushImg != null) {
				g2.drawImage(brushImg, 0, 0, comp);
			}
		}

		// set up
		g2.setColor(comp.getForeground());
		g2.setStroke(new BasicStroke(1.0f));

		// draw all the dynamic parts on the screen:

		// axis labels
		for (int i = 0; i < numDimensions; i++) {
			float curx = i * stepx;

			// hovering over Axis
			if (i == hoverAxis) {
				g2.setStroke(new BasicStroke(1.5f));
				g2.draw(new Line2D.Float(curx, 0, curx, height));
				g2.setStroke(new BasicStroke(1.0f));
			} else {
				g2.draw(new Line2D.Float(curx, 0, curx, height));
			}

			String label = comp.getAxisLabel(i);
			if (label != null) {
				g2.drawString(label, curx - 10, height + 30);
			}

			g2.drawString("" + comp.getAxisOffset(i), curx + 2,
					borderV / 2 - 22);
			g2.drawString("" + (comp.getAxisOffset(i) + comp.getAxisScale(i)),
					curx + 2, height + borderV / 2 - 5);

			drawArrow(g2, (int) curx, -20, 8, false, (comp.getAxisScale(i) < 0));
		}

		// brush Hover
		if (inBrush) {
			g2.setColor(new Color(0.7f, 0.0f, 0.0f));
			g2.setStroke(new BasicStroke(2.5f));
			g2.draw(new Line2D.Float(brushHoverX, brushHoverStart, brushHoverX,
					brushHoverEnd));
			// TODO: Sometimes hovering stops working after this
			if (hoverAxis == -1)
				inBrush = false;
		}

		// angular brushing
		if (inAngularBrush) {

			if (angularPhase1) {

				int startx, endx, starty, endy;

				if (angularCurX != angularRefX) { // avoid div. by zero
					startx = angularRegion * stepx;
					endx = (angularRegion + 1) * stepx;
					starty = (int) (angularRefY - (angularRefX - (float) startx)
							/ ((float) (angularCurX - angularRefX))
							* (angularCurY - (float) angularRefY));
					endy = (int) (angularCurY + (endx - (float) angularCurX)
							/ ((float) (angularCurX - angularRefX))
							* (angularCurY - (float) angularRefY));
				} else {
					startx = angularCurX;
					endx = angularCurX;
					starty = 0 - borderV;
					endy = height + borderV;
				}
				g2.setColor(comp.getForeground());
				g2.setStroke(new BasicStroke(1.0f));
				g2.drawLine(startx, starty, endx, endy);

				g2.drawRect(angularRefX - 2, angularRefY - 2, 4, 4);
				g2.drawRect(angularCurX - 2, angularCurY - 2, 4, 4);
			} else {
				int startx, endx, starty, endy;

				if (angularStartX != angularRefX) { // avoid div. by zero
					startx = angularRegion * stepx;
					endx = (angularRegion + 1) * stepx;
					starty = (int) (angularRefY - (angularRefX - (float) startx)
							/ ((float) (angularStartX - angularRefX))
							* (angularStartY - (float) angularRefY));
					endy = (int) (angularStartY + (endx - (float) angularStartX)
							/ ((float) (angularStartX - angularRefX))
							* (angularStartY - (float) angularRefY));
				} else {
					startx = angularStartX;
					endx = angularStartX;
					starty = 0 - borderV;
					endy = height + borderV;
				}

				g2.setColor(comp.getForeground());
				g2.setStroke(new BasicStroke(1.0f));
				g2.drawLine(startx, starty, endx, endy);

				if (angularCurX != angularRefX) { // avoid div. by zero
					startx = angularRegion * stepx;
					endx = (angularRegion + 1) * stepx;
					starty = (int) (angularRefY - (angularRefX - (float) startx)
							/ ((float) (angularCurX - angularRefX))
							* (angularCurY - (float) angularRefY));
					endy = (int) (angularCurY + (endx - (float) angularCurX)
							/ ((float) (angularCurX - angularRefX))
							* (angularCurY - (float) angularRefY));
				} else {
					startx = angularCurX;
					endx = angularCurX;
					starty = 0 - borderV;
					endy = height + borderV;
				}

				g2.drawLine(startx, starty, endx, endy);

				g2.drawRect(angularRefX - 2, angularRefY - 2, 4, 4);
				g2.drawRect(angularStartX - 2, angularStartY - 2, 4, 4);
				g2.drawRect(angularCurX - 2, angularCurY - 2, 4, 4);

				Color tc = comp.getForeground();

				// modify alpha


				g2.setColor(new Color(tc.getRed(), tc.getGreen(), tc.getBlue()));

				g2.fillArc(angularRefX - 50, angularRefY - 50, 100, 100, (int) (angularAngle1 * 180.0f / Math.PI),
						(int) ((angularAngle2 - angularAngle1) * 180.0f / Math.PI));

			}

		}

		// axis histograms
		if (comp.getBoolPreference("histogram")) {
			int bins = comp.getIntPreference("histogramBins");
			float stepy = (float) height / bins;

			g2.setStroke(new BasicStroke(1.0f));

			for (int i = 0; i < numDimensions; i++) {
				float curx = i * stepx;

				// if (i > 0)
				curx -= stepx / 4;
				// if (i == (numDimensions-1)) curx -= stepx/4;

				g2.setColor(new Color(1.0f, 1.0f, 1.0f, 0.8f));
				g2.fillRect((int) curx, 0, stepx / 2, height);
				g2.setColor(new Color(0.0f, 0.0f, 0.0f, 0.4f));
				g2.drawRect((int) curx, 0, stepx / 2, height);

				float xscale = comp.getAxisScale(i);
				float xoffset = comp.getAxisOffset(i);

				// include records right at the border
				xoffset -= xscale * 0.00005f;
				xscale += xscale * 0.0001f;

				int j;
				int baseWidth = 0;

				// TODO: this is not nice... we should cache the float
				// calculations
				if (comp.getIntPreference("histogramWidth") != ParallelDisplay.HISTO_TOTALREC) {
					if (comp.getIntPreference("histogramWidth") == ParallelDisplay.HISTO_BINREC) {
						for (j = 0; j < bins; j++) {
							float upper = xoffset + (j * stepy / height)
							* xscale;
							float lower = xoffset + ((j + 1) * stepy / height)
							* xscale;
							int count = comp.getNumRecordsInRange(i, Math.min(
									lower, upper), Math.max(lower, upper));

							if (count > baseWidth)
								baseWidth = count;
						}
					} else if (comp.getIntPreference("histogramWidth") == ParallelDisplay.HISTO_BRUSHREC) {
						for (j = 0; j < bins; j++) {
							float upper = xoffset + (j * stepy / height)
							* xscale;
							float lower = xoffset + ((j + 1) * stepy / height)
							* xscale;
							int count = comp.getNumBrushedInRange(i, Math.min(
									lower, upper), Math.max(lower, upper));

							if (count > baseWidth)
								baseWidth = count;
						}
					} else { // unknown preference
						baseWidth = comp.getNumRecords();
					}
				} else {
					baseWidth = comp.getNumRecords();
				}

				for (j = 0; j < bins; j++) {

					float upper = xoffset + (j * stepy / height) * xscale;
					float lower = xoffset + ((j + 1) * stepy / height) * xscale;
					float count = comp.getNumRecordsInRange(i, Math.min(lower,
							upper), Math.max(lower, upper));
					float hwidth = ((count / baseWidth) * stepx / 2);

					if (hwidth > stepx / 2) {
						hwidth = stepx / 2;
					}

					g2.setColor(new Color(0.0f, 0.0f, 0.0f, 0.4f));
					g2.fillRect((int) (curx + stepx / 4 - hwidth / 2),
							(int) (j * stepy), (int) hwidth, (int) stepy + 1);

					count = comp.getNumBrushedInRange(i,
							Math.min(lower, upper), Math.max(lower, upper));
					hwidth = ((count / baseWidth) * stepx / 2);

					g2.setColor(new Color(0.0f, 0.0f, 0.0f, 0.6f));
					g2.fillRect((int) (curx + stepx / 4 - hwidth / 2),
							(int) (j * stepy), (int) hwidth, (int) stepy + 1);

				}

			}
		}

		// hovering over record
		if ((comp.getBoolPreference("hoverLine")) && (hoverRecord != -1)) {
			// System.out.println("Painting record " + i);

			// Color col = new Color(1.0f, 1.0f, 0.8f);
			Font oldfont = g2.getFont();
			Font newfont = new Font(oldfont.getName(), Font.PLAIN, oldfont
					.getSize() - 2);
			g2.setFont(newfont);

			GeneralPath rPath = new GeneralPath();
			float yval = getYValue(hoverRecord, 0, comp);
			rPath.moveTo(0, yval);

			for (int j = 1; j < numDimensions; j++) {
				yval = getYValue(hoverRecord, j, comp);
				rPath.lineTo(stepx * j, yval);
			}

			g2.setStroke(new BasicStroke(2.5f));
			g2.draw(rPath);

			g2.setStroke(new BasicStroke(1.5f));
			g2.setColor(Color.red);
			g2.draw(rPath);

			g2.setFont(oldfont);
		}

		if ((comp.getBoolPreference("hoverText")) && (hoverRecord != -1)) {
			Color col = new Color(1.0f, 1.0f, 0.8f);
			for (int j = 0; j < numDimensions; j++) {
				float yval = getYValue(hoverRecord, j, comp);
				drawTooltip(g2, comp.getAxisLabel(j) + "=\n"
						+ comp.getValue(hoverRecord, j), stepx * j, (int) yval,
						col);
			}
		}

		// dragging axis
		if (dragAxis) {
			AlphaComposite ac = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.7f);
			g2.setComposite(ac);

			g2.setStroke(new BasicStroke(0.5f));
			g2.drawLine(dragX - borderH, 0, dragX - borderH, height);
		}

		// tooltips
		if ((comp.getBoolPreference("hoverLine")) && (metaText != null)) {
			drawTooltip(g2, metaText, metaX, metaY + 10, new Color(0.7f, 0.7f,
					1.0f));
		}

		g2.translate(-borderH, -borderV);

		//	int numPixels = bufferImg.getHeight() * bufferImg.getWidth();
		//	System.out.println();

		if (alphaFlag == true && alpha < 1) {
			switch(renderMode) {
			case INITIAL:
				renderMode = RenderMode.DRAFT;
				break;
			case DRAFT:
				renderMode = RenderMode.FINAL;
				break;
			case FINAL:
				alpha = alpha + stepSize ;
				renderThread.setAlpha(alpha);
				comp.deepRepaint = true;
				renderMode = RenderMode.INITIAL;
				comp.repaint();
			}
		}


		if(numDimensions>1)
			pixelBasedEntropyArray=new float[numDimensions-1];
			for (int dimensionNumber = 0; dimensionNumber < (numDimensions - 1); dimensionNumber++) 
			{

				double entropy = computeEntropy(dimensionNumber);
//				System.out.println("Entropy  " + entropy);
	            pixelBasedEntropyArray[dimensionNumber]=(float)entropy;


			}
	}

	public void clearImage(){

		renderThread.reset();

	}


//	public int getMaxOverplotting(ParallelDisplay comp) {

//		int overplotting = 0;
//		for (int i = 0; i < comp.getModel().getNumDimensions(); i++) {
//			for (int j = i + 1; j < comp.getModel().getNumDimensions(); j++) {

//				overplotting = comp.getModel().getAxisPair(i, j, comp).getOverPlotting(comp.getHeight() - 2 * comp.getBorderV());
//				System.out.println("Number of bins less than median  " + overplotting);
				// if(overplotting>k)
				// k=overplotting;

//			}
			// maxOverplotting=k;
//		}
//		return overplotting;
//
//	}

	/**
	 * Helper function to trigger rendering of a given region.
	 */
	void renderRegion(int startAxis, int stopAxis) {
		if (startAxis < 0)
			startAxis = 0;
		if (stopAxis >= numDimensions)
			stopAxis = numDimensions - 1;

		renderThread.setRegion(startAxis, stopAxis);
		renderThread.render();

		if (brushImg != null) {
			// if we do this, we have to add another buffer img for the brush in
			// paint()
			// brushThread.setRegion(startAxis, stopAxis);
			brushThread.render();
		}
	}

	/**
	 * Helper function to trigger rendering of the whole display.
	 */
	void renderAll() {
		renderRegion(0, numDimensions - 1);
	}

	/**
	 * Helper function to trigger rendering of the brush image.
	 */
	void renderBrush() {
		brushThread.setRegion(0, numDimensions - 1);
		brushThread.render();
	}

	/**
	 * Draws the given record on the given Graphics object. This method is
	 * called by the RenderThread and can be overridden to modify the look of
	 * the rendering.
	 * 
	 * @param g2
	 *            The Graphics2D object to draw the record on.
	 * @param comp
	 *            The ParallelDisplay component to get the record data from.
	 * @param num
	 *            The id of the record to draw.
	 * @param startAxis
	 *            The start axis to draw from.
	 * @param stopAxis
	 *            The stop axis to draw to.
	 */
	void drawRecord(Graphics2D g2, ParallelDisplay comp, int num, int startAxis, int stopAxis) {

	    HashMap<Integer, Float> pixelDataMapPerDimension = null;
	    HashMap<Integer, HashMap<Integer, Float>> pixelDataMapForDataset = comp.getMap();
	    		
		float v1 =0;
		float v2 =0;
		GeneralPath rPath = new GeneralPath();
		rPath.moveTo(stepx * startAxis, getYValue(num, startAxis, comp));

		v1 = getYValue(num, startAxis, comp);
		
		

		
		for (int j = startAxis + 1; j <= stopAxis; j++) {
			v2 = getYValue(num, j, comp);
			//v2 = getYValue(num, stopAxis, comp);
			//colorcode
		//	g2.setColor(getRecordColor(v1, v2, parallelDisplay.getHeight()));
			g2.setColor(new Color(0.5f,0.5f,0.5f, 0.6f));
			
			rPath.lineTo(stepx * j, getYValue(num, j, comp));
			
			
			if(comp.getMap().get(j) == null)
				pixelDataMapForDataset.put(j, pixelDataMapPerDimension = new HashMap<Integer, Float>());

			pixelDataMapPerDimension = pixelDataMapForDataset.get(j);
			pixelDataMapPerDimension.put(num, v1);

			pixelDataMapPerDimension = null;
		}

		comp.setMap(pixelDataMapForDataset);
		g2.draw(rPath);
	}
	
	 protected Color getRecordColor(float point1, float point2, int numBins){

			
           float val = point1+point2;
           if(val<0)
        	   val= val*(-1);

			float norm = (float)(((val)/(float)(2*numBins)));
			float mult[] = {RECORD_COLOR_HIGH[0] * norm + RECORD_COLOR_LOW[0]*(1-norm), RECORD_COLOR_HIGH[1] * norm + RECORD_COLOR_LOW[1]*(1-norm), 
					RECORD_COLOR_HIGH[2] * norm + RECORD_COLOR_LOW[2]*(1-norm), 0.4f};

            Color color = new Color(mult[0], mult[1], mult[2]);

			return color;

		}

	/**
	 * Helper function to draw a "tooltip" on the given graphics object.
	 * 
	 * @param g2
	 *            The Graphics2D Object to draw on.
	 * @param text
	 *            The (multiline) text of the tooltip.
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @param col
	 *            The background color.
	 */
	private void drawTooltip(Graphics2D g2, String text, int x, int y, Color col) {
		int i;
		int mheight, mwidth = 0;
		int numLines, lineHeight;

		StringTokenizer tok = new StringTokenizer(text, "\n");
		numLines = tok.countTokens();
		String lines[] = new String[numLines];

		for (i = 0; i < numLines; i++) {
			lines[i] = tok.nextToken();

			int tempwidth = g2.getFontMetrics().stringWidth(lines[i]) + 6;
			if (tempwidth > mwidth)
				mwidth = tempwidth;
		}

		lineHeight = g2.getFontMetrics().getHeight();
		mheight = numLines * lineHeight + 2;

		x += 10;
		y += 10;
		if (x + mwidth > width)
			x -= (mwidth + 20);

		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				0.7f);
		g2.setComposite(ac);

		g2.setStroke(new BasicStroke(0.5f));
		g2.setColor(new Color(0.2f, 0.2f, 0.2f));
		g2.drawRect(x, y, mwidth, mheight);
		g2.setColor(col);
		g2.fillRect(x + 1, y + 1, mwidth - 1, mheight - 1);

		g2.setColor(Color.black);

		ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
		g2.setComposite(ac);

		for (i = 0; i < numLines; i++) {
			g2.drawString(lines[i], x + 3, y + (i + 1) * lineHeight - 4);
		}

	}

	/**
	 * Helper function to draw an arrow.
	 * 
	 * @param g2
	 *            The Graphics2D Object to draw on.
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @param size
	 *            The size in pixels.
	 * @param horizontal
	 *            If true, the arrow is drawn horizontally, if false vertically.
	 * @param topright
	 *            If true, the arrowhead is top/right, if false bottom/left.
	 */
	private void drawArrow(Graphics2D g2, int x, int y, int size,
			boolean horizontal, boolean topright) {

		if (horizontal) {

			g2.drawLine(x - size / 2, y, x + size / 2, y);

			if (topright) {
				g2.drawLine(x + size / 4, y - size / 4, x + size / 2, y);
				g2.drawLine(x + size / 4, y + size / 4, x + size / 2, y);
			} else {
				g2.drawLine(x - size / 4, y - size / 4, x - size / 2, y);
				g2.drawLine(x - size / 4, y + size / 4, x - size / 2, y);
			}
		} else {

			g2.drawLine(x, y - size / 2, x, y + size / 2);

			if (topright) {
				g2.drawLine(x + size / 4, y - size / 4, x, y - size / 2);
				g2.drawLine(x - size / 4, y - size / 4, x, y - size / 2);
			} else {
				g2.drawLine(x + size / 4, y + size / 4, x, y + size / 2);
				g2.drawLine(x - size / 4, y + size / 4, x, y + size / 2);
			}
		}
	}

	int[] computeImageHistogram(int currentDimension) {

		// System.out.println("Testing"+ bufferImg.getHeight());
//		int imageWidth = bufferImg.getWidth();
		int imageHeight = bufferImg.getHeight();
		
		double sumOfColorVal = 0;
//		int numPixels = imageHeight * imageWidth;
		int sizeOfArray = 256;
		int colorHistogramArray[] = new int[sizeOfArray];
		

		// calculate the sum of all the pixel values for each pixel
		/*
		 * for(int x=0;x<imageWidth;x++){ for(int y=0;y<imageHeight;y++){
		 * 
		 * int colorVal=(bufferImg.getRGB(x, y)&0xFF);
		 * colorValArray[colorVal]++;
		 * 
		 * sumOfColorVal=sumOfColorVal+colorValArray[colorVal];
		 * //System.out.println("Testing"+ sumOfColorVal);
		 * 
		 * }
		 * 
		 * }
		 */

		int upperBoundaryOfRegion = (currentDimension + 1) * stepx;
		int lowerBoundaryOfRegion = currentDimension * stepx;
		for (int x = lowerBoundaryOfRegion; x < upperBoundaryOfRegion; x++) {
		
			for (int y = 0; y < imageHeight; y++) {

			   int colorVal = (bufferImg.getRGB(x, y) & (0xFF));
				
				
//				int Rval = bufferImg.getRGB(x, y)<<16;
//				int Gval = bufferImg.getRGB(x, y)<<8;
//				int Bval = bufferImg.getRGB(x, y);
//				
//				int colorVal = (Rval | Gval | Bval)& (0xFF);
				colorHistogramArray[colorVal]++;

				sumOfColorVal = sumOfColorVal + colorHistogramArray[colorVal];

			}

		}
		// Once the colorHistogram is computed for a region, compute the entropy
		//computeEntropy(numPixels);
		return colorHistogramArray;
	}
	
	// once the sum is computed divide the pixel value in each pixel by the sum
	// and calculate Shannon entropy 
	public double computeEntropy(int currentDimension) {

		int colorHistogramArray[] = computeImageHistogram(currentDimension);
		
		double numPixels = (bufferImg.getWidth()/(numDimensions-1))*bufferImg.getHeight();
		
		double sumOfEntropy = 0;
		double probabilityValue = 0;
		double logprobabilityValue = 0;
		for (int i = 0; i < colorHistogramArray.length; i++) {
			probabilityValue = colorHistogramArray[i] / numPixels;

			if (colorHistogramArray[i] > 0)
				logprobabilityValue = (Math.log(numPixels
						/ (colorHistogramArray[i])) / AxisPair.LOG_BASE_2);

			double entropy = (probabilityValue * logprobabilityValue);

			sumOfEntropy = sumOfEntropy + entropy;

		}
		return sumOfEntropy;

	}
	
	/**
	 * Helper function, returns the y value (on screen) for a given record.
	 * Scale factors and translation is applied.
	 * 
	 * @param record
	 *            The recordnumber.
	 * @param axis
	 *            The axis to calculate the y value for.
	 * @param comp
	 *            our "parent" component.
	 */
	private float getYValue(int record, int axis, ParallelDisplay comp) {
		float value=1;
		if(numDimensions!=0)
		{

			value= comp.getValue(record, axis);

			value -= comp.getAxisOffset(axis);
			value *= (comp.getHeight() - 2 * borderV) / comp.getAxisScale(axis);
		}
		return value;


	}

	// record old coordinates for dragging
	int oldMouseX, oldMouseY;

	int activeAxis = -1;

	float oldScale, oldOffset;

	// 0.0 - 1.0 value of loacion of click on axis
	float clickValue;

	// actual value of point clicked on axis
	float clickAxisValue;

	int clickModifiers;

	boolean tempBrushSwap = false;

	/**
	 * Invoked when the mouse exits the component.
	 * 
	 * @param e
	 *            The mouse event.
	 */
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Invoked when a mouse button has been released on a component. Checks if
	 * something has been dragged and finishes the drag process.
	 * 
	 * @param e
	 *            The mouse event.
	 */
	public void mouseReleased(MouseEvent e) {
		ParallelDisplay comp = (ParallelDisplay) e.getComponent();

		// System.out.println("mouse released");

		if (e.isPopupTrigger()) {
			// System.out.println("showing popup...");
			comp.popupMenu.show(comp, e.getX(), e.getY());
			return;
		}

		switch (comp.getEditMode()) {
		case ParallelDisplay.REORDER:
			dragAxis = false;
			break;
		case ParallelDisplay.BRUSH:
			if (!angularPhase1) {
				inBrush = false;
				inAngularBrush = false;
				hoverAxis = -1;

				comp.setCurrentBrush(tempBrush);
				tempBrushSwap = true;
			}
			break;
		}
	}

	private int brushmode = 0;

	private static final int BRUSH_NORMAL = 0;

	private static final int BRUSH_ADD = 1;

	private static final int BRUSH_SUBTRACT = 2;

	private static final int BRUSH_INTERSECT = 3;

	private boolean inAngularBrush = false;

	private boolean angularPhase1 = false;

	private int angularRefX = 0;

	private int angularRefY = 0;

	private int angularCurX = 0;

	private int angularCurY = 0;

	private int angularStartX = 0;

	private int angularStartY = 0;

	private float angularAngle1 = 0.0f;

	private float angularAngle2 = 0.0f;

	private int angularRegion = 0;

	private int hoverRegion = 0;

	private ParallelDisplay parallelDisplay;



	/**
	 * Invoked when a mouse button has been pressed on a component. Checks if
	 * the user starts dragging something.
	 * 
	 * @param e
	 *            The mouse event.
	 */
	public void mousePressed(MouseEvent e) {
		ParallelDisplay comp = (ParallelDisplay) e.getComponent();

		if (e.isPopupTrigger()) {
			comp.popupMenu.show(comp, e.getX(), e.getY());
			return;
		}

		oldMouseX = e.getX();
		oldMouseY = e.getY();

		activeAxis = hoverAxis;

		clickModifiers = e.getModifiers();

		if (activeAxis != -1) {

			oldScale = comp.getAxisScale(activeAxis);
			oldOffset = comp.getAxisOffset(activeAxis);

			clickValue = ((float) oldMouseY - borderV)
			/ (comp.getHeight() - 2 * borderV);
			clickAxisValue = comp.getAxisOffset(activeAxis) + clickValue
			* comp.getAxisScale(activeAxis);
		}

		switch (comp.getEditMode()) {
		case ParallelDisplay.REORDER:
			dragAxis = true;
			break;
		case ParallelDisplay.BRUSH:
			inBrush = true;

			if (inAngularBrush && angularPhase1) {
				angularPhase1 = false;

				angularStartX = angularCurX;
				angularStartY = angularCurY;
			} else {
				if (activeAxis != -1) {
					brushHoverStart = oldMouseY - borderV;
					brushHoverEnd = oldMouseY - borderV;
					brushHoverX = oldMouseX - borderH;
					inAngularBrush = false;
				} else {
					inAngularBrush = true;
					angularPhase1 = true;
					angularRefX = e.getX() - borderH;
					angularRefY = e.getY() - borderV;
					angularCurX = angularRefX;
					angularCurY = angularRefY;
					angularRegion = hoverRegion;
				}
			}

			dragBrush = new Brush(comp.getNumRecords(), comp
					.getColorPreference("brushColor"));

			if (e.isControlDown()) {
				brushmode = BRUSH_INTERSECT;
			} else if (e.isShiftDown()) {
				brushmode = BRUSH_ADD;
			} else if (e.isAltDown()) {
				brushmode = BRUSH_SUBTRACT;
			} else {
				brushmode = BRUSH_NORMAL;
			}

			hoverRecord = -1;

			if (brushImg == null) {
				brushImg = new BufferedImage(comp.getWidth(), comp.getHeight(),
						BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D ig = brushImg.createGraphics();
				// fill with transparent white
				ig.setColor(new Color(1.0f, 1.0f, 1.0f, 0.0f));
				ig.fillRect(0, 0, comp.getWidth(), comp.getHeight());
			}
		}

	}

	/**
	 * Invoked when a mouse button is pressed on a component and then dragged.
	 * Mouse drag events will continue to be delivered to the component where
	 * the first originated until the mouse button is released (regardless of
	 * whether the mouse position is within the bounds of the component).
	 * 
	 * Depending on the current mode, this method performs scaling, translating
	 * or reordering of axes.
	 * 
	 * @param e
	 *            The mouse event.
	 */
	public void mouseDragged(MouseEvent e) {
		ParallelDisplay comp = (ParallelDisplay) e.getComponent();

		int mouseX = e.getX();
		int mouseY = e.getY();

		setMetaInfo(null, 0, 0);

		switch (comp.getEditMode()) {
		case ParallelDisplay.SCALE:
			if (activeAxis != -1) {
				float way = ((float) (oldMouseY - mouseY))
				/ (comp.getHeight() - 2 * borderV);
				comp.setAxisScale(activeAxis, oldScale + (way * oldScale));
				float newValue = clickValue
				* (comp.getAxisScale(activeAxis) - oldScale);
				comp.setAxisOffset(activeAxis, oldOffset - newValue);

				renderRegion(activeAxis - 1, activeAxis + 1);
			}
			break;
		case ParallelDisplay.TRANSLATE:
			if (activeAxis != -1) {
				float way = ((float) (oldMouseY - mouseY))
				/ (comp.getHeight() - 2 * borderV);
				way *= comp.getAxisScale(activeAxis);
				comp.setAxisOffset(activeAxis, oldOffset + way);

				renderRegion(activeAxis - 1, activeAxis + 1);
			}
			break;
		case ParallelDisplay.REORDER:
			if (activeAxis != -1) {
				int deltaX = mouseX - oldMouseX;
				int num = activeAxis + deltaX / stepx;

				if (num < 0)
					num = 0;
				if (num >= numDimensions)
					num = numDimensions - 1;

				dragX = mouseX;

				if (activeAxis != num) {
					comp.swapAxes(activeAxis, num);

					// System.out.println("setting repaint axes: " +
					// (Math.min(num,activeAxis) - 1) + ", " +
					// (Math.max(num,activeAxis) + 1));

					renderRegion(Math.min(num, activeAxis) - 1, Math.max(num,
							activeAxis) + 1);

					activeAxis = num;
					hoverAxis = num;
					oldMouseX = stepx * num + borderH;
				}
				// to display hoverAxis
				comp.repaint();
			}
			break;
		case ParallelDisplay.BRUSH:
			if (!inAngularBrush) {
				brushHoverEnd = mouseY - borderV;
				float releaseValue = ((float) mouseY - borderV)
				/ (comp.getHeight() - 2 * borderV);
				releaseValue = comp.getAxisOffset(activeAxis) + releaseValue
				* comp.getAxisScale(activeAxis);
				float lowerBound = Math.min(clickAxisValue, releaseValue);
				float upperBound = Math.max(clickAxisValue, releaseValue);
				boolean doSoft = comp.getBoolPreference("softBrush");
				float radius = 0.0f;
				int ids[];
				if (doSoft) {
					radius = comp.getFloatPreference("brushRadius")
					* (upperBound - lowerBound);
					if (radius == 0.0f) {
						System.out.println("radius is zero");
						doSoft = false;
					}
					ids = comp.getRecordsByValueRange(activeAxis, lowerBound
							- radius, upperBound + radius);
				} else {
					ids = comp.getRecordsByValueRange(activeAxis, lowerBound,
							upperBound);
				}
				int id = 0;
				for (int i = 0; i < comp.getNumRecords(); i++) {
					float brushVal = 0.0f;
					if ((ids.length > 0) && (i == ids[id])) {
						// record is inside brush region

						brushVal = 1.0f;

						if (doSoft) {
							float val = comp.getValue(i, activeAxis);
							if (val < lowerBound) {
								brushVal = 1.0f - (-val + lowerBound) / radius;
							}
							if (val > upperBound) {
								brushVal = 1.0f - (val - upperBound) / radius;
							}
						}

						if (id < ids.length - 1)
							id++;

					}

					dragBrush.setBrushValue(i, brushVal);
				}

			} else { // angular brushing
				angularCurX = mouseX - borderH;
				angularCurY = mouseY - borderV;
				if (!angularPhase1) {
					float maxratio = (angularRefY - angularStartY)
					/ (float) (angularStartX - angularRefX);
					float tempratio = (angularRefY - angularCurY)
					/ (float) (angularCurX - angularRefX);
					float minratio = Math.min(maxratio, tempratio);
					maxratio = Math.max(maxratio, tempratio);

					angularAngle1 = (float) Math.atan(maxratio);
					angularAngle2 = (float) Math.atan(minratio);

					// System.out.println("a1: " + angularAngle1 + " a2: " +
					// angularAngle2);

					for (int i = 0; i < dragBrush.getNumValues(); i++) {
						float val1 = (comp.getValue(i, angularRegion) - comp
								.getAxisOffset(angularRegion))
								/ comp.getAxisScale(angularRegion)
								* (comp.getHeight() - 2 * borderV);
						float val2 = (comp.getValue(i, angularRegion + 1) - comp
								.getAxisOffset(angularRegion + 1))
								/ comp.getAxisScale(angularRegion + 1)
								* (comp.getHeight() - 2 * borderV);
						float ratio = (val1 - val2) / stepx;

						// System.out.println("val1: " + val1 + " val2: " + val2
						// + " ratio: " + ratio + "minratio: " + minratio +
						// " maxratio: " + maxratio);
						// System.out.println("axis1: " + angularRegion);
						if (ratio >= minratio && ratio <= maxratio) {
							// System.out.println("setting brush num " + i +
							// " ratio: " + ratio + " minratio: " + minratio +
							// " maxratio: " + maxratio);
							dragBrush.setBrushValue(i, 1.0f);
						} else {
							dragBrush.setBrushValue(i, 0.0f);
						}
					}
				}
			}

			if (brushmode == BRUSH_INTERSECT) {
				tempBrush = comp.getCurrentBrush().intersect(dragBrush);
			} else if (brushmode == BRUSH_ADD) {
				tempBrush = comp.getCurrentBrush().add(dragBrush);
			} else if (brushmode == BRUSH_SUBTRACT) {
				tempBrush = comp.getCurrentBrush().subtract(dragBrush);
			} else {
				tempBrush = dragBrush;
			}

			comp.fireBrushModified(tempBrush);

			if (tempBrush.getNumBrushed() > 0) {
				brushThread.setBrush(tempBrush);
				renderBrush();
				// to see brush line in realtime
			}
			comp.repaint();
			break;

		}

	}

	/**
	 * Invoked when the mouse has been clicked on a component.
	 * 
	 * Checks if the click hit an arrow and inverts the corresponding axis.
	 * 
	 * @param e
	 *            The mouse event.
	 */
	public void mouseClicked(MouseEvent e) {

		ParallelDisplay comp = (ParallelDisplay) e.getComponent();

		// arrow clicked or invert mode
		if ((comp.getEditMode() == ParallelDisplay.INVERT)
				|| (e.getY() <= 25 && e.getY() > 12)) {
			if (hoverAxis != -1) {
				comp.setAxisOffset(hoverAxis, comp.getAxisOffset(hoverAxis)
						+ comp.getAxisScale(hoverAxis));
				comp.setAxisScale(hoverAxis, comp.getAxisScale(hoverAxis) * -1);

				renderRegion(activeAxis - 1, activeAxis + 1);

			}
		}

	}

	/**
	 * Invoked when the mouse enters a component.
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Invoked when the mouse button has been moved on a component (with no
	 * buttons no down).
	 * 
	 * Displays tooltips if mouse is hovering over axes or records.
	 * 
	 * @param e
	 *            The mouse event.
	 */
	public void mouseMoved(MouseEvent e) {

		ParallelDisplay parallelDisplay = (ParallelDisplay) e.getComponent();

		if (inBrush) {
			if (inAngularBrush && angularPhase1) {
				angularCurX = e.getX() - borderH;
				angularCurY = e.getY() - borderV;
				parallelDisplay.repaint();
			}
		} else {
			int mousex = e.getX() - borderH;
			int mousey = e.getY() - borderV;

			int oldAxis = hoverAxis;
			int oldRecord = hoverRecord;

			hoverAxis = -1;

			for (int i = 0; i < numDimensions; i++) {
				if ((mousex > (i * stepx - 6)) && (mousex < (i * stepx + 6))) {
					hoverAxis = i;
				}
				if ((mousex > i * stepx) && (mousex < (i + 1) * stepx)) {
					parallelDisplay.popupMenu.setTargetRegion(i + 1);
					hoverRegion = i;
				}
			}

			if (mousex < 0)
				parallelDisplay.popupMenu.setTargetRegion(0);

			hoverRecord = getRecordByCoordinates(mousex, mousey, parallelDisplay);

			if ((oldAxis != hoverAxis) || (oldRecord != hoverRecord)) {
				if (hoverAxis != -1) {
					setMetaInfo(parallelDisplay.getAxisLabel(hoverAxis), mousex, mousey);

					switch (parallelDisplay.getEditMode()) {
					case ParallelDisplay.REORDER:
						parallelDisplay.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
						break;
					case ParallelDisplay.SCALE:
						parallelDisplay.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
						break;
					case ParallelDisplay.TRANSLATE:
						parallelDisplay.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
						break;
					case ParallelDisplay.INVERT:
						parallelDisplay.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
						break;
					case ParallelDisplay.BRUSH:
						parallelDisplay.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
						break;
					}

				} else {
					setMetaInfo(null, 0, 0);

					parallelDisplay.resetCursor();
				}

				if (hoverRecord != -1) {
					setMetaInfo(parallelDisplay.getRecordLabel(hoverRecord), mousex,
							mousey);
				}

				parallelDisplay.repaint();
			}
			// TODO: this will break DisplayMetricsBarsPanel when number of metrics changes
			float displayMetrics[]=new float[Metrics.values().length];
			if (oldAxis != hoverAxis && hoverAxis != -1) {
				// System.out.println("hoverAxis = "+hoverAxis);
				if (hoverAxis < parallelDisplay.axes.length - 1) {
					// get the dimension of the hoveraxis as this is sensitive to reordering

					int numBins = parallelDisplay.getHeight() - 2*parallelDisplay.getBorderV();

					int dim1 = parallelDisplay.axes[hoverAxis].dimension;
					int dim2 = parallelDisplay.axes[hoverAxis+1].dimension;


//					int numCrossings = (int)parallelDisplay.getModel().getAxisPair(dim1, dim2, parallelDisplay).getNumCrossings(numBins).getValue();
//					displayMetrics[Metrics.NumCrossings.ordinal()] = (float)numCrossings;
//
//					float angleOfCrossings = (int)parallelDisplay.getModel().getAxisPair(dim1, dim2, parallelDisplay).getAngleOfCrossingMedian(numBins).getValue();
//					System.err.println("angleOfCrossings median: "+angleOfCrossings);
//					displayMetrics[Metrics.AngleOfCrossings.ordinal()] = (float)angleOfCrossings;

//					float parallelism = parallelDisplay.getModel().getAxisPair(dim1, dim2, parallelDisplay).getParallelism(numBins).getValue();
//					displayMetrics[Metrics.Parallelism.ordinal()] = parallelism;

					float mutualInfo = parallelDisplay.getModel().getAxisPair(hoverAxis, hoverAxis+1, parallelDisplay).getMutualInfo(numBins).getValue();
			//		displayMetrics[Metrics.MutualInformation.ordinal()] = mutualInfo;
				//	System.err.println( " mutualInfo  "   + mutualInfo);
					
					
					
					//					System.err.println("Values: " + ParallelDisplay.getInstance().getModel().getNumRecords() + ", crossings: " + numCrossings);

//					float convergence_divergence = parallelDisplay.getModel().getAxisPair(dim1, dim2, parallelDisplay).getConvergence_Divergence(numBins,5).getValue();
//					displayMetrics[Metrics.Convergence_Divergence.ordinal()] = convergence_divergence;
//
//					float degreeOfOverplotting = parallelDisplay.getModel().getAxisPair(dim1, dim2, parallelDisplay).getDegreeOfOverPlotting(numBins).getValue();
//					displayMetrics[Metrics.Overplotting.ordinal()] = degreeOfOverplotting;
					
					float binEntropy = parallelDisplay.getModel().getAbsoluteEntropy(numBins, hoverAxis);
					float conditionalEntropy = parallelDisplay.getModel().getAxisPair(dim1, dim2, parallelDisplay).computeDistanceEntropy(numBins);
					float pixelBasedEntropy = parallelDisplay.getDisplayUI().pixelBasedEntropyArray[hoverAxis];
							    System.err.println("PE  " +pixelBasedEntropy + "   "  + "Conditional Entropy " + conditionalEntropy);
				//	displayMetrics[Metrics.PixelBasedEntropy.ordinal()] = pixelBasedEntropy;
					
//					float distanceEntropy = parallelDisplay.getModel().getAxisPair(dim1, dim2, parallelDisplay).getDistanceEntropy(numBins);
//				    System.err.println("Distance Entropy " + distanceEntropy);

					//HistogramPanel.instance.setAxes(hoverAxis, hoverAxis+1, borderV);
					//ScatterplotPanel.scatterInstance.setAxes(hoverAxis, hoverAxis+1);
					//	DisplayMetricBarsPanel.getInstance().setMetric(hoverAxis, displayMetrics);

					int numBinsDegree= 	DisplayMetricBarsPanel.getInstance().getHeight();

					int degree[] = parallelDisplay.getModel().getAxisPair(dim1, dim2, parallelDisplay).getDegree(numBinsDegree, true);
					DisplayMetricBarsPanel.getInstance().setHistArray( dim1, degree);


					//OptimizationPanel.getInstance().setComboBoxItems(-1);
				}
			}

		}

	}

	/**
	 * Helper method to display a tooltip on hover.
	 */
	private void setMetaInfo(String text, int x, int y) {
		metaText = text;
		metaX = x;
		metaY = y;
	}

	/**
	 * Returns the record that goes through the screen coordinates x,y. The
	 * first record that is found is returned.
	 * 
	 * @param x
	 *            The x screen coordinate.
	 * @param y
	 *            The y screen coordinate.
	 * @param comp
	 *            The "parent" component.
	 * 
	 * @return The recordnumber of the first record found passing through the
	 *         given point.
	 */
	public int getRecordByCoordinates(int x, int y, ParallelDisplay comp) {
		for (int i = 0; i < numDimensions - 1; i++) {
			if ((x >= i * stepx) && (x < (i + 1) * stepx)) {
				float part = (x - i * stepx) / (float) stepx;
				for (int j = 0; j < numRecords; j++) {
					float recVal = (1 - part) * getYValue(j, i, comp) + part
					* getYValue(j, i + 1, comp);

					if (Math.abs(recVal - y) < 3.0)
						return j;
				}
				break;
			}
		}

		return -1;
	}

	public int getBorderV() {
		return borderV;
	}

	public void setAlphaParameters(boolean flag) {

		alphaFlag = true;
		alpha = .1f;
		renderThread.setAlpha(alpha);
		parallelDisplay.deepRepaint = true;
		renderMode = RenderMode.INITIAL;
	}

	public float getAlpha(){

		return alpha;
	}

}
