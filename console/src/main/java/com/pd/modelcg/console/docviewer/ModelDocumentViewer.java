package com.pd.modelcg.console.docviewer;

import com.pd.modelcg.console.canvasext.UMLCanvas;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Dimension;

public class ModelDocumentViewer extends DocumentViewer {
    public ModelDocumentViewer(String directoryAbsolutePath) {
        super(directoryAbsolutePath);

        this.viewPane = new JScrollPane();
        this.umlCanvas = UMLCanvas.create();
        this.umlCanvas.setOpaque(false);
        this.umlCanvas.setBackground(Color.lightGray);

        //create overlay viewport to draw directly to
        overlay = new OverlayImage();
        overlay.setBackground(Color.lightGray);
        overlay.setView(umlCanvas);

        //create model viewer
        viewPane.setViewport(overlay);                                
    }
    @Override
    public void setPreferredSize(Dimension dim) {
        viewPane.setPreferredSize(dim);
    }
    @Override
    public JComponent getComponent() {
        return this.viewPane;
    }
    public UMLCanvas getUMLCanvas() {
        return this.umlCanvas;
    }

    @Override
    public void clear() {
        umlCanvas.reset();
        umlCanvas.drawObjects();
    }

    @Override
    public void doNewFile(String fileName) {
        super.reset();
        umlCanvas.reset();
        umlCanvas.drawObjects();
    }
    @Override
    public void doOpenFile(String fileName) {
        super.reset();
        umlCanvas.reset();     
        umlCanvas.readGraph(fileName);
    }
    @Override
    public void doSaveAsFile(String fileName){
        umlCanvas.writeGraph(fileName);
    }

    private JScrollPane     viewPane;
    private OverlayImage    overlay;
    private UMLCanvas       umlCanvas;
}
