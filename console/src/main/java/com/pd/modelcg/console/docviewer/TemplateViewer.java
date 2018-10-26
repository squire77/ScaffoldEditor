package com.pd.modelcg.console.docviewer;

import com.pd.modelcg.console.codegenui.GeneratorEditorPanel;
import com.pd.modelcg.console.codegenui.GeneratorListPanel;
import com.pd.modelcg.console.codegenui.GeneratorTypeListPanel;
import com.pd.modelcg.console.application.ErrorDialog;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TemplateViewer extends TextDocumentViewer {
    public TemplateViewer(String directoryAbsolutePath) {
        super(directoryAbsolutePath);

        Border border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);

        //Action Control Panel
        GeneratorEditorPanel genEditorPanel = new GeneratorEditorPanel(border);
        GeneratorListPanel genListPanel = new GeneratorListPanel(border, genEditorPanel);
        GeneratorTypeListPanel genTypeListPanel = new GeneratorTypeListPanel(border, genListPanel);
        JSplitPane actionListPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                   genTypeListPanel, 
                                                   genListPanel);
        JSplitPane controlPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                                                 actionListPane, 
                                                 genEditorPanel);

        //Text Template Panel
        JPanel templatePanel = new JPanel(new BorderLayout());
        templatePanel.add(super.getComponent(), BorderLayout.CENTER);
        templatePanel.add(createTestPanel(), BorderLayout.SOUTH);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, controlPanel, templatePanel);

        testDataWindow = new ModelTestTextWindow();
        testDataResultWindow = new ModelTestTextWindow();
    }
   
    @Override
    public void setPreferredSize(Dimension d) {
        splitPane.setPreferredSize(d);
    }
    
    @Override
    public JComponent getComponent() {
        return splitPane;
    }
    
    private JPanel createTestPanel() {
        //ClassModel.createTestModel();
        
        JButton testDataButton = new JButton("Test Data");
        testDataButton.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        testDataWindow.setLocation(e.getXOnScreen()-testDataWindow.getSize().width,
                                                   e.getYOnScreen()-testDataWindow.getSize().height);
                        testDataWindow.setVisible(true);
                    }
                }
        );
        JButton testButton = new JButton("Test");
        testButton.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        //generate code and place into window                        
                        try {
                            //String testData = testDataWindow.getDocumentViewer().getText();                            
                            //testDataResultWindow.getDocumentViewer().setText(ClassModel.generateTestModel(getText()));
                            testDataResultWindow.setLocation(e.getXOnScreen()-testDataWindow.getSize().width,
                                                        e.getYOnScreen()-testDataWindow.getSize().height);
                            testDataResultWindow.setVisible(true);
                        } catch (Exception ex) {
                            ErrorDialog.getInstance().error("Problem generating code.", ex);
                        }  
                    }
                }
        );

        JPanel testPanel = new JPanel(new GridLayout(1, 5));
        testPanel.add(new JLabel(""));
        testPanel.add(new JLabel(""));
        testPanel.add(new JLabel(""));
        testPanel.add(testDataButton);
        testPanel.add(testButton);

        return testPanel;
    }    

    private JSplitPane              splitPane;
    private ModelTestTextWindow testDataWindow;
    private ModelTestTextWindow testDataResultWindow;
}

