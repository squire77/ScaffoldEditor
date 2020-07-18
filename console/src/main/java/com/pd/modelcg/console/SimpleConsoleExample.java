package com.pd.modelcg.console;

//import com.pd.modelcg.codegen.graphics.canvas.Drawable;
//import com.pd.modelcg.codegen.graphics.canvas.DrawingCanvas;
import com.pd.modelcg.codegen.graphics.draggablecanvas.Draggable;
import com.pd.modelcg.codegen.graphics.draggablecanvas.DraggableCanvas;
import com.pd.modelcg.console.docviewer.OverlayImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class SimpleConsoleExample extends JFrame {
    private JScrollPane viewPane;
    private DraggableCanvas canvas;
    //private DrawingCanvas<Drawable> canvas2;

    public SimpleConsoleExample() {
        super("VVN-2");

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width-800)/2, (dim.height-690)/2);

        canvas = new DraggableCanvas();
//        canvas2 = new DrawingCanvas<>(Color.lightGray);

        OverlayImage overlay = new OverlayImage();
        overlay.setBackground(Color.lightGray);
        overlay.setView(canvas);
//        overlay.setView(canvas2);

        viewPane = new JScrollPane();
        viewPane.setViewport(overlay);
        viewPane.setPreferredSize(new Dimension(800-20, 690-10));

        getContentPane().add(viewPane);

        canvas.init();
        canvas.showGrid(false);
        canvas.snapToGrid(false);
        canvas.drawObjects();
//        canvas2.drawObjects();

        addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent event){
                viewPane.setPreferredSize(new Dimension(getWidth()-30, getHeight()-120));
                repaint();
            }
        });
    }

    void run() {
        Polygon shape = new Polygon();
        shape.addPoint(0, 0);
        shape.addPoint(50, 0);
        shape.addPoint(50,50);
        shape.addPoint(0, 50);
        canvas.addObject(new Draggable(shape));
//        canvas2.addObject(new Drawable(shape));
    }
}
