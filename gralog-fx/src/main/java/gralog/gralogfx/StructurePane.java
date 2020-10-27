/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;


import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLOutput;
import java.util.ArrayList;

import com.rits.cloning.Cloner;
import gralog.dialog.GralogList;
import gralog.exportfilter.ExportFilter;
import gralog.exportfilter.ExportFilterManager;
import gralog.exportfilter.ExportFilterParameters;
import gralog.gralogfx.input.MultipleKeyCombination;
import gralog.gralogfx.panels.ObjectListDisplay;
import gralog.gralogfx.undo.Undo;
import gralog.preferences.Configuration;
import gralog.gralogfx.threading.ScrollThread;
import gralog.preferences.MenuPrefVariable;
import gralog.preferences.Preferences;
import gralog.structure.*;
import gralog.events.*;
import gralog.rendering.*;
import gralog.gralogfx.events.*;
import gralog.gralogfx.piping.Piping;
import gralog.gralogfx.piping.Piping.MessageToConsoleFlag;
import java.util.function.BiFunction;
import java.util.function.BiConsumer;
import java.util.concurrent.CountDownLatch;
import gralog.gralogfx.panels.PluginControlPanel;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import gralog.structure.controlpoints.ControlPoint;
import gralog.structure.controlpoints.ResizeControls;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;
import javafx.geometry.Point2D;

import javafx.event.EventType;


/**
 *
 */
//public class StructurePane extends ScrollPane implements StructureListener {
public class StructurePane extends StackPane implements StructureListener {

    public static final double DISTANCE_CURSOR_STOP_ALIGN = 0.3;
    public static final double DISTANCE_START_ALIGN = 0.3;

    // indices are respectively equal
    public static MenuPrefVariable[] menuVariables;
    public static Field[] menuVariableFields;

    public static LinkedHashSet<Object>  CLIPBOARD;

    static {

        ArrayList<Field> flds = new ArrayList<>();
        for(Field f : StructurePane.class.getDeclaredFields()) {
            if(f.isAnnotationPresent(MenuPrefVariable.class)) {
                flds.add(f);
            }
        }
        menuVariableFields = new Field[flds.size()];

        flds.toArray(menuVariableFields);
        menuVariables = new MenuPrefVariable[menuVariableFields.length];

        for(int i = 0; i < menuVariables.length; i++) {
            menuVariables[i] = menuVariableFields[i].getAnnotation(MenuPrefVariable.class);
        }
    }

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

    Structure<Vertex, Edge> structure;
    Canvas canvas;
    Highlights highlights = new Highlights();

    //context menu
    private ContextMenu vertexMenu;
    private ContextMenu canvasMenu;

    private ObjectListDisplay objectListDisplay;

    //temporary drawing state variables
    private boolean blockVertexCreationOnRelease = false;

    private Set<Object> dragging = null;
    private boolean wasDraggingPrimary = false;
    private boolean wasDraggingSecondary = false;

    private Point2D boxingStartingPosition; //model
    private Point2D boxingEndingPosition;   //screen
    private boolean selectionBoxingActive = false;
    private boolean selectionBoxDragging = false;


    private Vector2D resizeControlDragPosition; //relative to origin
    private boolean alreadyAlignedResize = false;
    private double initialThetaDrag = -1;
    private Vector2D singleVertexDragPosition; //relative to origin
    private boolean alreadyAligned = false;

    private IMovable currentEdgeStartingPoint;
    private boolean drawingEdge = false;

    private boolean selectedCurveControlPoint = false;
    private Edge holdingEdge = null;
    private Vector2D holdingEdgeStartingPosition;

    // Utility
    private Cloner cloner;

    //UI Threads
    private Thread horizontalScrollThread;
    private Thread verticalScrollThread;

    private boolean pipingUnderway = false;

    private double lastMouseX = -1d;
    private double lastMouseY = -1d;

    @MenuPrefVariable(name="Draw Coordinate Grid")
    private boolean hasGrid;

    @MenuPrefVariable(name="Grid Size")
    private double gridSize;

    @MenuPrefVariable(name="Snap to Grid")
    private boolean snapToGrid = true;

    private Configuration config;

    public StructurePane(Structure<Vertex, Edge> structure) {
        this(structure, new Configuration());
    }
    public StructurePane(Structure<Vertex, Edge> structure, Configuration config) {

        this.config = config;
        this.cloner = new Cloner();
        //init to config
        hasGrid = config.getValue("StructurePane_showGrid", Boolean::parseBoolean,true);
        gridSize = config.getValue("StructurePane_gridSize", Double::parseDouble, 1.0);
        snapToGrid = config.getValue("StructurePane_snapToGrid", Boolean::parseBoolean,true);

        this.structure = structure;
        Undo.Record(this.structure); // initial copy

        canvas = new Canvas(500,500);
        this.getChildren().add(canvas);

        // resize canvas with surrounding StructurePane
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());
        canvas.widthProperty().addListener(e -> this.requestRedraw());
        canvas.heightProperty().addListener(e -> this.requestRedraw());

