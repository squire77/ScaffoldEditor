package com.pd.modelcg.console.canvasext;

import com.pd.modelcg.codegen.graphics.draggablecanvas.DraggableCanvas;
import com.pd.modelcg.codegen.graphics.graphcanvas.EndPoint;
import com.pd.modelcg.codegen.graphics.graphcanvas.Graph;
import com.pd.modelcg.codegen.model.uml.basic.UmlPackage;
import com.pd.modelcg.codegen.model.uml.basic.UmlType;
import com.pd.modelcg.codegen.model.uml.basic.cm.CmClass;
import com.pd.modelcg.codegen.model.uml.basic.cm.CmOperation;
import com.pd.modelcg.codegen.model.uml.basic.cm.CmParameter;
import com.pd.modelcg.codegen.model.uml.basic.cm.CmProperty;
import com.pd.modelcg.console.canvasext.form.UMLClassForm;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.Observable;

public class UMLClassNode extends UMLNode {       
    public static UMLClassNode create(Graph graph, UMLCanvas canvas, CmClass clazz) {
        UMLClassNode c = new UMLClassNode(graph, canvas, clazz);
        c.initialize();                                
        return c;
    }
    
    // use the create method to ensure initialize() is called  
    private UMLClassNode(Graph graph, UMLCanvas canvas, CmClass clazz) {
        super(graph, canvas);

        this.clazz = clazz;        
    }
    
    //must call this after constructor
    @Override
    public void initialize() {
        super.initialize();
        
        resizePolygon();
        
        //create controls for passing mouse events
        super.addControl(new LinkToAdornment(canvas, this, LinkToAdornment.TOP).initialize());
        super.addControl(new LinkToAdornment(canvas, this, LinkToAdornment.BOTTOM).initialize());
        super.addControl(new LinkToAdornment(canvas, this, LinkToAdornment.RIGHT).initialize());
        super.addControl(new LinkToAdornment(canvas, this, LinkToAdornment.LEFT).initialize());
        super.addControl(new ResizeAdornment(canvas, this, ResizeAdornment.UPPER_LEFT).initialize());
        super.addControl(new ResizeAdornment(canvas, this, ResizeAdornment.UPPER_RIGHT).initialize());
        super.addControl(new ResizeAdornment(canvas, this, ResizeAdornment.LOWER_RIGHT).initialize());
        super.addControl(new ResizeAdornment(canvas, this, ResizeAdornment.LOWER_LEFT).initialize());
        
        //observe model changes
        clazz.addObserver(this);
    }    
    
    @Override
    public UMLNodeType getNodeType() {
        return UMLNodeType.CLASS_NODE_TYPE;
    }
            
    @Override
    public String getPackageID() {
        return getModelElement().getPackage();
    }

    @Override
    public String getID() {
        return clazz.getID();
    }
    
    public CmClass getModelElement() {
        return this.clazz;
    }
    
    @Override
    public void update(Observable o, Object obj) {
        resizePolygon();
        canvas.drawObjects();
    }

    public void remove(DraggableCanvas canvas) {
        super.remove(canvas);    
    
        //remove from model        
        UmlPackage pkg = UmlPackage.getPackage(this.clazz.getPackage());
        pkg.removeOwnedType(this.clazz.getID());
        CmClass.removeClass(this.clazz.getID());
        UmlType.unregisterType(this.clazz.getID());
    }

    @Override
    public void mouseClicked(MouseEvent event) {      
        if (event.getClickCount() == 2) {//&& !event.isConsumed()) {
            //lazy create the form to save on load time
            if (classForm == null) {
                this.classForm = new UMLClassForm(clazz);
            }
            
            classForm.loadValues();
            classForm.setLocation(event.getXOnScreen(), event.getYOnScreen());
            classForm.setLocationRelativeTo(null); //center dialog on screen
            classForm.setVisible(true);
            //event.consume();
        }
    }        
    
    @Override
    public void doDraw(Graphics2D g) {
        super.doDraw(g);        
        
        FontMetrics fm = canvas.getFontMetrics(ARIEL_BOLD_16);
        int nameWidth = fm.stringWidth(clazz.getName());
        
        //draw title
        int x0 = getPolygon().xpoints[0];
        int y0 = getPolygon().ypoints[0];
        g.setPaint(super.getBorderColor());
        g.setFont(ARIEL_BOLD_16);
        g.drawString(clazz.getName(), x0+width()/2-nameWidth/2, y0+(gridSpacing+CHAR_HEIGHT/2));
        y0 += 2 * gridSpacing;                
        
        //draw separator
        g.drawLine(x0, y0, x0+width(), y0);
        
        AttributedString attribStr;
        
        //draw attributes       
        if (!clazz.getOwnedAttributes().isEmpty()) {
            for (CmProperty attrib: clazz.getOwnedAttributes()) {
                String attribName = attrib.getName();
                attribStr = new AttributedString(getAttribStr(attribName));
                attribStr.addAttribute(TextAttribute.FONT, ARIEL_PLAIN_16);
                if (clazz.getOwnedAttributeByName(attribName).isStatic()) {
                    attribStr.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                }
                g.drawString(attribStr.getIterator(), x0+gridSpacing, y0+(gridSpacing+CHAR_HEIGHT/2));
                y0 += 2 * gridSpacing;
            }
        }
        
        //draw separator
        g.drawLine(x0, y0, x0+width(), y0);
        
        //draw methods      
        if (!clazz.getOwnedOperations().isEmpty()) {
            for (CmOperation method: clazz.getOwnedOperations()) {
                String methodName = method.getName();
                attribStr = new AttributedString(getMethodStr(methodName));
                if (clazz.getOwnedOperationByName(methodName).isAbstract()) {
                    attribStr.addAttribute(TextAttribute.FONT, ARIEL_ITALIC_16);
                } else {
                    attribStr.addAttribute(TextAttribute.FONT, ARIEL_PLAIN_16);
                }
                if (clazz.getOwnedOperationByName(methodName).isStatic()) {
                    attribStr.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                }
                
                g.drawString(attribStr.getIterator(), x0+gridSpacing, y0+(gridSpacing+CHAR_HEIGHT/2));
                y0 += 2 * gridSpacing;
            }
        }        
    }   
    
