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

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import org.mediavirus.parvis.file.STFFile;
import org.mediavirus.parvis.model.Brush;


@SuppressWarnings("serial")
public class MainFrame extends javax.swing.JFrame implements ProgressListener, BrushListener {

	private static final String WINDOWTITLE = "Pargnostics";
	MatrixMetaView matrixView;

	/** The name of the file to load on startup. */
	//private static final String INITIALFILENAME = "data/winequality.stf";
	//	private static final String INITIALFILENAME = "data/abalone.stf";
	//private static final String INITIALFILENAME = "data/cloud.stf";
	//private static final String INITIALFILENAME = "data/parkinsons.stf";
	private static final String INITIALFILENAME = "data/winequalityNumerical.stf";

	DisplayMetricBarsPanel metricsDisplay;
	PargnosticsPanel scatterPanel;

	/** Creates new form MainFrame 
	 * @param controller */
	public MainFrame(ParameterizedDisplay parameterizedDisplay, MatrixMetaView matrixView) {

        this.matrixView = matrixView;
		initComponents();

		parallelDisplay.addProgressListener(this);
		parallelDisplay.addBrushListener(this);

		parameterizedDisplay.initialize(parallelDisplay.getModel(), parallelDisplay);
		matrixView.initialize(parallelDisplay.getModel(), parallelDisplay, parameterizedDisplay);

		//         for(BufferedImage img: p.getBufferedImageList())
		//			
		//			System.out.println(p.computeEntropy(img));


		//        BrushList brushList = new BrushList(parallelDisplay);
		//        brushList.setLocation(this.getX() + this.getWidth(), this.getY());
		//        brushList.setVisible(true);

		setSize(1000, 800);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.et
	 * @param controller 
	 */
	private void initComponents() {//GEN-BEGIN:initComponents
		menuEditGroup = new javax.swing.ButtonGroup();
		buttonEditGroup = new javax.swing.ButtonGroup();
		statusPanel = new javax.swing.JPanel();
		progressPanel = new javax.swing.JPanel();
		progressLabel = new javax.swing.JLabel();
		progressBar = new javax.swing.JProgressBar();
		timeLabel = new javax.swing.JLabel();
		quickPrefPanel = new javax.swing.JPanel();
		histogramBox = new javax.swing.JCheckBox();
		tooltipBox = new javax.swing.JCheckBox();
		hoverBox = new javax.swing.JCheckBox();
		fuzzyBrushBox = new javax.swing.JCheckBox();
		radiusField = new javax.swing.JTextField();
		toolbarPanel = new javax.swing.JPanel();
		modeBar = new javax.swing.JToolBar();
		modeLabel = new javax.swing.JLabel();
		orderButton = new javax.swing.JToggleButton();
		scaleButton = new javax.swing.JToggleButton();
		translateButton = new javax.swing.JToggleButton();
		brushButton = new javax.swing.JToggleButton();
		jSeparator2 = new javax.swing.JSeparator();
		countLabel = new javax.swing.JLabel();
		resetBrushButton = new javax.swing.JButton();
		resetAllButton = new javax.swing.JButton();
		urlBar = new javax.swing.JToolBar();
		datasourceLabel = new javax.swing.JLabel();
		urlField = new javax.swing.JTextField();
		jButton1 = new javax.swing.JButton();
		menuBar = new javax.swing.JMenuBar();
		fileMenu = new javax.swing.JMenu();
		openMenu = new javax.swing.JMenuItem();
		saveBrushItem = new javax.swing.JMenuItem();
		editMenu = new javax.swing.JMenu();
		orderMenu = new javax.swing.JRadioButtonMenuItem();
		scaleMenu = new javax.swing.JRadioButtonMenuItem();
		translateMenu = new javax.swing.JRadioButtonMenuItem();
		brushMenu = new javax.swing.JRadioButtonMenuItem();
		jSeparator1 = new javax.swing.JSeparator();
		preferencesMenu = new javax.swing.JMenuItem();
		viewMenu = new javax.swing.JMenu();
		scaleZeroMaxItem = new javax.swing.JMenuItem();
		scaleMinMaxItem = new javax.swing.JMenuItem();
		scaleMinMaxAbsItem = new javax.swing.JMenuItem();
		helpMenu = new javax.swing.JMenu();
		helpItem = new javax.swing.JMenuItem();
		aboutItem = new javax.swing.JMenuItem();
		parallelDisplay = new ParallelDisplay(matrixView);


		setLayout(new MigLayout("fill", "[grow 0][]", "[]0[]"));



		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle(WINDOWTITLE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				exitForm(evt);
			}
		});



