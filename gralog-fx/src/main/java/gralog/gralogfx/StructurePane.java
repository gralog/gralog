/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;

import java.util.ArrayList;

import gralog.structure.*;
import gralog.events.*;
import gralog.rendering.*;
import gralog.gralogfx.events.*;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;
import javafx.geometry.Point2D;

import javafx.event.EventType;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;


/**
 *
 */
//public class StructurePane extends ScrollPane implements StructureListener {
public class StructurePane extends StackPane implements StructureListener {


    private boolean needsRepaint = true;
    private Lock needsRepaintLock = new ReentrantLock();

    private static Color selectionBoxColor = Color.rgb(40, 110, 160, 0.3);
    private static final EventType<StructurePaneEvent> ALL_STRUCTUREPANE_EVENTS
        = new EventType<>("ALL_STRUCTUREPANE_EVENTS");
    private static final EventType<StructurePaneEvent> STRUCTUREPANE_SELECTIONCHANGED
        = new EventType<>(ALL_STRUCTUREPANE_EVENTS, "STRUCTUREPANE_SELECTIONCHANGED");

    public void setOnSelectionChanged(EventHandler<StructurePaneEvent> handler) {
        this.setEventHandler(STRUCTUREPANE_SELECTIONCHANGED, handler);
    }

    // private List<SpaceEvent> spaceListeners = new ArrayList<SpaceEvent>();

    Structure structure;
    Canvas canvas;
    Highlights highlights = new Highlights();


    //temporary drawing state variables
    private Set<Object> dragging = null;
    private boolean wasDraggingPrimary = false;
    private boolean wasDraggingSecondary = false;
    private boolean wasDraggingMiddle = false;

    private Point2D boxingStartingPosition;
    private boolean selectionBoxingActive = false;
    private boolean selectionBoxDragging = false;
    private IMovable currentEdgeStartingPoint;

    private boolean selectedCurveControlPoint = false;

    private Edge holdingEdge = null;
    private Vector2D holdingEdgeStartingPosition;

    private double lastMouseX = -1d;
    private double lastMouseY = -1d;


    private double gridSize = 1.0; // cm
    private boolean snapToGrid = true;

    public StructurePane(Structure structure) {
        this.structure = structure;
        canvas = new Canvas(500,500);

        this.getChildren().add(canvas);
        // resize canvas with surrounding StructurePane
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());
        canvas.widthProperty().addListener(e -> this.requestRedraw());
        canvas.heightProperty().addListener(e -> this.requestRedraw());



        canvas.setOnScroll(e -> {
            ScrollEvent se = (ScrollEvent) e;

            Point2D oldMousePos = screenToModel(new Point2D(se.getX(), se.getY()));
            zoomFactor *= Math.pow(1.2d, se.getDeltaY() / 40d);
            Point2D newMousePos = screenToModel(new Point2D(se.getX(), se.getY()));

            offsetX += oldMousePos.getX() - newMousePos.getX(); // sweet :-)
            offsetY += oldMousePos.getY() - newMousePos.getY();
            this.requestRedraw();
        });

        canvas.setFocusTraversable(true);
        canvas.addEventFilter(MouseEvent.ANY, (e) -> canvas.requestFocus());

