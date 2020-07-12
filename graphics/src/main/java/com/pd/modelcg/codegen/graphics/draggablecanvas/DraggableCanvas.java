package com.pd.modelcg.codegen.graphics.draggablecanvas;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.pd.modelcg.codegen.graphics.canvas.ZBuffer;

public class DraggableCanvas extends JPanel implements IDraggableCanvas, MouseWheelListener, MouseInputListener
{
    final public static Color BACKGROUND_COLOR = Color.lightGray;
    final static float DASH[] = {10.0f};
    final static BasicStroke DASHED_STROKE = new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f, DASH, 0.0f);

    private BufferedImage                   image;

    private Color                           backgroundColor;

    protected PopupMenu                     objectPopup;
    protected PopupMenu                     backgroundPopup;
    protected PopupMenu                     groupSelectionPopup;

    protected java.util.List<IDraggable>    savedSelection;
    protected java.util.List<IDraggable>    groupSelection;
    protected boolean                       groupSelectionEnabled;
    protected boolean                       groupSelectionActive;
    protected Rectangle                     groupSelectionBounds;
    protected Point                         groupSelectionStart;
    protected Point                         groupSelectionEnd;

    protected Point startPos;
    protected IDraggable                    selection;
    protected int                           width;
    protected int                           height;
    protected Grid                          grid;

    protected ZBuffer<IDraggable>           zBuffer;

    public DraggableCanvas()
    {
        this.backgroundColor = BACKGROUND_COLOR;
        this.savedSelection = new ArrayList<>();
        this.groupSelection = new ArrayList<>();
        this.groupSelectionEnabled = false;
        this.groupSelectionBounds = new Rectangle();
        this.groupSelectionStart = new Point();
        this.groupSelectionEnd = new Point();

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(screenDim); //this size is fixed since its maxed out
        this.width = screenDim.width;
        this.height = screenDim.height;

        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        this.grid = new Grid(width, height);
        this.grid.snapToGrid(true); //enable snap to grid
        this.grid.showGrid(true); //enable show grid
        this.selection = null;
        this.zBuffer = new ZBuffer<>();
    }

    //must be called after calling constructor
    //also, call drawObjects() to paint the background and optional grid
    public void init() {
        createObjectPopupMenu();
        createBackgroundPopupMenu();
        createGroupSelectionPopupMenu();
        addMouseWheelListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void reset() {
        this.selection = null;
        this.zBuffer.clear();
    }

    public int getGridSpacing() {
        return grid.getSpacing();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image, 0, 0, backgroundColor, null);

        if (groupSelectionActive) {
            Graphics2D g2d = (Graphics2D) g;

            for (IDraggable d: groupSelection) {
                d.draw(g2d);
            }

            g2d.setColor(Color.BLUE);
            g2d.setStroke(DASHED_STROKE);
            g2d.drawRect(groupSelectionBounds.x, groupSelectionBounds.y,
                    groupSelectionBounds.width, groupSelectionBounds.height);

            return;
        }

        //draw active selection over the image
        if (selection != null) {
            selection.draw((Graphics2D)g);
        }
    }

    public void showGrid(boolean visible) {
        grid.showGrid(visible);
        drawObjects();
    }

    public void snapToGrid(boolean snap) {
        grid.snapToGrid(snap);
        drawObjects();
    }

    public void addObject(IDraggable object) {
        object.snapToGrid(grid);
        zBuffer.add(object);
        object.draw((Graphics2D)image.getGraphics());
        repaint();
    }

    // modify Draggable subclasses Remove() method, not this method
    // only Draggable.remove() should call this method
    final public void removeObject(IDraggable object) {
        zBuffer.remove(object);
        drawObjects(); //remove object from image
    }

    final public void drawObjects() {
        Graphics2D g = (Graphics2D)image.getGraphics();

        g.setPaint(backgroundColor);
        g.fillRect(0, 0, width, height);

        grid.draw(g);
        doDrawObjects(g);

        if (groupSelectionEnabled) {
            g.setColor(Color.BLUE);
            g.setStroke(DASHED_STROKE);
            g.drawRect(groupSelectionBounds.x, groupSelectionBounds.y,
                    groupSelectionBounds.width, groupSelectionBounds.height);
        }

        repaint(); // this is necessary to re-paint the canvas, for example if something gets removed
    }

    protected void doDrawObjects(Graphics2D g) {
        zBuffer.apply((d) -> d.snapToGrid(grid));
        zBuffer.drawAllBackToFront(g);
    }

    public void selectObjectAtPosition(int x, int y) {
        IDraggable obj = zBuffer.getFrontMostAtPosition(x, y);
        updateSelection(obj);
    }

    private void updateSelection(IDraggable newSelection) {
        if(selection != null)
            selection.unselect();

        selection = newSelection;

        if(newSelection != null)
            newSelection.select();
    }

    protected void createObjectPopupMenu() {
        objectPopup = new PopupMenu();
        add(objectPopup);

        MenuItem remove = new MenuItem("Remove");
        objectPopup.add(remove);

        remove.addActionListener((event) -> {
                if(selection != null) {
                    selection.remove(DraggableCanvas.this);
                    selection = null;
                }
        } );
    }

    protected void createBackgroundPopupMenu() {
        backgroundPopup = new PopupMenu();
        add(backgroundPopup);

        MenuItem showGridM = new MenuItem("Show Grid");
        showGridM.addActionListener( (event) -> {
            grid.showGrid(true);
            drawObjects();
        } );

        MenuItem hideGridM = new MenuItem("Hide Grid");
        hideGridM.addActionListener( (event) -> {
            grid.showGrid(false);
            drawObjects();
        } );

        MenuItem enableGridM = new MenuItem("Enable Snap-to-Grid");
        enableGridM.addActionListener( (event) -> {
            grid.snapToGrid(true);
            drawObjects();
        } );

        MenuItem disableGridM = new MenuItem("Disable Snap-to-Grid");
        disableGridM.addActionListener( (event) -> {
            grid.snapToGrid(false);
            drawObjects();
        } );

        backgroundPopup.add(showGridM);
        backgroundPopup.add(hideGridM);
        backgroundPopup.add(enableGridM);
        backgroundPopup.add(disableGridM);
    }

    protected void createGroupSelectionPopupMenu() {
        groupSelectionPopup = new PopupMenu();
        add(groupSelectionPopup);
    }

    private void popupEvent(MouseEvent event) {
        if (event.isPopupTrigger()) {
            IDraggable object = zBuffer.getFrontMostAtPosition(event.getX(), event.getY());
            if(object != null) {
                objectPopup.show(this, event.getX(), event.getY());
            } else {
                if (groupSelectionEnabled && groupSelectionBounds.contains(event.getPoint())) {
                    groupSelectionPopup.show(this, event.getX(), event.getY());
                    return;
                }

                backgroundPopup.show(this, event.getX(), event.getY());
            }
        }
    }

    public void mouseEntered(MouseEvent event) {
        if (selection != null) {
            selection.mouseEntered(event);
        }

        repaint();
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
            if (selection != null) {
                int angle = e.getWheelRotation() * 5;
                selection.rotate(angle);
                drawObjects();
            }
        }

        repaint();
    }

    public void mouseMoved(MouseEvent event) {
        if (selection != null) {
            selection.mouseMoved(event);
        }

        repaint();
    }

    public void mouseExited(MouseEvent event) {
        if (selection != null) {
            selection.mouseExited(event);
        }

        repaint();
    }

    public void mouseClicked(MouseEvent event) { //mouse up and down
        if(selection != null) {
            selection.mouseClicked(event);
        } else {
            clearGroupSelection();
            drawObjects();
        }

        repaint();
    }

    public void mousePressed(MouseEvent event) { //mouse down
        startPos = event.getPoint();

        if (groupSelectionEnabled && groupSelectionBounds.contains(event.getPoint())) {
            this.groupSelectionActive = true;

            for (IDraggable d: groupSelection) {
                d.mousePressed(event);
                d.setVisible(false);
            }

            //erase selection before we drag it around; also, erase bounding box
            groupSelectionEnabled = false;
            drawObjects();
            groupSelectionEnabled = true;

            for (IDraggable d: groupSelection) {
                d.setVisible(true);
            }

            return;
        }

        selectObjectAtPosition(event.getX(), event.getY());

        if (selection != null) {
            selection.mousePressed(event);

            //erase selection before we drag it around
            selection.setVisible(false);
            drawObjects();
            selection.setVisible(true);
        } else {
            drawObjects(); //refresh everything, e.g. highlighting and endpoint visibility

            //check for group selection (rubberband)
            groupSelectionEnabled = true;
            groupSelectionStart.x = event.getX();
            groupSelectionStart.y = event.getY();
        }

        repaint();
    }

    public void mouseDragged(MouseEvent event) {
        if (startPos == null) {
            return;
        }

        int deltaX = event.getX() - (int)startPos.getX();
        int deltaY = event.getY() - (int)startPos.getY();

        if (groupSelectionActive) {
            if (validateGroupSelectionBounds(deltaX, deltaY, width, height)) {
                for (IDraggable d: groupSelection) {
                    //only drag non-grouped objects since grouped objects are assumed to move together
                    if (d.isDepthLevelOne()) {
                        d.mouseDragged(event, deltaX, deltaY);
                    }
                }

                groupSelectionBounds.x += deltaX;
                groupSelectionBounds.y += deltaY;

                startPos = event.getPoint();
                repaint();
            }

            return;
        }

        if(selection != null) {
            //make sure we don't move the object off the viewable screen
            if (selection.validate(deltaX, deltaY, width, height)) {
                selection.mouseDragged(event, deltaX, deltaY);
                startPos = event.getPoint();
                repaint();
            }
        } else {
            if (groupSelectionEnabled) {
                groupSelectionEnd.x = event.getX();
                groupSelectionEnd.y = event.getY();
                updateGroupSelectionBoundingRectangle();
                drawObjects();
            }
        }
    }

    public void mouseReleased(MouseEvent event) { //mouse up
        popupEvent(event);

        if (groupSelectionActive) {
            for (IDraggable d: groupSelection) {
                d.mouseReleased(event);
                d.snapToGrid(grid); //draw selection back onto the image
            }

            clearGroupSelection();
            drawObjects();
            startPos = null;
            return;
        }

        if(selection != null) {
            selection.mouseReleased(event);
            selection.snapToGrid(grid); //draw selection back onto the image
            drawObjects();
        } else {
            if (groupSelectionEnabled) {
                clearGroupSelection();
                updateGroupSelection();

                if (!groupSelection.isEmpty()) {
                    this.groupSelectionEnabled = true;

                    //save off the selection for use by pop-up actions
                    savedSelection.clear();
                    for (IDraggable d: this.groupSelection) {
                        //only add items of depth 1
                        if (d.isDepthLevelOne()) {
                            savedSelection.add(d);
                        }
                    }
                }

                drawObjects();
            }
        }

        repaint();
        startPos = null;
    }

    protected void clearGroupSelection() {
        //clear color back to normal
        for (IDraggable d: this.groupSelection) {
            unhighlightNodeAndLinks(d);
        }

        this.groupSelection.clear();
        this.groupSelectionEnabled = false;
        this.groupSelectionActive = false;
    }

    protected void updateGroupSelection() {
        zBuffer.apply( (d) -> {
            if (isGroupSelectable(d) &&
                    groupSelectionBounds.contains(d.getPolygon().xpoints[0], d.getPolygon().ypoints[0]) &&
                    groupSelectionBounds.contains(d.getPolygon().xpoints[2], d.getPolygon().ypoints[2])) {
                this.groupSelection.add(d);

                //highlight color of selected objects
                highlightNodeAndLinks(d);
            }
        });
    }

    public boolean validateGroupSelectionBounds(int deltaX, int deltaY, int width, int height) {
        boolean result = true;

        int left = groupSelectionBounds.x;
        int right = groupSelectionBounds.x + deltaX;
        int top = groupSelectionBounds.y;
        int bottom = groupSelectionBounds.y + deltaY;

        if ((left + deltaX < 0) || (right + deltaX > width) ||
                (top + deltaY < 0) || (bottom + deltaY > height)) {
            result = false;
        }

        return result;
    }

    protected void updateGroupSelectionBoundingRectangle() {
        int startX;
        int rectWidth;

        if (groupSelectionStart.x < groupSelectionEnd.x) {
            startX = groupSelectionStart.x;
            rectWidth = groupSelectionEnd.x - startX;
        } else {
            startX = groupSelectionEnd.x;
            rectWidth = groupSelectionStart.x - startX;
        }

        int startY;
        int rectHeight;

        if (groupSelectionStart.y < groupSelectionEnd.y) {
            startY = groupSelectionStart.y;
            rectHeight = groupSelectionEnd.y - startY;
        } else {
            startY = groupSelectionEnd.y;
            rectHeight = groupSelectionStart.y - startY;
        }

        groupSelectionBounds.x = startX;
        groupSelectionBounds.y = startY;
        groupSelectionBounds.width = rectWidth;
        groupSelectionBounds.height = rectHeight;
    }

    //overriden by subclass
    protected boolean isGroupSelectable(IDraggable d) {
        return false;
    }

    //overriden by subclass
    protected void highlightNodeAndLinks(IDraggable d) {
        d.setColor(d.getSelectedColor());
        d.setBorderColor(d.getSelectedBorderColor());
    }

    //overriden by subclass
    protected void unhighlightNodeAndLinks(IDraggable d) {
        d.setColor(d.getUnselectedColor());
        d.setBorderColor(d.getUnselectedBorderColor());
    }
}