    private String getAttribStr(String name) {
        CmProperty attr = clazz.getOwnedAttributeByName(name);
        
        String visiStr;
        
        switch (attr.getVisibilityKind()) {
            case 0:
                visiStr = "+";
                break;
            case 1:
                visiStr = "-";
                break;
            case 2:   
                visiStr = "#";
                break;
            default:
                visiStr = "";
        }
        
        StringBuilder typeStr = new StringBuilder();
        
        if (attr.getType() != null) {
            typeStr.append(":");
            typeStr.append(attr.getType().getName());
        }
        
        return visiStr + name + typeStr.toString();        
    }
    
    private String getMethodStr(String name) {
        CmOperation operation = clazz.getOwnedOperationByName(name);
        
        String visiStr;
        
        switch (operation.getVisibilityKind()) {
            case 0:
                visiStr = "+";
                break;
            case 1:
                visiStr = "-";
                break;
            case 2:   
                visiStr = "#";
                break;
            default: //PACKAGE
                visiStr = "~";
        }
        
        String retStr = "";
        
        if (!operation.getType().isVoid()) {
            retStr = ":" + operation.getType().getName();
        }
        
        StringBuilder paramStr = new StringBuilder();        
        boolean first = true;
        
        for (CmParameter p: operation.getParameters()) {
            if (!first) {
                paramStr.append(",");
            } else {
                first = false;
            }
            
            paramStr.append(p.getName());
            
            if (p.getType() != null) {
                paramStr.append(":");
                paramStr.append(p.getType().getName());
            }
        }
        
        return visiStr + name + "(" + paramStr + ")" + retStr;     
    }
    
    public void resizePolygon() {    
        //cheat by using bold italic which uses maximum space
        FontMetrics fm = canvas.getFontMetrics(ARIEL_BOLD_ITALIC_16);
        
        int maxStrWidth = fm.stringWidth(clazz.getName());
        for (CmProperty attrib: clazz.getOwnedAttributes()) {          
            String attribStr = getAttribStr(attrib.getName());
            int strWidth = fm.stringWidth(attribStr);
            if (strWidth > maxStrWidth) {
                maxStrWidth = strWidth;
            }
        }
        for (CmOperation method: clazz.getOwnedOperations()) {
            String methodStr = getMethodStr(method.getName());
            int strWidth = fm.stringWidth(methodStr);
            if (strWidth > maxStrWidth) {
                maxStrWidth = strWidth;
            }
        }                        
        
        // max(name width, max_chars(attribs), max_chars(methods)) + 2
        double newWidth = maxStrWidth / gridSpacing + 2;
        
        // title height + (# attribs + # methods) * 2
        double newHeight = TITLE_HEIGHT + (clazz.getOwnedAttributes().size() + clazz.getOwnedOperations().size()) * 2;
        
        //calculate new height/width for polygon
        //Title: 6
        //Attributes: max(6, letter width * max_num_chars(attribs))
        //Methods: max(6, letter width * max_num_chars(methods))
        Dimension dim = recalculateSize(width()/gridSpacing, height()/gridSpacing, newWidth, newHeight, gridSpacing);
        
        if (dim != null) {
            //resize the polygon
            int posX = getPolygon().xpoints[0];
            int posY = getPolygon().ypoints[0];
            super.initialize(recreatePolygon(dim.width, dim.height));
            translate(posX, posY);

            //reposition on the endpoints
            for (EndPoint ep: endPoints) {
                ep.resetPosition();                
            }        
        }
    }
     
    private static Polygon recreatePolygon(int width, int height) {       
        //node with controls is always 4 points in clockwise order
        //use multiples of grid spacing to prevent resizing when snap is enabled
        Polygon rectangle = new Polygon();
        rectangle.addPoint(0, 0);
        rectangle.addPoint(width, 0);
        rectangle.addPoint(width, height);
        rectangle.addPoint(0, height);
        
        return rectangle;
    } 

    private UMLClassForm                    classForm;     
    private CmClass                         clazz;
    
    //initialize polygon (must be static to pass into super constructor)
    static
    {
        Dimension dim = recalculateSize(0, 0, TITLE_WIDTH, TITLE_HEIGHT, 12);
        
        if (dim != null) {
            initialPolygon = recreatePolygon(dim.width, dim.height);
        }
    }           
}