        setMouseEvents();
    }

    // public void addSpaceListener(SpaceEvent toAdd) {
    //     spaceListeners.add(toAdd);
    // }           

    // public void spacePressed(){
    //     System.out.println("space pressed");
    //     for (SpaceEvent listener : spaceListeners){
    //         listener.spacePressed();
    //     }
    // }

    public Structure getStructure() {
        return structure;
    }

    public void requestRedraw() {
        needsRepaintLock.lock();
        try {
            if (!needsRepaint) {
                Platform.runLater(this::draw);
                needsRepaint = true;
            }
        } finally {
            needsRepaintLock.unlock();
        }
    }

    public void requestRedraw(Point2D from, Point2D to) {
        needsRepaintLock.lock();
        try {
            if (!needsRepaint) {
                Platform.runLater(() -> this.draw(from, to));
                needsRepaint = true;
            }
        } finally {
            needsRepaintLock.unlock();
        }
    }
    private void requestRedrawRectangle(Point2D from, Point2D to, Color color){
        needsRepaintLock.lock();
        try {
            if (!needsRepaint) {
                Platform.runLater(() -> this.drawRectangle(from, to, color));
                needsRepaint = true;
            }
        } finally {
            needsRepaintLock.unlock();
        }
    }
    public void alignHorizontallyMean(){
        structure.alignHorizontallyMean(highlights.getSelection());
        structure.snapToGrid(gridSize);
        this.requestRedraw();
    }
    public void alignVerticallyMean(){
        structure.alignVerticallyMean(highlights.getSelection());
        structure.snapToGrid(gridSize);
        this.requestRedraw();
    }
    public final void setMouseEvents() {

        canvas.setOnMouseClicked(e -> { });
        canvas.setOnMousePressed(this::onMousePressed);
        canvas.setOnMouseReleased(this::onMouseReleased);
        canvas.setOnMouseDragged(this::onMouseDragged);
        canvas.setOnKeyPressed(e -> {

            switch (e.getCode()) {
                case DELETE:
                    Set<Object> selection = new HashSet<>(highlights.getSelection());
                    for (Object o : selection) {
                        if (o instanceof Vertex) {
                            structure.removeVertex((Vertex) o);
                            clearSelection();
                            System.out.println(structure.holes);
                        }
                        else if (o instanceof Edge && !selectedCurveControlPoint){
                            structure.removeEdge((Edge) o);
                            clearSelection();
                        }
                        else if (o instanceof CurveControlPoint){
                            CurveControlPoint c = ((CurveControlPoint)o);
                            c.parent.removeControlPoint(c);
                            highlights.remove(c);
                            selectedCurveControlPoint = false;
                            break; //if not breaking, edge will be able to be deleted
                        }
                    }
                    this.requestRedraw();
                    break;
//                case V:
//                    highlights.filterType(Vertex.class);
//                    this.requestRedraw();
//                    break;
                case C:
                    structure.collapseEdges(highlights.getSelection());
                    this.requestRedraw();
                    break;
//                case E:
//                    highlights.filterType(Edge.class);
//                    this.requestRedraw();
//                    break;
                case D:
                    List<Vertex> duplicates = structure.duplicate(highlights.getSelection(), gridSize);
                    structure.snapToGrid(gridSize);
                    highlights.clearSelection();
                    highlights.selectAll(duplicates);
                    this.requestRedraw();
                    break;
                case A:
                    if(e.isControlDown() || e.isMetaDown()){
                        highlights.selectAll(structure.getAllMovablesModifiable());
                        this.requestRedraw();
                    }
                    this.requestRedraw();
                    break;

                case B:
                    Object edge = highlights.getSelection().iterator().next();
                    if(edge instanceof Edge){
                        ((Edge) edge).addCurveControlPoint(((Edge) edge).getTarget().coordinates.plus(new Vector2D(0,-2)));
                        ((Edge) edge).addCurveControlPoint(((Edge) edge).getSource().coordinates.plus(new Vector2D(0,-2)));
                        this.requestRedraw();
                    }
                    break;
            }
        });
    }
    private void onMousePressed(MouseEvent e){
        Point2D mousePositionModel = screenToModel(new Point2D(e.getX(), e.getY()));
        lastMouseX = mousePositionModel.getX();
        lastMouseY = mousePositionModel.getY();
        IMovable selected = structure.findObject(lastMouseX, lastMouseY);

        //group selection handling for primary mouse button
        if(e.isPrimaryButtonDown()){

            //if selection hit something, select
            if (selected != null) {
                //only clear selection if mouse press was not on a bezier control point
                if(selected instanceof CurveControlPoint){
                    //select only the edge and the bezier control point.
                    CurveControlPoint controlPoint = (CurveControlPoint) selected;
                    clearSelection();
                    select(controlPoint.parent);
                    select(selected);
                    dragging = highlights.getSelection();
                    selectedCurveControlPoint = true;
                }else{
                    //reassign selection to object that was not in the list
                    if(!e.isControlDown() && !highlights.isSelected(selected)){
                        clearSelection();
                    }

                    select(selected);
                    dragging = highlights.getSelection();

                    if(selected instanceof Edge){
                        holdingEdge = (Edge) selected;
                        holdingEdgeStartingPosition = Vector2D.point2DToVector(mousePositionModel);
                    }
                    if(selected instanceof Vertex){
                        System.out.println(((Vertex)selected).id);
                    }
                }
            }
            //if selection hit nothing, start boxing
            else if(!e.isControlDown()){
                boxingStartingPosition = new Point2D(e.getX(), e.getY());
                selectionBoxingActive = true;
                clearSelection();
            }
        }else if(e.isSecondaryButtonDown()){
            //start an edge if secondary mouse down on a vertex
            if(selected instanceof Vertex){
                currentEdgeStartingPoint = selected;
            }else if(selected == null){
                Vertex v = structure.createVertex();
                v.coordinates = new Vector2D(
                        mousePositionModel.getX(),
                        mousePositionModel.getY()
                );
                if (snapToGrid){
                    v.snapToGrid(gridSize);
                }

                structure.addVertex(v);
            }
        }
        this.requestRedraw();
    }
    private void onMouseReleased(MouseEvent e){
        MouseButton b = e.getButton();

        Point2D mousePositionModel = screenToModel(new Point2D(e.getX(), e.getY()));
        lastMouseX = mousePositionModel.getX();
        lastMouseY = mousePositionModel.getY();
        IMovable selected = structure.findObject(lastMouseX, lastMouseY);

        if (dragging != null && snapToGrid) {
            structure.snapToGrid(gridSize);
            this.requestRedraw();
        }
        else if(b == MouseButton.PRIMARY && selectionBoxDragging && selectionBoxingActive){
            if(distSquared(screenToModel(boxingStartingPosition), mousePositionModel) > 0.01){

                Set<IMovable> objs = structure.findObjects(screenToModel(boxingStartingPosition), mousePositionModel);
                highlights.selectAll(objs);
            }
        }
        //mouse release dissolves selection group, but not when
        //1) the selection group has been moved = wasDraggingPrimary
        //2) another item has been added to the selection = isControlDown
        //3) the button released wasn't primary
        else if(b == MouseButton.PRIMARY && !e.isControlDown() && !wasDraggingPrimary){
            Object lastAdded = highlights.lastAdded();
            clearSelection();
            select(lastAdded);
        }
        //right release on a vertex while drawing an edge = add edge
        else if(b == MouseButton.SECONDARY && selected instanceof Vertex && currentEdgeStartingPoint != null){
            structure.addEdge((Vertex)currentEdgeStartingPoint, (Vertex)selected);
        }
        wasDraggingPrimary = false;
        wasDraggingSecondary = false;
        selectionBoxingActive = false;
        selectionBoxDragging = false;
        holdingEdge = null;
        dragging = null;
        currentEdgeStartingPoint = null;

        this.requestRedraw();
    }
    private void onMouseDragged(MouseEvent e) {
        if(e.isPrimaryButtonDown()){
            wasDraggingPrimary = true;
        }
        if(e.isSecondaryButtonDown()){
            wasDraggingSecondary = true;
        }

        Vector2D mousePositionModel = screenToModel(new Vector2D(e.getX(), e.getY()));
        // Drag objects only with primary button
        if (e.isPrimaryButtonDown()) {
            //If dragging is null, start drawing a box for box selection
            if(dragging == null){
                selectionBoxDragging = true;
                requestRedrawRectangle(boxingStartingPosition, new Point2D(e.getX(), e.getY()), selectionBoxColor);
            }
            //else just move the dragging object
            else{
                //if holding an edge
                if(holdingEdge != null){
                    if(mousePositionModel.minus(holdingEdgeStartingPosition).length() > 0.2){
                        CurveControlPoint ctrl = holdingEdge.addCurveControlPoint(mousePositionModel);
                        clearSelection();
                        select(ctrl);
                        select(holdingEdge);
                        selectedCurveControlPoint = true;
                        holdingEdge = null;
                    }
                }
                else{
                    for (Object o : dragging)
                        if (o instanceof IMovable) {
                            Vector2D offset = new Vector2D(
                                    mousePositionModel.getX() - lastMouseX,
                                    mousePositionModel.getY() - lastMouseY
                            );
                            ((IMovable) o).move(offset);
                        }
                }
                // update model position under mouse
                // this must not be done when we are dragging the screen!!!!!
                lastMouseX = mousePositionModel.getX();
                lastMouseY = mousePositionModel.getY();
            }
        }
        else if(e.isSecondaryButtonDown()){
            //if edge is being drawn currently, draw a line between start and mouse
            if(currentEdgeStartingPoint != null){
                Vertex v = (Vertex) currentEdgeStartingPoint;
                Point2D vScreenCords = modelToScreen(new Point2D(v.coordinates.getX(), v.coordinates.getY()));
                this.requestRedraw(vScreenCords, new Point2D(e.getX(), e.getY()));
            }
        }
        // Drag only with middle mouse button
        else if(e.isMiddleButtonDown()){
            offsetX -= (mousePositionModel.getX() - lastMouseX);
            offsetY -= (mousePositionModel.getY() - lastMouseY);
        }

        this.requestRedraw();
    }

    double screenResolutionX = 96d; // dpi
    double screenResolutionY = 96d; // dpi
    double offsetX = -1d;
    double offsetY = -1d;
    double zoomFactor = 1d;

    private double distSquared(Point2D first, Point2D second){
        return Math.pow(first.getX() - second.getX(), 2) + Math.pow(first.getY() - second.getY(), 2);
    }
    public Point2D modelToScreen(Point2D point) {
        Point2D result = new Point2D(
            (point.getX() - offsetX) * zoomFactor * (screenResolutionX / 2.54),
            (point.getY() - offsetY) * zoomFactor * (screenResolutionY / 2.54)
        // dots per inch -> dots per cm
        );
        return result;
    }
    public Vector2D modelToScreen(Vector2D v){
        return new Vector2D(
                (v.getX() - offsetX) * zoomFactor * (screenResolutionX / 2.54),
                (v.getY() - offsetY) * zoomFactor * (screenResolutionY / 2.54));
    }
    public double modelToScreenX(double x){
        return (x - offsetX) * zoomFactor * (screenResolutionX / 2.54);
    }
    public double modelToScreenY(double y){
        return (y - offsetY) * zoomFactor * (screenResolutionY / 2.54);
    }

    public Point2D screenToModel(Point2D point) {
        Point2D result = new Point2D(
            (point.getX() / (screenResolutionX / 2.54) / zoomFactor) + offsetX,
            (point.getY() / (screenResolutionY / 2.54) / zoomFactor) + offsetY
        // dots per inch -> dots per cm
        );
        return result;
    }
    public Vector2D screenToModel(Vector2D point) {
        Vector2D result = new Vector2D(
                (point.getX() / (screenResolutionX / 2.54) / zoomFactor) + offsetX,
                (point.getY() / (screenResolutionY / 2.54) / zoomFactor) + offsetY
                // dots per inch -> dots per cm
        );
        return result;
    }

    private void draw(Point2D from, Point2D to) {
        this.needsRepaintLock.lock();
        try {
            if (needsRepaint) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                draw(gc);

                gc.setStroke(Color.BLACK);
                gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());


                needsRepaint = false;
            }
        } finally {
            this.needsRepaintLock.unlock();
        }
    }

    protected void draw() {
        this.needsRepaintLock.lock();
        try {
            if (needsRepaint) {
                draw(canvas.getGraphicsContext2D());
                needsRepaint = false;
            }
        } finally {
            this.needsRepaintLock.unlock();
        }
    }
    private void drawRectangle(Point2D from, Point2D to, Color color){
        this.needsRepaintLock.lock();
        try {
            if (needsRepaint) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                draw(gc);

                GralogGraphicsContext ggc = new JavaFXGraphicsContext(gc, this);
                ggc.selectionRectangle(from, to, color);

                needsRepaint = false;
            }
        } finally {
            this.needsRepaintLock.unlock();
        }
    }

    private void draw(GraphicsContext gc) {
        // clear
        double w = gc.getCanvas().getWidth();
        double h = gc.getCanvas().getHeight();
        gc.clearRect(0, 0, w, h);
        gc.setFill(Color.rgb(240, 240, 240));
        gc.fillRect(0, 0, w, h);

        // grid
        if (zoomFactor * (screenResolutionX / 2.54) >= 10) {
            gc.setStroke(Color.rgb(225, 225, 225));
            Point2D leftupper = screenToModel(new Point2D(0d, 0d));
            Point2D rightlower = screenToModel(new Point2D(w, h));
            for (double x = leftupper.getX() - (leftupper.getX() % gridSize); x <= rightlower.getX(); x += gridSize) {
                Point2D lineScreen = modelToScreen(new Point2D(x, 0));
                gc.strokeLine(lineScreen.getX(), 0, lineScreen.getX(), h);
            }
            for (double y = leftupper.getY() - (leftupper.getY() % gridSize); y <= rightlower.getY(); y += gridSize) {
                Point2D lineScreen = modelToScreen(new Point2D(0, y));
                gc.strokeLine(0, lineScreen.getY(), w, lineScreen.getY());
            }
        }

        // origin
        gc.setStroke(Color.BLACK);
        Point2D center = modelToScreen(new Point2D(0d, 0d));
        gc.strokeLine(center.getX(), 0, center.getX(), h);
        gc.strokeLine(0, center.getY(), w, center.getY());

        // draw the graph
        GralogGraphicsContext ggc = new JavaFXGraphicsContext(gc, this);
        structure.render(ggc, highlights);
    }

    public void select(Object obj) {
        highlights.select(obj);
        this.fireEvent(new StructurePaneEvent(STRUCTUREPANE_SELECTIONCHANGED));
    }
    public void select(List<IMovable> list) {
        for(IMovable obj : list){
            highlights.select(obj);
        }
        this.fireEvent(new StructurePaneEvent(STRUCTUREPANE_SELECTIONCHANGED));
    }
    public void selectAll(Collection elems) {
        highlights.selectAll(elems);
        this.fireEvent(new StructurePaneEvent(STRUCTUREPANE_SELECTIONCHANGED));
    }

    public void clearSelection() {
        highlights.clearSelection();
        selectedCurveControlPoint = false;
        this.fireEvent(new StructurePaneEvent(STRUCTUREPANE_SELECTIONCHANGED));
    }

    private boolean wasDragging(){
        return wasDraggingPrimary || wasDraggingSecondary || wasDraggingMiddle;
    }
    /**
     * Annotates the given vertex or edge with the given string. Overrides the
     * old annotation for this vertex/edge if present.
     *
     * @param o A vertex or an edge.
     * @param annotation The annotation.
     */
    public void annotate(Object o, String annotation) {
        highlights.annotate(o, annotation);
    }

    /**
     * Removes all annotations from all vertices and all edges.
     */
    public void clearAnnotations() {
        highlights.clearAnnotations();
    }

    @Override
    public void structureChanged(StructureEvent e) {
        this.requestRedraw();
    }

    @Override
    public void vertexChanged(VertexEvent e) {
    }

    @Override
    public void edgeChanged(EdgeEvent e) {
    }
}
