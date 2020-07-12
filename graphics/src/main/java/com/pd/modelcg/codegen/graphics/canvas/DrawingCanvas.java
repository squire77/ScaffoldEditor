package com.pd.modelcg.codegen.graphics.canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DrawingCanvas<T extends IDrawable> extends JPanel
{
    private BufferedImage         image;
    private Color                 backgroundColor;
    private int                   width;
    private int                   height;
    private ZBuffer<T>            zBuffer;

    public DrawingCanvas(Color backgroundColor)
    {
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(screenDim); //this size is fixed since its maxed out
        this.width = screenDim.width;
        this.height = screenDim.height;
        this.zBuffer = new ZBuffer<>();
        this.backgroundColor = backgroundColor;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public Graphics2D getGraphics() {
        return (Graphics2D) image.getGraphics();
    }

    public BufferedImage getImage() {
        return image;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ZBuffer<T> getZbuffer() {
        return zBuffer;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image, 0, 0, backgroundColor, null);

        zBuffer.drawAllBackToFront(getGraphics());
    }

    public void addObject(T object)
    {
        zBuffer.add(object); //add to top of stack
        object.draw((Graphics2D)image.getGraphics());
        repaint();
    }

    public void removeObject(T object)
    {
        zBuffer.remove(object);
        drawObjects();
    }

    public void drawObjects()
    {
        Graphics2D g = (Graphics2D)image.getGraphics();

        g.setPaint(backgroundColor);
        g.fillRect(0, 0, width, height);

        zBuffer.drawAllBackToFront(g);

        repaint();
    }
}