		statusPanel.setLayout(new BorderLayout());

		statusPanel.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(), "status", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10)));
		statusPanel.setFont(new java.awt.Font("Dialog", 0, 10));
		statusPanel.setPreferredSize(new java.awt.Dimension(272, 50));
		progressPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

		progressLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		progressLabel.setText("progress:");
		progressPanel.add(progressLabel);

		progressBar.setFont(new java.awt.Font("Dialog", 0, 10));
		progressBar.setMaximumSize(new java.awt.Dimension(1000, 18));
		progressBar.setMinimumSize(new java.awt.Dimension(10, 16));
		progressBar.setPreferredSize(new java.awt.Dimension(100, 18));
		progressBar.setStringPainted(true);
		progressPanel.add(progressBar);

		timeLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		timeLabel.setText("(0.0 s)");
		progressPanel.add(timeLabel);

		statusPanel.add(progressPanel);


		quickPrefPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

		histogramBox.setFont(new java.awt.Font("Dialog", 0, 10));
		histogramBox.setText("hist.");
		histogramBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				histogramBoxActionPerformed(evt);
			}
		});

		quickPrefPanel.add(histogramBox);

		tooltipBox.setFont(new java.awt.Font("Dialog", 0, 10));
		tooltipBox.setSelected(true);
		tooltipBox.setText("tooltips");
		tooltipBox.setMargin(new java.awt.Insets(0, 2, 0, 2));
		tooltipBox.setEnabled(false);
		tooltipBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				tooltipBoxActionPerformed(evt);
			}
		});

		quickPrefPanel.add(tooltipBox);

		hoverBox.setFont(new java.awt.Font("Dialog", 0, 10));
		hoverBox.setText("line");
		hoverBox.setMargin(new java.awt.Insets(0, 2, 0, 2));
		hoverBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				hoverBoxActionPerformed(evt);
			}
		});

		quickPrefPanel.add(hoverBox);

		fuzzyBrushBox.setFont(new java.awt.Font("Dialog", 0, 10));
		fuzzyBrushBox.setSelected(true);
		fuzzyBrushBox.setText("Brush Fuzziness:");
		fuzzyBrushBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		fuzzyBrushBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				fuzzyBrushBoxActionPerformed(evt);
			}
		});

		quickPrefPanel.add(fuzzyBrushBox);

		radiusField.setFont(new java.awt.Font("Dialog", 0, 10));
		radiusField.setText(" 20 %");
		radiusField.setBorder(new javax.swing.border.LineBorder((java.awt.Color) javax.swing.UIManager.getDefaults().get("Button.select")));
		radiusField.setPreferredSize(new java.awt.Dimension(30, 17));
		radiusField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				radiusFieldActionPerformed(evt);
			}
		});

		radiusField.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusGained(java.awt.event.FocusEvent evt) {
				radiusFieldFocusGained(evt);
			}
		});

		quickPrefPanel.add(radiusField);

		statusPanel.add(quickPrefPanel, java.awt.BorderLayout.EAST);

		add(statusPanel, "south");

		toolbarPanel.setLayout(new java.awt.GridLayout(2, 0));

		modeLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		modeLabel.setText("Edit Mode: ");
		modeBar.add(modeLabel);

		orderButton.setFont(new java.awt.Font("Dialog", 0, 10));
		orderButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/reorder.gif")));
		orderButton.setSelected(true);
		orderButton.setText("Order");
		orderButton.setToolTipText("Reorder axes by dragging them across the display.");
		buttonEditGroup.add(orderButton);
		orderButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		orderButton.setMaximumSize(new java.awt.Dimension(65, 27));
		orderButton.setMinimumSize(new java.awt.Dimension(65, 27));
		orderButton.setPreferredSize(new java.awt.Dimension(65, 27));
		orderButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeOrder(evt);
			}
		});

		modeBar.add(orderButton);

		scaleButton.setFont(new java.awt.Font("Dialog", 0, 10));
		scaleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/scale.gif")));
		scaleButton.setText("Scale");
		scaleButton.setToolTipText("Scale axes by dragging up (zoom out) or down (zoom in).");
		buttonEditGroup.add(scaleButton);
		scaleButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		scaleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeScale(evt);
			}
		});

		modeBar.add(scaleButton);

		translateButton.setFont(new java.awt.Font("Dialog", 0, 10));
		translateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/move.gif")));
		translateButton.setText("Translate");
		translateButton.setToolTipText("Translate axes by dragging up or down.");
		buttonEditGroup.add(translateButton);
		translateButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		translateButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeTranslate(evt);
			}
		});

		modeBar.add(translateButton);

		brushButton.setFont(new java.awt.Font("Dialog", 0, 10));
		brushButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/brush.gif")));
		brushButton.setText("Brush");
		brushButton.setToolTipText("Translate axes by dragging up or down.");
		buttonEditGroup.add(brushButton);
		brushButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		brushButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeBrush(evt);
			}
		});

		modeBar.add(brushButton);

		jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
		modeBar.add(jSeparator2);

		countLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		countLabel.setText("0 / 0   ");
		countLabel.setMaximumSize(new java.awt.Dimension(100, 16));
		modeBar.add(countLabel);

		resetBrushButton.setFont(new java.awt.Font("Dialog", 0, 10));
		resetBrushButton.setText("Reset Brush");
		resetBrushButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				resetBrushButtonActionPerformed(evt);
			}
		});

		modeBar.add(resetBrushButton);

		resetAllButton.setBackground(new java.awt.Color(255, 153, 153));
		resetAllButton.setFont(new java.awt.Font("Dialog", 0, 10));
		resetAllButton.setText("Reset All");
		resetAllButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				resetAllButtonActionPerformed(evt);
			}
		});

		modeBar.add(resetAllButton);

		toolbarPanel.add(modeBar);

		datasourceLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		datasourceLabel.setText("Datasource: ");
		urlBar.add(datasourceLabel);

		urlField.setFont(new java.awt.Font("Dialog", 0, 10));
		urlField.setText(INITIALFILENAME);
		urlFieldActionPerformed(null);
		urlField.setMargin(new java.awt.Insets(0, 0, 0, 5));
		urlField.setMinimumSize(null);
		urlField.setPreferredSize(null);
		urlField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				urlFieldActionPerformed(evt);
			}
		});

		urlBar.add(urlField);

		jButton1.setFont(new java.awt.Font("Dialog", 0, 10));
		jButton1.setText("Load File...");
		jButton1.setMargin(new java.awt.Insets(0, 5, 0, 0));
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				openItemActionPerformed(evt);
			}
		});

		urlBar.add(jButton1);

		toolbarPanel.add(urlBar);

		add(toolbarPanel, "north");

		//set up layout for the different panels 

		metricsDisplay = new DisplayMetricBarsPanel(parallelDisplay);

		OptimizationPanel optimizationPanel = new OptimizationPanel(parallelDisplay);

		//      histogramPanel = new HistogramPanel(parallelDisplay);
		//  add(brushControlPanel, "width 200!");

		add(optimizationPanel, "width 300!");
		parallelDisplay.setOptimizationReference(optimizationPanel);
		add(parallelDisplay, "grow, wrap");

		add(optimizationPanel.getButtonBox(), "height ::200");

		add(metricsDisplay, "height ::200, growx");


		//   add(optimizationPanel.getButtonBox(), "hmin 160");
		//  scatterplotFrame.add(scatterPanel,"height min:180, growx");
		//    add(metricsDisplay, "height min:180, growx");


		//       mainBox.add(rightBox);

		//       Box histogramBox=Box.createVerticalBox();
		//       histogramBox.add(mPanel,java.awt.BorderLayout.CENTER);
		//  add(histogramPanel, "east");

		//       add(mainBox, "grow");

		//parallelDisplay.setPreferredSize(new java.awt.Dimension(800, 480));
		//  getContentPane().add(parallelDisplay, java.awt.BorderLayout.CENTER);
		menuBar.setFont(new java.awt.Font("Dialog", 0, 11));
		fileMenu.setText("File");
		openMenu.setText("Open File...");
		openMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				openItemActionPerformed(evt);
			}
		});

		fileMenu.add(openMenu);
		saveBrushItem.setText("Export Brush...");
		saveBrushItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveBrushItemActionPerformed(evt);
			}
		});

		fileMenu.add(saveBrushItem);
		menuBar.add(fileMenu);
		editMenu.setText("Edit");
		orderMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
		orderMenu.setFont(new java.awt.Font("Dialog", 0, 11));
		orderMenu.setSelected(true);
		orderMenu.setText("Reorder Axes");
		menuEditGroup.add(orderMenu);
		orderMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/reorder.gif")));
		orderMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeOrder(evt);
			}
		});

		editMenu.add(orderMenu);
		scaleMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
		scaleMenu.setFont(new java.awt.Font("Dialog", 0, 11));
		scaleMenu.setText("Scale Axis");
		menuEditGroup.add(scaleMenu);
		scaleMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/scale.gif")));
		scaleMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeScale(evt);
			}
		});

		editMenu.add(scaleMenu);
		translateMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
		translateMenu.setFont(new java.awt.Font("Dialog", 0, 11));
		translateMenu.setText("Translate Axis");
		menuEditGroup.add(translateMenu);
		translateMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/move.gif")));
		translateMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeTranslate(evt);
			}
		});

		editMenu.add(translateMenu);
		brushMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
		brushMenu.setFont(new java.awt.Font("Dialog", 0, 11));
		brushMenu.setText("Brush Records");
		menuEditGroup.add(brushMenu);
		brushMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/brush.gif")));
		brushMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeBrush(evt);
			}
		});

		editMenu.add(brushMenu);
		editMenu.add(jSeparator1);
		preferencesMenu.setFont(new java.awt.Font("Dialog", 0, 11));
		preferencesMenu.setText("Preferences...");
		preferencesMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				preferencesMenuActionPerformed(evt);
			}
		});

		editMenu.add(preferencesMenu);
		menuBar.add(editMenu);
		viewMenu.setText("View");
		scaleZeroMaxItem.setFont(new java.awt.Font("Dialog", 0, 11));
		scaleZeroMaxItem.setText("Scale Axes 0-max");
		scaleZeroMaxItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				scaleZeroMaxItemActionPerformed(evt);
			}
		});

		viewMenu.add(scaleZeroMaxItem);
		scaleMinMaxItem.setFont(new java.awt.Font("Dialog", 0, 11));
		scaleMinMaxItem.setText("Scale Axes min-max");
		scaleMinMaxItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				scaleMinMaxItemActionPerformed(evt);
			}
		});

		viewMenu.add(scaleMinMaxItem);
		scaleMinMaxAbsItem.setFont(new java.awt.Font("Dialog", 0, 11));
		scaleMinMaxAbsItem.setText("Scale Axes min-max (abs)");
		scaleMinMaxAbsItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				scaleMinMaxAbsItemActionPerformed(evt);
			}
		});

		viewMenu.add(scaleMinMaxAbsItem);
		menuBar.add(viewMenu);
		helpMenu.setText("Help");
		helpItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
		helpItem.setText("Help");
		helpMenu.add(helpItem);
		aboutItem.setText("About...");
		helpMenu.add(aboutItem);
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);

		//  OptimizationPanel oPanel = new OptimizationPanel();
		//  add(oPanel, BorderLayout.EAST);

		//   mDisplay=new MetricsDisplayPanel();
		//   add(mDisplay, BorderLayout.SOUTH);

		pack();

		parallelDisplay.setVisible(true);

		JFrame scatterFrame=new JFrame("Pargnostics");
		scatterFrame.setLayout(new BorderLayout());
		JPanel helperPanel=new JPanel();
		scatterPanel=new PargnosticsPanel(parallelDisplay,helperPanel);
		scatterFrame.add(scatterPanel,BorderLayout.CENTER);
		scatterFrame.add(helperPanel,BorderLayout.EAST);
		scatterFrame.setSize(1000, 800);
		scatterFrame.setVisible(true);
		scatterFrame.add(scatterPanel);

	}//GEN-END:initComponents

	private void preferencesMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preferencesMenuActionPerformed
		PrefsFrame pf = new PrefsFrame(parallelDisplay);
		pf.setVisible(true);
	}//GEN-LAST:event_preferencesMenuActionPerformed

	private void saveBrushItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBrushItemActionPerformed
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new javax.swing.filechooser.FileFilter(){
			public boolean accept(File f){
				return(f.isDirectory() || f.getName().endsWith(".chf"));
			}
			public String getDescription(){
				return "CHF (Channel File) Data Files";
			}
		});
		if (currentPath == null){
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		}
		else {
			chooser.setCurrentDirectory(currentPath);
		}

		int option = chooser.showSaveDialog(this);

		if (option == JFileChooser.APPROVE_OPTION) {
			if (chooser.getSelectedFile() != null){
				try {
					parallelDisplay.getCurrentBrush().writeToFile(chooser.getSelectedFile(),true);
				}
				catch(IOException ioex){
					System.out.println(ioex.getMessage());
				}
			}
		}
	}//GEN-LAST:event_saveBrushItemActionPerformed

	private void histogramBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_histogramBoxActionPerformed
		parallelDisplay.setBoolPreference("histogram", histogramBox.isSelected());
		parallelDisplay.repaint();
	}//GEN-LAST:event_histogramBoxActionPerformed

	private void fuzzyBrushBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fuzzyBrushBoxActionPerformed
		if (fuzzyBrushBox.isSelected()){
			radiusField.setEnabled(true);
			String txt = radiusField.getText();
			if (txt.indexOf('%') > -1) {
				txt = txt.substring(0,txt.indexOf('%'));
			}
			txt = txt.trim();

			int num = Integer.parseInt(txt);
			parallelDisplay.setFloatPreference("brushRadius",((float)num)/100.0f);
		}
		else {
			radiusField.setEnabled(false);
			parallelDisplay.setFloatPreference("brushRadius",0.0f);
		}

	}//GEN-LAST:event_fuzzyBrushBoxActionPerformed

	private void resetAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetAllButtonActionPerformed
		parallelDisplay.resetAll();
	}//GEN-LAST:event_resetAllButtonActionPerformed

	private void resetBrushButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBrushButtonActionPerformed
		parallelDisplay.setCurrentBrush(null);
	}//GEN-LAST:event_resetBrushButtonActionPerformed

	private void hoverBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hoverBoxActionPerformed
		if (hoverBox.isSelected()){
			tooltipBox.setEnabled(true);
			parallelDisplay.setBoolPreference("hoverText", tooltipBox.isSelected());
			parallelDisplay.setBoolPreference("hoverLine", hoverBox.isSelected());
		}
		else {
			tooltipBox.setEnabled(false);
			parallelDisplay.setBoolPreference("hoverText", false);
			parallelDisplay.setBoolPreference("hoverLine", hoverBox.isSelected());
		}
	}//GEN-LAST:event_hoverBoxActionPerformed

	private void radiusFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radiusFieldActionPerformed
		String txt = radiusField.getText();
		if (txt.indexOf('%') > -1) {
			txt = txt.substring(0,txt.indexOf('%'));
		}
		txt = txt.trim();

		int num = Integer.parseInt(txt);
		parallelDisplay.setFloatPreference("brushRadius",((float)num)/100.0f);
		radiusField.setText(" " + num + " %");
		radiusField.transferFocus();
	}//GEN-LAST:event_radiusFieldActionPerformed

	private void radiusFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_radiusFieldFocusGained
		radiusField.selectAll();
	}//GEN-LAST:event_radiusFieldFocusGained

	private void scaleMinMaxAbsItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleMinMaxAbsItemActionPerformed
		parallelDisplay.minMaxAbsScale();
	}//GEN-LAST:event_scaleMinMaxAbsItemActionPerformed

	private void scaleMinMaxItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleMinMaxItemActionPerformed
		parallelDisplay.minMaxScale();
	}//GEN-LAST:event_scaleMinMaxItemActionPerformed

	private void scaleZeroMaxItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleZeroMaxItemActionPerformed
		parallelDisplay.zeroMaxScale();
	}//GEN-LAST:event_scaleZeroMaxItemActionPerformed

	private void setEditModeTranslate(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setEditModeTranslate
		parallelDisplay.setEditMode(ParallelDisplay.TRANSLATE);
		translateButton.setSelected(true);
		translateMenu.setSelected(true);
	}//GEN-LAST:event_setEditModeTranslate

	private void setEditModeScale(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setEditModeScale
		parallelDisplay.setEditMode(ParallelDisplay.SCALE);
		scaleButton.setSelected(true);
		scaleMenu.setSelected(true);
	}//GEN-LAST:event_setEditModeScale

	private void setEditModeOrder(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setEditModeOrder
		parallelDisplay.setEditMode(ParallelDisplay.REORDER);
		orderButton.setSelected(true);
		orderMenu.setSelected(true);
	}//GEN-LAST:event_setEditModeOrder

	private void setEditModeBrush(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setEditModeBrush
		parallelDisplay.setEditMode(ParallelDisplay.BRUSH);
		brushButton.setSelected(true);
		brushMenu.setSelected(true);
	}//GEN-LAST:event_setEditModeBrush

	private void urlFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_urlFieldActionPerformed
		STFFile f = new STFFile(urlField.getText());
		f.addProgressListener(this);

		try {
			f.readContents();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		parallelDisplay.setModel(f);
		setTitle("Parvis - " + f.getName());
	}//GEN-LAST:event_urlFieldActionPerformed

	private void tooltipBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tooltipBoxActionPerformed
		parallelDisplay.setBoolPreference("hoverText", tooltipBox.isSelected());
	}//GEN-LAST:event_tooltipBoxActionPerformed

	File currentPath = null;

	private void openItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openItemActionPerformed
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new javax.swing.filechooser.FileFilter(){
			public boolean accept(File f){
				return(f.isDirectory() || f.getName().endsWith(".stf"));
			}
			public String getDescription(){
				return "STF (Simple Table Format) Data Files";
			}
		});
		if (currentPath == null){
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		}
		else {
			chooser.setCurrentDirectory(currentPath);
		}

		int option = chooser.showOpenDialog(this);

		if (option == JFileChooser.APPROVE_OPTION) {
			if (chooser.getSelectedFile() != null){
				currentPath = chooser.getSelectedFile().getParentFile();
				String urltext = chooser.getSelectedFile().getAbsolutePath();
				urlField.setText(urltext);
				STFFile f = new STFFile(urltext);
				try {
					f.readContents();
				} catch (IOException e) {
					e.printStackTrace();
				}

				parallelDisplay.setModel(f);
				setTitle("Parvis - " + f.getName());
			}
		}
	}//GEN-LAST:event_openItemActionPerformed

	/** Exit the Application */
	private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
		System.exit(0);
	}//GEN-LAST:event_exitForm



	private long progressstart = 0;

	/**
	 * Callback for progress events. Updates the progressbar.
	 */
	public void processProgressEvent(ProgressEvent e){
		switch(e.getType()){
		case ProgressEvent.PROGRESS_START:
			progressstart = e.getTimestamp();
			progressBar.setValue(0); 
			timeLabel.setText("0 s");

			if (parallelDisplay.getCurrentBrush() == null){
				//workaround because we are not notified otherways if model changes
				countLabel.setText("0 / " + parallelDisplay.getNumRecords() + "   ");
			}
			break;

		case ProgressEvent.PROGRESS_UPDATE:
			progressBar.setValue((int)(e.getProgress() * 100)); 
			timeLabel.setText(((e.getTimestamp() - progressstart) / 1000) + " s");
			break;

		case ProgressEvent.PROGRESS_FINISH:
			progressBar.setValue(100);
			timeLabel.setText(((e.getTimestamp() - progressstart) / 1000) + " s");
			break;
		}
		progressLabel.setText(e.getMessage());
		//System.out.println(e.getMessage() + ": " + ((int)(e.getProgress() * 100))+"%");
	}

	public void brushChanged(Brush b) {
	}

	/**
	 * Callback triggered if the brush has been modified. Updates the counter.
	 */
	public void brushModified(Brush b) {
		if (b != null){
			countLabel.setText(b.getNumBrushed() + " / " + b.getNumValues() + "   ");
		}
		else {
			countLabel.setText("0 / " + parallelDisplay.getNumRecords() + "   ");
		}
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton resetBrushButton;
	private org.mediavirus.parvis.gui.ParallelDisplay parallelDisplay;
	//    private org.mediavirus.parvis.gui.DisplayMetricBarsPanel metricsDisplayPanel;
	//    private org.mediavirus.parvis.gui.analysis.HistogramPanel histogramPanel;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JRadioButtonMenuItem brushMenu;
	private javax.swing.JSeparator jSeparator2;
	private javax.swing.JMenuItem preferencesMenu;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.ButtonGroup menuEditGroup;
	private javax.swing.JButton jButton1;
	private javax.swing.JPanel quickPrefPanel;
	private javax.swing.JMenuItem helpItem;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JPanel statusPanel;
	private javax.swing.JToolBar modeBar;
	private javax.swing.JMenuItem saveBrushItem;
	private javax.swing.JLabel datasourceLabel;
	private javax.swing.JCheckBox hoverBox;
	private javax.swing.JMenuItem scaleMinMaxAbsItem;
	private javax.swing.JLabel timeLabel;
	private javax.swing.JProgressBar progressBar;
	private javax.swing.JLabel countLabel;
	private javax.swing.JMenuItem scaleZeroMaxItem;
	private javax.swing.JToolBar urlBar;
	private javax.swing.JButton resetAllButton;
	private javax.swing.JMenu viewMenu;
	private javax.swing.JToggleButton translateButton;
	private javax.swing.JToggleButton orderButton;
	private javax.swing.JToggleButton scaleButton;
	private javax.swing.JCheckBox fuzzyBrushBox;
	private javax.swing.JPanel progressPanel;
	private javax.swing.JLabel modeLabel;
	private javax.swing.JMenuItem scaleMinMaxItem;
	private javax.swing.JRadioButtonMenuItem translateMenu;
	private javax.swing.JRadioButtonMenuItem orderMenu;
	private javax.swing.JToggleButton brushButton;
	private javax.swing.JTextField urlField;
	private javax.swing.JPanel toolbarPanel;
	private javax.swing.JRadioButtonMenuItem scaleMenu;
	private javax.swing.JMenu editMenu;
	private javax.swing.JMenuItem openMenu;
	private javax.swing.JTextField radiusField;
	private javax.swing.JCheckBox tooltipBox;
	private javax.swing.JCheckBox histogramBox;
	private javax.swing.JLabel progressLabel;
	private javax.swing.ButtonGroup buttonEditGroup;
	private javax.swing.JMenuItem aboutItem;
	private javax.swing.JMenu helpMenu;
	// End of variables declaration//GEN-END:variables

}
