package com.pd.modelcg.console.docviewer;

import java.awt.*;
import javax.swing.*;

public class OverlayImage extends JViewport {

    private static double SIN_30 = 0.5;
    private static double COS_30 = 0.866;
    private static int CENTER_X = 400;
    private static int CENTER_Y = 300;

    @Override
    public void paintChildren(Graphics g) {
        super.paintChildren(g);

        //paint overlay on top of the image
        //<<insert code here>>
    }

    //public void sphere(Graphics2D g, int x, int y, int z, int r) {
    //    (X-H)2 + (Y-K)2=r2
    //    for (int i=-r; i<=r; ++r){
    //        int x = r*r -
    //    }
    //}

    public void circle(Graphics2D g, int x, int y, int z, int d) {
        g.setPaint(Color.blue);
        int a = CENTER_X + x + (int)(COS_30*z);
        int b = CENTER_Y - y - (int)(SIN_30*z);
        g.drawOval(a, b, d, d);
    }

    public void line(Graphics2D g, int x1, int y1, int z1, int x2, int y2, int z2) {
        g.setPaint(Color.blue);
        int a = CENTER_X + x1 + (int)(COS_30*z1);
        int b = CENTER_Y - y1 - (int)(SIN_30*z1);
        int c = CENTER_X + x2 + (int)(COS_30*z2);
        int d = CENTER_Y - y2 - (int)(SIN_30*z2);
        g.drawLine(a, b, c, d);
    }

    public void point(Graphics2D g, int x, int y, int z) {
        line(g, x,y,z,x,y,z);
    }
}
