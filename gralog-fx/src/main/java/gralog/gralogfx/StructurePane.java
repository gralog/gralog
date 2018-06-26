/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;

import java.io.File;
import java.util.ArrayList;

import gralog.exportfilter.ExportFilter;
import gralog.exportfilter.ExportFilterDescription;
import gralog.exportfilter.ExportFilterManager;
import gralog.exportfilter.ExportFilterParameters;
import gralog.gralogfx.input.MultipleKeyCombination;
import gralog.preferences.Configuration;
import gralog.gralogfx.threading.ScrollThread;
import gralog.preferences.MenuPrefVariable;
import gralog.preferences.Preferences;
import gralog.structure.*;
import gralog.events.*;
import gralog.rendering.*;
import gralog.gralogfx.events.*;
import gralog.gralogfx.piping.Piping;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import gralog.structure.controlpoints.ControlPoint;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;
import javafx.geometry.Point2D;

import javafx.event.EventType;
import javafx.stage.FileChooser;


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

    private List<Consumer<Highlights>> highlightsSubribers = new ArrayList<>();
    private List<Consumer<Structure>> structureSubscribers = new ArrayList<>();

    public void setOnHighlightsChanged(Consumer<Highlights> highlightsSubribers) {
        this.highlightsSubribers.add(highlightsSubribers);
    }

    public void setOnStructureChanged(Consumer<Structure> structureSubscribers) {
        this.structureSubscribers.add(structureSubscribers);
    }

    private Piping pipeline;

    // private List<SpaceEvent> spaceListeners = new ArrayList<SpaceEvent>();

    Structure structure;
    Canvas canvas;
    Highlights highlights = new Highlights();

    //context menu
    ContextMenu vertexMenu;

    //temporary drawing state variables
    private boolean blockVertexCreationOnRelease = false;

    private Set<Object> dragging = null;
    private boolean wasDraggingPrimary = false;
    private boolean wasDraggingSecondary = false;
    private boolean wasDraggingMiddle = false;

    private Point2D boxingStartingPosition; //model
    private Point2D boxingEndingPosition;   //screen
    private boolean selectionBoxingActive = false;
    private boolean selectionBoxDragging = false;

    private IMovable currentEdgeStartingPoint;
    private boolean drawingEdge = false;

    private boolean selectedCurveControlPoint = false;
    private Edge holdingEdge = null;
    private Vector2D holdingEdgeStartingPosition;

    //UI Threads
    private Thread horizontalScrollThread;
    private Thread verticalScrollThread;

    private double lastMouseX = -1d;
    private double lastMouseY = -1d;

    @MenuPrefVariable(name="Draw Coordinate Grid")
    private boolean hasGrid;

    @MenuPrefVariable(name="Grid Size")
    private double gridSize;

    @MenuPrefVariable(name="Snap to Grid")
    private boolean snapToGrid = true;

    protected Configuration config;

    public StructurePane(Structure structure){
        this(structure, new Configuration());
    }
    public StructurePane(Structure structure, Configuration config) {

        this.config = config;

        //init to config
        hasGrid = config.getValue("StructurePane_showGrid", Boolean::parseBoolean,true);
        gridSize = config.getValue("StructurePane_gridSize", Double::parseDouble, 1.0);
        snapToGrid = config.getValue("StructurePane_snapToGrid", Boolean::parseBoolean,true);

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

        vertexMenu = new ContextMenu();

        MenuItem copy = new MenuItem("Copy");
        copy.setAccelerator(new MultipleKeyCombination(KeyCode.CONTROL, KeyCode.C));
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(e -> {
            deleteSelection();
            this.requestRedraw();
        });
        MenuItem addLoop = new MenuItem("Add loop");

        addLoop.setOnAction(e -> {
            if(highlights.getSelection().size() == 1){
                Vertex v = (Vertex)highlights.getSelection().iterator().next();
                structure.addEdge(v, v, config);
            }
            this.requestRedraw();
        });
        vertexMenu.getItems().addAll(addLoop, copy, delete);

    }

    public Structure getStructure() {
        return structure;
    }

    public void setPiping(Piping pipeline){
        this.pipeline = pipeline;
    }
    public Piping getPiping(){
        return this.pipeline;
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
                        }
                        else if (o instanceof Edge && !selectedCurveControlPoint){
                            structure.removeEdge((Edge) o);
                            clearSelection();
                        }
                        else if (o instanceof ControlPoint){
                            ControlPoint c = ((ControlPoint)o);
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
                    List<Object> duplicates = structure.duplicate(highlights.getSelection(), gridSize);
                    if(snapToGrid){
                        structure.snapToGrid(gridSize);
                    }
                    clearSelection();
                    selectAll(duplicates);
                    this.requestRedraw();
                    break;
                case A:
                    if(e.isControlDown() || e.isMetaDown()){
                        selectAll(structure.getAllMovablesModifiable());
                        this.requestRedraw();
                    }
                    this.requestRedraw();
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
                if(selected instanceof ControlPoint){
                    //select only the edge and the bezier control point.
                    ControlPoint controlPoint = (ControlPoint) selected;
                    selectAllExclusive(controlPoint.parent, selected);
                    dragging = highlights.getSelection();
                    selectedCurveControlPoint = true;
                }else{
                    //reassign selection to object that was not in the list
                    if(!e.isControlDown() && !highlights.isSelected(selected)){
                        selectExclusive(selected);
                    }else{
                        select(selected);
                    }
                    dragging = highlights.getSelection();

                    if(selected instanceof Edge){
                        holdingEdge = (Edge) selected;
                        holdingEdgeStartingPosition = Vector2D.point2DToVector(mousePositionModel);
                    }
                }
            }
            //if selection hit nothing, start boxing
            else if(!e.isControlDown()){
                boxingStartingPosition = new Point2D(lastMouseX, lastMouseY);
                selectionBoxingActive = true;

                //if user is clearing his selection, do not create a vertex
                if(!highlights.getSelection().isEmpty()){
                    blockVertexCreationOnRelease = true;
                }
                clearSelection();
            }
        }else if(e.isSecondaryButtonDown()){
            //start an edge if secondary mouse down on a vertex
            if(selected instanceof Vertex){
                currentEdgeStartingPoint = selected;
            }else if(selected == null){

            }
        }
        vertexMenu.hide();
        this.requestRedraw();
    }
    private void onMouseReleased(MouseEvent e){
        MouseButton b = e.getButton();

        Point2D mousePositionModel = screenToModel(new Point2D(e.getX(), e.getY()));
        lastMouseX = mousePositionModel.getX();
        lastMouseY = mousePositionModel.getY();
        IMovable selected = structure.findObject(lastMouseX, lastMouseY);

        if (dragging != null && hasGrid && snapToGrid) {
            structure.snapToGrid(gridSize);
            this.requestRedraw();
        }
        else if(b == MouseButton.PRIMARY){
            if(selected == null && !selectionBoxDragging && !blockVertexCreationOnRelease){
                Vertex v = structure.addVertex(config);
                v.coordinates = new Vector2D(
                        mousePositionModel.getX(),
                        mousePositionModel.getY()
                );
                if (hasGrid && snapToGrid){
                    v.snapToGrid(gridSize);
                }
                structureSubscribers.forEach(s -> s.accept(structure));
            }
            else if(selectionBoxDragging && selectionBoxingActive &&
                    distSquared(screenToModel(boxingStartingPosition), mousePositionModel) > 0.01){

                Set<IMovable> objs = structure.findObjects(boxingStartingPosition, mousePositionModel);
                selectAll(objs);
            }
        }
        else if(b == MouseButton.SECONDARY){
            if(selected instanceof Vertex){
                //right release on a vertex while drawing an edge = add edge
                if(drawingEdge && currentEdgeStartingPoint != null){
                    structure.addEdge((Vertex)currentEdgeStartingPoint, (Vertex)selected, config);
                }
                //right click opens context menu
                else if(vertexMenu != null){
                    if(!highlights.isSelected(selected)){
                        selectExclusive(selected);
                    }
                    vertexMenu.show(canvas, e.getScreenX(), e.getScreenY());
                }
            }
        }

        blockVertexCreationOnRelease = false;
        wasDraggingPrimary = false;
        wasDraggingSecondary = false;
        selectionBoxingActive = false;
        selectionBoxDragging = false;
        holdingEdge = null;
        dragging = null;

        currentEdgeStartingPoint = null;
        drawingEdge = false;

        if(horizontalScrollThread != null){
            horizontalScrollThread.interrupt();
            horizontalScrollThread = null;
        }
        if(verticalScrollThread != null){
            verticalScrollThread.interrupt();
            verticalScrollThread = null;
        }
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

                boxingEndingPosition = new Point2D(e.getX(), e.getY());
                selectionBoxDragging = true;

                handleHorizontalScrolling(e.getX(), 0, canvas.getWidth());
                handleVerticalScrolling(e.getY(), 0, canvas.getHeight());

            }
            //else just move the dragging object
            else{
                //if holding an edge
                if(holdingEdge != null){
                    tryAddControlPoint(mousePositionModel, holdingEdgeStartingPosition);
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
                drawingEdge = true;
                Vertex v = (Vertex) currentEdgeStartingPoint;
                Point2D vScreenCords = modelToScreen(new Point2D(v.coordinates.getX(), v.coordinates.getY()));
                this.requestRedraw(vScreenCords, new Point2D(e.getX(), e.getY()));
            }
            //drag pane with right drag
            else{
                offsetX -= (mousePositionModel.getX() - lastMouseX);
                offsetY -= (mousePositionModel.getY() - lastMouseY);
            }
        }
        this.requestRedraw();
    }

    double screenResolutionX = 96d; // dpi
    double screenResolutionY = 96d; // dpi
    private double offsetX = -1d;
    private double offsetY = -1d;
    double zoomFactor = 1d;


    /**
     * Offset the whole drawing pane by a given vector. Uses the sync keyword so
     * it can be used by scrolling threads
     */
    public synchronized void move(double offsetX, double offsetY){
        this.offsetX += offsetX;
        this.offsetY += offsetY;
    }

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
        gc.setLineWidth(1);
        // grid
        if (hasGrid && zoomFactor * (screenResolutionX / 2.54) >= 10) {
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

        // draw the graph
        GralogGraphicsContext ggc = new JavaFXGraphicsContext(gc, this);
        structure.render(ggc, highlights);

        //draw the selection box
        if(selectionBoxDragging){
            Point2D boxStartScreen = modelToScreen(boxingStartingPosition);
            ggc.selectionRectangle(boxStartScreen, boxingEndingPosition, selectionBoxColor);
        }
    }
    private void tryAddControlPoint(Vector2D mousePositionModel, Vector2D clickPosition){
        if(mousePositionModel.minus(holdingEdgeStartingPosition).length() > 0.2){
            ControlPoint ctrl = holdingEdge.addControlPoint(mousePositionModel, clickPosition);
            if(ctrl != null){
                clearSelection();
                selectAllExclusive(ctrl, holdingEdge);
                selectedCurveControlPoint = true;
                holdingEdge = null;
            }
        }
    }

    public void select(Object obj) {
        highlights.select(obj);
        highlightsSubribers.forEach(c -> c.accept(highlights));
    }

    public void selectExclusive(Object obj) {
        highlights.clearSelection();
        highlights.select(obj);
        highlightsSubribers.forEach(c -> c.accept(highlights));
    }

    public void selectAll(Collection<?> elems) {
        highlights.selectAll(elems);
        highlightsSubribers.forEach(c -> c.accept(highlights));
    }

    public void deselectAll(Collection<?> elems){
        highlights.deselectAll(elems);
        highlightsSubribers.forEach(c -> c.accept(highlights));
    }

    public void selectAllExclusive(Object... elems) {
        highlights.clearSelection();
        for(Object e : elems){
            highlights.select(e);
        }
        highlightsSubribers.forEach(c -> c.accept(highlights));
    }
    public void deleteSelection(){
        Set<Object> selection = new HashSet<>(highlights.getSelection());
        for (Object o : selection) {
            if (o instanceof Vertex) {
                structure.removeVertex((Vertex) o);
                clearSelection();
            }
            else if (o instanceof Edge && !selectedCurveControlPoint){
                structure.removeEdge((Edge) o);
                clearSelection();
            }
            else if (o instanceof ControlPoint){
                ControlPoint c = ((ControlPoint)o);
                c.parent.removeControlPoint(c);
                highlights.remove(c);
                selectedCurveControlPoint = false;
            }
        }
    }
    public void clearSelection() {
        boolean wasEmpty = false;
        if(highlights.getSelection().isEmpty()){
            wasEmpty = true;
        }
        highlights.clearSelection();
        selectedCurveControlPoint = false;

        if(!wasEmpty){
            highlightsSubribers.forEach(c -> c.accept(highlights));
        }
    }
    private void handleHorizontalScrolling(double cursorX, double from, double to){
        if(cursorX > to && horizontalScrollThread == null){
            horizontalScrollThread = ScrollThread.horizontal(this, true);
            horizontalScrollThread.start();
        }else if(cursorX < from && horizontalScrollThread == null){
            horizontalScrollThread = ScrollThread.horizontal(this, false);
            horizontalScrollThread.start();
        }
        else if(cursorX >= 0 && cursorX <= to && horizontalScrollThread != null){
            horizontalScrollThread.interrupt();
            horizontalScrollThread = null;
        }

    }
    private void handleVerticalScrolling(double cursorY, double from, double to){
        if(cursorY > to && verticalScrollThread == null){
            verticalScrollThread = ScrollThread.vertical(this, true);
            verticalScrollThread.start();
        }else if(cursorY < from && verticalScrollThread == null){
            verticalScrollThread = ScrollThread.vertical(this, false);
            verticalScrollThread.start();
        }
        else if(cursorY >= 0 && cursorY <= to && verticalScrollThread != null){
            verticalScrollThread.interrupt();
            verticalScrollThread = null;
        }

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

    public void saveStructure(){
        if(structure.hasFileReference()){
            try{
                structure.getFileReference();
                File file = new File(structure.getFileReference());

                // has the user selected the native file-type or an export-filter?
                String extension = file.getName(); // unclean way of getting file extension
                int idx = extension.lastIndexOf('.');
                extension = idx > 0 ? extension.substring(idx + 1) : "";

                ExportFilter exportFilter = ExportFilterManager
                        .instantiateExportFilterByExtension(structure.getClass(), extension);
                if (exportFilter != null) {
                    // configure export filter
                    ExportFilterParameters params = exportFilter.getParameters(structure);
                    if (params != null) {
                        ExportFilterStage exportStage = new ExportFilterStage(exportFilter, params, null);
                        exportStage.showAndWait();
                        if (!exportStage.dialogResult)
                            return;
                    }
                    exportFilter.exportGraph(structure, file.getAbsolutePath(), params);
                } else {
                    structure.writeToFile(file.getAbsolutePath());
                }
                System.out.println("Saving " + file.getName() + " to: \t" + file.getPath());
            }catch(Exception ex){
                ExceptionBox x = new ExceptionBox();
                x.showAndWait(ex);
            }
        }else{
            MainWindow.Main_Application.onSaveAs();
        }
    }
    /**
     * Requests to close the current structure pane. After closing,
     * can also execute a given Runnable.
     *
     * @param afterClose The method invoked after this structure closes
     *
     */
    public void requestClose(Runnable afterClose) {
        if (structure.isEmpty()) { // TODO: isEmpty is not enough to know if you can just close
            afterClose.run();
            return;
        }

        //open a close save context window
        Alert con = new Alert(Alert.AlertType.NONE);
        con.setTitle("Close Structure");
        con.setHeaderText("Do you want to discard all changes to this structure?");

        ButtonType save = new ButtonType("Save Changes", ButtonBar.ButtonData.APPLY);
        ButtonType discard = new ButtonType("Discard", ButtonBar.ButtonData.NO);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        con.getButtonTypes().addAll(save, discard, cancel);

        Optional<ButtonType> result = con.showAndWait();

        if(result.get() == save){
            saveStructure();
            if(structure.hasFileReference()){
                Preferences.setString("lastUsedStructure", structure.getFileReference());
            }
            afterClose.run();
        }else if(result.get() == discard){
            afterClose.run();
        }

        //else canceling the closing routine
    }

    public Highlights getHighlights() {
        return highlights;
    }
}