        canvas.setOnScroll(se -> {
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
        copy.setAccelerator(new MultipleKeyCombination(KeyCode.COMPOSE, KeyCode.C));
        copy.setOnAction(e -> {
            copySelectionToClipboard();
        });
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(e -> {
            deleteSelection();
            this.requestRedraw();
        });
        MenuItem addLoop = new MenuItem("Add loop");
        addLoop.setOnAction(e -> {
            if(highlights.getSelection().size() == 1) {
                Vertex v = (Vertex)highlights.getSelection().iterator().next();
                structure.addEdge(v, v, config);
                Undo.Record(structure);
            }
            this.requestRedraw();
        });
        MenuItem addList = new MenuItem("Create new list");

        addList.setOnAction(e -> {
            createListFromSelection();
        });
        vertexMenu.getItems().addAll(addList, new SeparatorMenuItem(), addLoop, copy, delete);
        
        canvasMenu = new ContextMenu();
        MenuItem center = new MenuItem("Center view");
        center.setAccelerator(new MultipleKeyCombination(KeyCode.CONTROL,KeyCode.ALT, KeyCode.C));
        center.setOnAction(e -> {
        	centerView();
        });
        canvasMenu.getItems().add(center);
    }

    public void centerView() {
    	double sumX = 0;
        double sumY = 0;
        for (Vertex v : structure.getVertices()) {
        	sumX += v.getCoordinates().getX();
        	sumY += v.getCoordinates().getY();
        }
        double n = structure.getVertices().size();

        offsetX = 1/n*sumX -  screenResolutionX / 2.54 / 2 / zoomFactor;
        offsetY = 1/n*sumY -  screenResolutionY / 2.54 / 3 / zoomFactor;
        this.requestRedraw();

        //TODO: zoom such that the whole structure can be seen
		double screenResolutionX = 96d; // dpi
		double screenResolutionY = 96d; // dpi
		double offsetX = -1d;
		double offsetY = -1d;
		double zoomFactor = 1d;
		blockVertexCreationOnRelease=true;

    }
    
    public void setObjectListDisplay(ObjectListDisplay obj) {
        objectListDisplay = obj;
    }

    public ObjectListDisplay getObjectListDisplay() {
        return objectListDisplay;
    }
    public void cutSelectionToClipboard() {
        copySelectionToClipboard();
        deleteSelection();
        this.requestRedraw();
    }


    public void copySelectionToClipboard() {
        CLIPBOARD = cloner.deepClone(highlights.getSelection());
        for(Object o : CLIPBOARD) {
            if(o instanceof Vertex) {
                Vertex v = (Vertex)o;
                for(Edge edge : v.getIncidentEdges()) {
                    if(!CLIPBOARD.contains(edge)) {
                        v.disconnectEdge(edge);
                    }
                }
            }
        }
    }

    public void pasteFromClipboard() { pasteFromClipboard(true);}

    public void pasteFromClipboard(boolean redraw) {
        var clipBoardCopy = (new Cloner()).deepClone(CLIPBOARD);
        structure.insertForeignSelection(clipBoardCopy, gridSize);
        Undo.Record(structure);
        highlights.clearSelection();
        selectAll(clipBoardCopy);
        if(redraw)
            this.requestRedraw();
    }

    public void undoStructure() {
        Undo.Revert(structure);
        this.requestRedraw();
    }

    public void redoStructure() {
        Undo.Redo(structure);
        this.requestRedraw();
    }
    
    public void recordStructure() {
    	Undo.Record(structure);
    }

    public Structure getStructure() {
        return structure;
    }
    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public void setPiping(Piping pipeline) {
        this.pipeline = pipeline;
    }
    public Piping getPiping() {
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

    public Piping makeANewPiping(String fileName,BiFunction<String,Piping,StructurePane> initGraph,
                                 BiConsumer<String,MessageToConsoleFlag> sendOutsideMessageToConsole) {
        CountDownLatch waitForPause = new CountDownLatch(1);
        CountDownLatch waitForVertexSelection = new CountDownLatch(1);

        Piping pipeline = new Piping(initGraph,
                this,waitForPause,
                this::handlePlannedPause,
                waitForVertexSelection,
                this::handlePlannedVertexSelection,
                sendOutsideMessageToConsole);

        this.setPiping(pipeline);

        // save the structure for the case, the algorithm changes it
        Undo.Record(structure);

        /* at the moment this is hardcorded but it will soon be made dynamic!!!*/

        Boolean initSuccess = pipeline.externalProcessInit(fileName,"hello world");
        if (!initSuccess) {
            return null;
        }

        pipingUnderway = true;
        pipeline.setFirstMessage(null);
        pipeline.start();
        return pipeline;
        
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
    private void requestRedraw(Runnable r) {
        needsRepaintLock.lock();
        try {
            if (!needsRepaint) {
                Platform.runLater(r);
                needsRepaint = true;
            }
        } finally {
            needsRepaintLock.unlock();
        }
    }

    void alignHorizontallyMean() {
        structure.alignHorizontallyMean(highlights.getSelection());
        structure.snapToGrid(gridSize);
        this.requestRedraw();
    }
    void alignVerticallyMean() {
        structure.alignVerticallyMean(highlights.getSelection());
        structure.snapToGrid(gridSize);
        this.requestRedraw();
    }
    private void setMouseEvents() {
        canvas.setOnMouseClicked(e -> {
        });
        canvas.setOnMousePressed(this::onMousePressed);
        canvas.setOnMouseReleased(this::onMouseReleased);
        canvas.setOnMouseDragged(this::onMouseDragged);
        canvas.setOnKeyPressed(e -> {

            switch (e.getCode()) {
                case DELETE:
                    deleteSelection();
                    Undo.Record(structure);
                    this.requestRedraw();
                    break;
                case X:
                    if(e.isControlDown() || e.isMetaDown()) {
                        cutSelectionToClipboard();
                    }
                    break;
                case C:
                    if(e.isControlDown() || e.isMetaDown()) {
                        copySelectionToClipboard();
                    }else {
                        structure.collapseEdges(highlights.getSelection());
                        this.requestRedraw();
                    }
                    break;
                case V:
                    if(e.isControlDown() || e.isMetaDown()) {
                        if(CLIPBOARD == null) {
                            break;
                        }
                        pasteFromClipboard(false);
                        if(snapToGrid) {
                            structure.snapToGrid(gridSize);
                        }
                        this.requestRedraw();
                    }
                    return;
                case D:
                    List<Object> duplicates = structure.duplicate(highlights.getSelection(), gridSize);
                    if(snapToGrid) {
                        structure.snapToGrid(gridSize);
                    }
                    clearSelection();
                    selectAll(duplicates);
                    this.requestRedraw();
                    break;
                case A:
                    if(e.isControlDown() || e.isMetaDown()) {
                        selectAll(structure.getAllMovablesModifiable());
                        this.requestRedraw();
                    }
                    this.requestRedraw();
                    break;
                case SPACE:
                    Piping myPiping = this.getPiping();
                    if (myPiping != null && myPiping.isInitialized()) {
                        if (myPiping.state == Piping.State.Paused) {

                            this.handlePlay();
                        }else if(myPiping.state == Piping.State.InProgress) {
                            this.handleSpontaneousPause();
                        }else if(myPiping.state == Piping.State.WaitingForSelection) {
                            this.handlePlannedVertexSelection();
                        }
                    }else {
                        System.out.println("piping is null or unit!" + myPiping);
                    }
                    break;
            }
        });
    }

    public boolean handlePlay() {
        Piping myPiping = this.getPiping();
        if (myPiping == null) {
            return false;
        }
        this.pipingUnderway = true;
        myPiping.waitForPauseToBeHandled.countDown();
        CountDownLatch newLatch = new CountDownLatch(1);
        myPiping.setPauseCountDownLatch(newLatch);
        Platform.runLater(()-> {
            PluginControlPanel.notifyPlayRequested();
        });
        return true;
    }

    public boolean handleSkip() {
        Piping myPiping = this.getPiping();
        if (myPiping == null) {
            return false;
        }
        myPiping.skipPressed();
        this.handlePlay();
        return true;
    }

    public boolean handleSpontaneousPause() {
        // this.pluginControlPanel.doVarStuff();
        Piping myPiping = this.getPiping();
        if (myPiping == null) {
            return false;
        }
        this.pipingUnderway = false;
        myPiping.pausePressed();
        Platform.runLater(()-> {
            PluginControlPanel.notifySpontaneousPauseRequested();
        });
        return true;
    }

    public boolean handlePlannedPause() {
        this.pipingUnderway = false;
        Platform.runLater(()-> {
            PluginControlPanel.notifyPlannedPauseRequested(getPiping().trackedVarArgs);
        });

        return true;
    }

    public boolean handleSpontaneousStop() {
        if (this.getPiping() == null) {
            return false;
        }
        this.pipingUnderway = false;
        this.getPiping().spontaneousStop();
        return true;
    }

    public boolean handlePlannedVertexSelection() {
        //todo: this

        return true;
    }

   


    private void onMousePressed(MouseEvent e) {

        Point2D mousePositionModel = screenToModel(new Point2D(e.getX(), e.getY()));

        lastMouseX = mousePositionModel.getX();
        lastMouseY = mousePositionModel.getY();
        IMovable selected = structure.findObject(lastMouseX, lastMouseY);
        //group selection handling for primary mouse button
        if(e.isPrimaryButtonDown()) {

            //if selection hit something, select
            if (selected != null) {
            	canvasMenu.hide();
                //only clear selection if mouse press was not on a bezier control point
                if(selected instanceof ControlPoint) {
                    //select only the edge and the bezier control point.
                    ControlPoint controlPoint = (ControlPoint) selected;
                    selectAllExclusive(controlPoint.parent, selected);
                    dragging = highlights.getSelection();
                    selectedCurveControlPoint = true;
                }
                else if(selected instanceof ResizeControls.RControl) {
                    select(((ResizeControls.RControl)selected).parent.v);
                    dragging = new HashSet<>();
                    dragging.add(selected);

                    resizeControlDragPosition = ((ResizeControls.RControl)selected).
                            position.minus(lastMouseX, lastMouseY);
                }
                else {
                    //reassign selection to object that was not in the list
                    if(!e.isShiftDown() && !highlights.isSelected(selected)) {
                        selectExclusive(selected);
                    }else {
                        select(selected);
                    }
                    dragging = highlights.getSelection();
                    if(dragging.size() == 1 && selected instanceof Vertex) {
                        singleVertexDragPosition = ((Vertex)selected).getCoordinates().minus(lastMouseX, lastMouseY);
                    }
                    if(selected instanceof Edge) {
                        holdingEdge = (Edge) selected;
                        holdingEdgeStartingPosition = Vector2D.point2DToVector(mousePositionModel);
                    }
                }
            }
            //if selection hit nothing, start boxing
            else {
                boxingStartingPosition = new Point2D(lastMouseX, lastMouseY);
                selectionBoxingActive = true;

                //if user is clearing his selection, do not create a vertex
                if(!highlights.getSelection().isEmpty()) {
                    blockVertexCreationOnRelease = true;
                }
                if(!e.isShiftDown())
                    clearSelection();
            }
        }else if(e.isSecondaryButtonDown()) {
            //start an edge if secondary mouse down on a vertex
            if(selected instanceof Vertex) {
            	canvasMenu.hide();
                currentEdgeStartingPoint = selected;
            }
        }
        vertexMenu.hide();
        
        this.requestRedraw();
    }
    private void onMouseReleased(MouseEvent e) {

        MouseButton b = e.getButton();

        Point2D mousePositionModel = screenToModel(new Point2D(e.getX(), e.getY()));
        lastMouseX = mousePositionModel.getX();
        lastMouseY = mousePositionModel.getY();
        IMovable selected = structure.findObject(lastMouseX, lastMouseY);
        
        //Start: handle response to piping wanting a user selected vertex
        if (!wasDraggingPrimary) {
            if (this.getPiping() != null && this.getPiping().getPipingState() == Piping.State.WaitingForSelection && selected != null) {

                this.getPiping().profferSelectedObject(selected);
            }
        }
        //End

        if (canvasMenu.isShowing()) {
        	blockVertexCreationOnRelease = true;
        	canvasMenu.hide();
        }
        if (dragging != null) {
            //Undo.Record(structure);
            if(hasGrid && snapToGrid) {
                structure.snapToGrid(gridSize);
                this.requestRedraw();
            }
        }
        else if(b == MouseButton.PRIMARY) {
            if(selected == null && !selectionBoxDragging && !blockVertexCreationOnRelease && selectionBoxingActive) {

                Vertex v = structure.addVertex(config);
                v.setCoordinates(mousePositionModel.getX(),
                        mousePositionModel.getY());
                if (hasGrid && snapToGrid) {
                    v.snapToGrid(gridSize);
                }
                structureSubscribers.forEach(s -> s.accept(structure));
                Undo.Record(structure);
            }
            else if(selectionBoxDragging && selectionBoxingActive &&
                    distSquared(screenToModel(boxingStartingPosition), mousePositionModel) > 0.01) {

                Set<IMovable> objs = structure.findObjects(boxingStartingPosition, mousePositionModel);
                selectAll(objs);
            }
        }
        else if(b == MouseButton.SECONDARY) {
            if(selected instanceof Vertex) {
                //right release on a vertex while drawing an edge = add edge
                if(drawingEdge && currentEdgeStartingPoint != null) {
                    if(currentEdgeStartingPoint == selected) {
                        vertexMenu.show(canvas, e.getScreenX(), e.getScreenY());
                    }else {
                        structure.addEdge((Vertex)currentEdgeStartingPoint, (Vertex)selected, config);
                        Undo.Record(structure);
                    }
                }
                //right click opens context menu
                else if(vertexMenu != null) {
                    if(!highlights.isSelected(selected)) {
                        selectExclusive(selected);
                    }
                    vertexMenu.show(canvas, e.getScreenX(), e.getScreenY());
                }
            } else if (!wasDraggingSecondary) {
            	canvasMenu.show(canvas, e.getScreenX(), e.getScreenY());
            }
        }

        blockVertexCreationOnRelease = false;
        wasDraggingPrimary = false;
        wasDraggingSecondary = false;
        selectionBoxingActive = false;
        selectionBoxDragging = false;
        holdingEdge = null;
        dragging = null;

        alreadyAlignedResize = false;
        initialThetaDrag = -1;

        currentEdgeStartingPoint = null;
        drawingEdge = false;

        if(horizontalScrollThread != null) {
            horizontalScrollThread.interrupt();
            horizontalScrollThread = null;
        }
        if(verticalScrollThread != null) {
            verticalScrollThread.interrupt();
            verticalScrollThread = null;
        }
        this.requestRedraw();
    }
    private void onMouseDragged(MouseEvent e) {

        if(e.isPrimaryButtonDown()) {
            wasDraggingPrimary = true;
        }
        if(e.isSecondaryButtonDown()) {
            wasDraggingSecondary = true;
        }

        Vector2D mousePositionModel = screenToModel(new Vector2D(e.getX(), e.getY()));
        // Drag objects only with primary button
        if (e.isPrimaryButtonDown()) {
            //If dragging is null, start drawing a box for box selection
            if(dragging == null) {

                boxingEndingPosition = new Point2D(e.getX(), e.getY());
                selectionBoxDragging = true;

                handleHorizontalScrolling(e.getX(), 0, canvas.getWidth());
                handleVerticalScrolling(e.getY(), 0, canvas.getHeight());

            }
            //else just move the dragging object
            else {
                //if holding an edge
                if(holdingEdge != null) {
                    tryAddControlPoint(mousePositionModel, holdingEdgeStartingPosition);
                }
                else {
                    for (Object o : dragging) {
                        if (o instanceof IMovable) {
                            Vector2D offset = new Vector2D(
                                    mousePositionModel.getX() - lastMouseX,
                                    mousePositionModel.getY() - lastMouseY
                            );
                            ((IMovable) o).move(offset);
                        }
                        if(o instanceof ResizeControls.RControl && (e.isMetaDown() || e.isControlDown())) {
                            var c = (ResizeControls.RControl)o;
                            c.position = (new Vector2D(
                                    mousePositionModel.getX(),
                                    mousePositionModel.getY()));
                            if(initialThetaDrag == -1) {
                                Vector2D parentPosition = c.parent.v.getCoordinates();
                                Vector2D rPosition = c.position;
                                initialThetaDrag = rPosition.minus(parentPosition).theta();
                            }
                            tryAlignToDiagonal(c, initialThetaDrag);
                            /*
                            var c = (ResizeControls.RControl)o;


                            Vector2D rel = ((ResizeControls.RControl)o).position.minus(
                                    mousePositionModel.getX(), mousePositionModel.getY());

                            if(resizeControlDragPosition == null)
                                continue;

                            Vector2D diffRel = resizeControlDragPosition.minus(rel);
                            if(diffRel.length() < DISTANCE_CURSOR_STOP_ALIGN) {
                                alreadyAlignedResize = tryAlignToDiagonal(c);
                            }else {

                                ((IMovable)o).move(diffRel);
                                tryAlignToDiagonal(c);
                                alreadyAlignedResize = false;
                            } */
                        }
                        //only align when the difference between initial relative dragging point
                        //and current relative position is small enough
                        if(o instanceof Vertex) {
                            Vector2D rel = ((Vertex)o).getCoordinates().minus(
                                    mousePositionModel.getX(), mousePositionModel.getY());
                            if(singleVertexDragPosition == null)
                                continue;
                            Vector2D diffRel = singleVertexDragPosition.minus(rel);
                            if (dragging.size() == 1) {
                                if(diffRel.length() < DISTANCE_CURSOR_STOP_ALIGN) {
                                    alreadyAligned = tryAlign((Vertex)o, 10);
                                }else { //break alignment
                                    ((IMovable)o).move(diffRel);
                                    tryAlign((Vertex)o, 10);
                                    alreadyAligned = false;
                                }
                            }
                        }
                    }
                }
                // update model position under mouse
                // this must not be done when we are dragging the screen!!!!!
                lastMouseX = mousePositionModel.getX();
                lastMouseY = mousePositionModel.getY();
            }
        }
        else if(e.isSecondaryButtonDown()) {
            //if edge is being drawn currently, draw a line between start and mouse
            if(currentEdgeStartingPoint != null) {
                drawingEdge = true;
                Vertex v = (Vertex) currentEdgeStartingPoint;
                Point2D vScreenCords = modelToScreen(new Point2D(v.getCoordinates().getX(), v.getCoordinates().getY()));
                this.requestRedraw(vScreenCords, new Point2D(e.getX(), e.getY()));
            }
            //drag pane with right drag
            else {
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
    public synchronized void move(double offsetX, double offsetY) {
        this.offsetX += offsetX;
        this.offsetY += offsetY;
    }

    private double distSquared(Point2D first, Point2D second) {
        return Math.pow(first.getX() - second.getX(), 2) + Math.pow(first.getY() - second.getY(), 2);
    }
    public Point2D modelToScreen(Point2D point) {
        return new Point2D(
            (point.getX() - offsetX) * zoomFactor * (screenResolutionX / 2.54),
            (point.getY() - offsetY) * zoomFactor * (screenResolutionY / 2.54)
        // dots per inch -> dots per cm
        );
    }
    public Vector2D modelToScreen(Vector2D v) {
        return new Vector2D(
                (v.getX() - offsetX) * zoomFactor * (screenResolutionX / 2.54),
                (v.getY() - offsetY) * zoomFactor * (screenResolutionY / 2.54));
    }
    public double modelToScreenX(double x) {
        return (x - offsetX) * zoomFactor * (screenResolutionX / 2.54);
    }
    public double modelToScreenY(double y) {
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

                gc.setStroke(Color.GREY);
                gc.setLineWidth(1);
                gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());


                needsRepaint = false;
            }
        } finally {
            this.needsRepaintLock.unlock();
        }
    }

    private void draw(Consumer<GraphicsContext> c) {
        this.needsRepaintLock.lock();
        try {
            if (needsRepaint) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                draw(gc);
                c.accept(gc);
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
        if(selectionBoxDragging && boxingStartingPosition != null) {
            Point2D boxStartScreen = modelToScreen(boxingStartingPosition);
            ggc.selectionRectangle(boxStartScreen, boxingEndingPosition, selectionBoxColor);
        }
    }

    private void tryAddControlPoint(Vector2D mousePositionModel, Vector2D clickPosition) {
        if(mousePositionModel.minus(holdingEdgeStartingPosition).length() > 0.2) {
            ControlPoint ctrl = holdingEdge.addControlPoint(mousePositionModel, clickPosition);
            if(ctrl != null) {
                clearSelection();
                selectAllExclusive(ctrl, holdingEdge);
                selectedCurveControlPoint = true;
                holdingEdge = null;
            }
        }
    }

    private void tryAlignToDiagonal(ResizeControls.RControl c) {
        tryAlignToDiagonal(c, -1);
    }

    private void tryAlignToDiagonal(ResizeControls.RControl c, double thetaForce) {
        Vector2D parentPosition = c.parent.v.getCoordinates();
        Vector2D rPosition = c.position;
        var size = c.parent.v.shape.sizeBox;
        var diff = rPosition.minus(parentPosition);
        double theta = thetaForce == -1 ? diff.theta() : thetaForce;

        if(theta < 90) {
            double scale = diff.multiply(new Vector2D(1, 1))/2;
            var newPos = (new Vector2D(scale, scale)).plus(parentPosition);
            c.move(newPos.minus(c.position));
        }else if(theta < 180) {
            double scale = diff.multiply(new Vector2D(-1, 1))/2;
            var newPos = (new Vector2D(-scale, scale)).plus(parentPosition);
            c.move(newPos.minus(c.position));
        }else if(theta < 270) {
            double scale = diff.multiply(new Vector2D(-1, -1))/2;
            var newPos = (new Vector2D(-scale, -scale)).plus(parentPosition);
            c.move(newPos.minus(c.position));
        }else {
            double scale = diff.multiply(new Vector2D(1, -1))/2;
            var newPos = (new Vector2D(scale, -scale)).plus(parentPosition);
            c.move(newPos.minus(c.position));
        }
        double diffSize = Math.abs(size.width - size.height);

        if(size.width > size.height) {
            if(c.position.getY() > c.parentCenter().getY()) {
                c.move(new Vector2D(0, diffSize));
            }else {
                c.move(new Vector2D(0, -diffSize));
            }
        }
        else {
            if(c.position.getX() > c.parentCenter().getX()) {
                c.move(new Vector2D(diffSize, 0));
            }else {
                c.move(new Vector2D(-diffSize, 0));
            }
        }
        this.requestRedraw(() -> this.draw(gc -> drawProportionalResizeLines(gc, c)));
    }
    /**
     * Aligns the given vertex to a nearby node. Also Draws helper
     * lines (node alignment) if the vertex is close enough to the x/y
     * coordinate of a vertex below a specified radius
     * @param vertex The vertex that wants to be aligned (aligner)
     * @param radius The max radius of the alignee (?)
     */
    private boolean tryAlign(Vertex vertex, double radius) {
        double maxDelta = DISTANCE_START_ALIGN;
        boolean xAligned = false,
                yAligned = false;
        Point2D dummy = new Point2D(0,0);

        Point2D horizontalP1 = dummy, horizontalP2 = dummy,
                verticalP1 = dummy, verticalP2 = dummy;
        for(Object v : structure.getVertices()) {
            Vertex x = ((Vertex)v);
            if(x == vertex) {
                continue;
            }
            if(x.getCoordinates().minus(vertex.getCoordinates()).length() < radius) {
                final double xdiff = x.getCoordinates().getX() - vertex.getCoordinates().getX();

                if(!xAligned && Math.abs(xdiff) < maxDelta) {
                    horizontalP1 = modelToScreen(new Point2D(x.getCoordinates().getX(), x.getCoordinates().getY()));
                    horizontalP2 = modelToScreen(new Point2D(x.getCoordinates().getX(), vertex.getCoordinates().getY()));
                    vertex.setCoordinates(x.getCoordinates().getX(), vertex.getCoordinates().getY());
                    xAligned = true;
                }
                else if(!yAligned && Math.abs(x.getCoordinates().getY() - vertex.getCoordinates().getY()) < maxDelta) {
                    verticalP1 = modelToScreen(new Point2D(x.getCoordinates().getX(), x.getCoordinates().getY()));
                    verticalP2 = modelToScreen(new Point2D(vertex.getCoordinates().getX(), x.getCoordinates().getY()));
                    vertex.setCoordinates(vertex.getCoordinates().getX(), x.getCoordinates().getY());
                    yAligned = true;
                }
                if(xAligned && yAligned) {
                    break;
                }
            }
        }
        if(xAligned || yAligned) {
            boolean finalXAligned = xAligned;
            boolean finalYAligned = yAligned;
            Point2D finalHP1 = horizontalP1;
            Point2D finalHP2 = horizontalP2;
            Point2D finalVP1 = verticalP1;
            Point2D finalVP2 = verticalP2;
            this.requestRedraw(() -> this.draw(gc -> {
                if(finalXAligned) {
                    drawAlignmentLines(gc, finalHP1, finalHP2);
                }
                if(finalYAligned) {
                    drawAlignmentLines(gc, finalVP1, finalVP2);
                }
            }));
        }
        return xAligned || yAligned;
    }
    private void drawProportionalResizeLines(GraphicsContext gc, ResizeControls.RControl c) {
        gc.setLineWidth(0.03 * zoomFactor * screenResolutionX / 2.54);
        gc.setStroke(Color.GRAY);
        gc.setLineDashes(0.03 * zoomFactor * screenResolutionX / 2.54,
                0.15 * zoomFactor * screenResolutionX / 2.54);

        Vector2D from = modelToScreen(c.position);
        Vector2D to = modelToScreen(c.getDiagonalSibling().position);
        gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());

        from = modelToScreen(c.getNextSibling().position);
        to = modelToScreen(c.getPreviousSibling().position);
        gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());

        gc.setLineDashes();
    }
    private void drawAlignmentLines(GraphicsContext gc, Point2D from, Point2D to) {
        gc.setLineWidth(0.03 * zoomFactor * screenResolutionX / 2.54);
        gc.setStroke(Color.GRAY);
        gc.setLineDashes(0.03 * zoomFactor * screenResolutionX / 2.54,
                0.15 * zoomFactor * screenResolutionX / 2.54);
        gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());
        gc.setLineDashes();
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

    public void deselectAll(Collection<?> elems) {
        highlights.deselectAll(elems);
        highlightsSubribers.forEach(c -> c.accept(highlights));
    }

    private void selectAllExclusive(Object... elems) {
        highlights.clearSelection();
        for(Object e : elems) {
            highlights.select(e);
        }
        highlightsSubribers.forEach(c -> c.accept(highlights));
    }
    private void deleteSelection() {
        Set<Object> selection = new HashSet<>(highlights.getSelection());
        for (Object o : selection) {
            if (o instanceof Vertex) {
                structure.removeVertex((Vertex) o);
                clearSelection();
            }
            else if (o instanceof Edge && !selectedCurveControlPoint) {
                structure.removeEdge((Edge) o);
                clearSelection();
            }
            else if (o instanceof ControlPoint) {
                ControlPoint c = ((ControlPoint)o);
                c.parent.removeControlPoint(c);
                highlights.remove(c);
                selectedCurveControlPoint = false;
            }
        }
    }

    private void createListFromSelection() {
        GralogList<Vertex> g = new GralogList<>(objectListDisplay.getUniqueDefaultName());
        g.overrideToString(v -> "" + v.getId());
        for(Object o : highlights.getSelection()) {
            if(o instanceof Vertex) {
                g.add((Vertex)o);
            }
        }
        objectListDisplay.vertexList.add(g);
    }

    public void clearSelection() {
        boolean wasEmpty = false;
        if(highlights.getSelection().isEmpty()) {
            wasEmpty = true;
        }
        highlights.clearSelection();
        selectedCurveControlPoint = false;

        if(!wasEmpty) {
            highlightsSubribers.forEach(c -> c.accept(highlights));
        }
    }
    private void handleHorizontalScrolling(double cursorX, double from, double to) {
        if(cursorX > to && horizontalScrollThread == null) {
            horizontalScrollThread = ScrollThread.horizontal(this, true);
            horizontalScrollThread.start();
        }else if(cursorX < from && horizontalScrollThread == null) {
            horizontalScrollThread = ScrollThread.horizontal(this, false);
            horizontalScrollThread.start();
        }
        else if(cursorX >= 0 && cursorX <= to && horizontalScrollThread != null) {
            horizontalScrollThread.interrupt();
            horizontalScrollThread = null;
        }

    }
    private void handleVerticalScrolling(double cursorY, double from, double to) {
        if(cursorY > to && verticalScrollThread == null) {
            verticalScrollThread = ScrollThread.vertical(this, true);
            verticalScrollThread.start();
        }else if(cursorY < from && verticalScrollThread == null) {
            verticalScrollThread = ScrollThread.vertical(this, false);
            verticalScrollThread.start();
        }
        else if(cursorY >= 0 && cursorY <= to && verticalScrollThread != null) {
            verticalScrollThread.interrupt();
            verticalScrollThread = null;
        }

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


    private void saveStructure() {
        if(structure.hasFileReference()) {
            try {
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
            }catch(Exception ex) {
                ExceptionBox x = new ExceptionBox();
                x.showAndWait(ex);
            }
        }else {
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

        if(!Preferences.getBoolean("Develop_requireConfirmation", true)) {
            afterClose.run();
            return;
        }

        ButtonType save = new ButtonType("Save Changes", ButtonBar.ButtonData.APPLY);
        ButtonType discard = new ButtonType("Discard", ButtonBar.ButtonData.NO);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        String message = "You have made changes to this structure. Do you want to save them?";

        Alert alert = createAlertWithOptOut(Alert.AlertType.CONFIRMATION, "Exit", null,
                message, "Never. (dev)",
                param -> Preferences.setBoolean("Develop_requireConfirmation", !param), save, discard, cancel);

        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == save) {
            saveStructure();
            if(structure.hasFileReference()) {
                Preferences.setString("lastUsedStructure", structure.getFileReference());
            }
            afterClose.run();
        }else if(result.get() == discard) {
            afterClose.run();
        }
        //else canceling the closing routine
    }

    public Highlights getHighlights() {
        return highlights;
    }

    /**
     * Taken from https://stackoverflow.com/a/36949596
     */
    private static Alert createAlertWithOptOut(Alert.AlertType type, String title, String headerText,
                                              String message, String optOutMessage, Consumer<Boolean> optOutAction,
                                              ButtonType... buttonTypes) {
        Alert alert = new Alert(type);
        // Need to force the alert to layout in order to grab the graphic,
        // as we are replacing the dialog pane with a custom pane
        alert.getDialogPane().applyCss();
        Node graphic = alert.getDialogPane().getGraphic();
        // Create a new dialog pane that has a checkbox instead of the hide/show details button
        // Use the supplied callback for the action of the checkbox
        alert.setDialogPane(new DialogPane() {
            @Override
            protected Node createDetailsButton() {
                CheckBox optOut = new CheckBox();
                optOut.setText(optOutMessage);
                optOut.setOnAction(e -> optOutAction.accept(optOut.isSelected()));
                return optOut;
            }
        });
        alert.getDialogPane().getButtonTypes().addAll(buttonTypes);
        alert.getDialogPane().setContentText(message);
        // Fool the dialog into thinking there is some expandable content
        // a Group won't take up any space if it has no children
        alert.getDialogPane().setExpandableContent(new Group());
        alert.getDialogPane().setExpanded(true);
        // Reset the dialog graphic using the default style
        alert.getDialogPane().setGraphic(graphic);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        return alert;
    }
}
