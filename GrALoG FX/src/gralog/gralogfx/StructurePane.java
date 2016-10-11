/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.gralogfx;

import gralog.structure.*;
import gralog.events.*;
import gralog.rendering.*;
import gralog.gralogfx.events.*;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;
import javafx.geometry.Point2D;

import javafx.event.EventType;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;

/**
 *
 */
//public class StructurePane extends ScrollPane implements StructureListener {
public class StructurePane extends StackPane implements StructureListener {

    boolean needsRepaint = true;
    Lock needsRepaintLock = new ReentrantLock();

    static final EventType<StructurePaneEvent> ALL_STRUCTUREPANE_EVENTS
            = new EventType<>("ALL_STRUCTUREPANE_EVENTS");
    static final EventType<StructurePaneEvent> STRUCTUREPANE_SELECTIONCHANGED
            = new EventType<>(ALL_STRUCTUREPANE_EVENTS, "STRUCTUREPANE_SELECTIONCHANGED");

    public void setOnSelectionChanged(EventHandler<StructurePaneEvent> handler) {
        this.setEventHandler(STRUCTUREPANE_SELECTIONCHANGED, handler);
    }

    Structure structure;
    Canvas canvas;

    Set<Object> selection = new HashSet<>();
    Set<Object> dragging = null;
    double lastMouseX = -1d;
    double lastMouseY = -1d;

    double gridSize = 1.0; // cm
    boolean snapToGrid = true;

    public StructurePane(Structure structure) {
        this.structure = structure;
        canvas = new Canvas(500, 500);

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

        setSelectMode();
    }

    public void requestRedraw() {
        needsRepaintLock.lock();
        try {
            if (!needsRepaint) {
                Platform.runLater(() -> {
                    this.draw();
                });
                needsRepaint = true;
            }
        }
        finally {
            needsRepaintLock.unlock();
        }
    }

    public void requestRedraw(double screenX, double screenY) {
        needsRepaintLock.lock();
        try {
            if (!needsRepaint) {
                Platform.runLater(() -> {
                    this.draw(screenX, screenY);
                });
                needsRepaint = true;
            }
        }
        finally {
            needsRepaintLock.unlock();
        }
    }

    public void setSelectMode() {
        canvas.setOnMousePressed(e -> {
            MouseEvent me = (MouseEvent) e;
            Point2D mousePositionModel = screenToModel(new Point2D(me.getX(), me.getY()));
            lastMouseX = (double) mousePositionModel.getX();
            lastMouseY = (double) mousePositionModel.getY();

            if (!e.isControlDown())
                clearSelection();

            IMovable selected = structure.findObject(lastMouseX, lastMouseY);
            if (selected != null) {
                select(selected);
                dragging = selection;
            }
        });
        canvas.setOnMouseReleased(e -> {
            if (dragging != null && snapToGrid) {
                structure.snapToGrid(gridSize);
                this.requestRedraw();
            }
            dragging = null;
        });
        canvas.setOnMouseDragged(e -> {
            MouseEvent me = (MouseEvent) e;
            Point2D mousePositionModel = screenToModel(new Point2D(me.getX(), me.getY()));
            if (dragging != null) {
                for (Object o : dragging)
                    if (o instanceof IMovable) {
                        Vector2D offset = new Vector2D(
                                mousePositionModel.getX() - lastMouseX,
                                mousePositionModel.getY() - lastMouseY
                        );
                        ((IMovable) o).move(offset);
                    }
                // update model position under mouse
                // this must not be done when we are dragging the screen!!!!!
                lastMouseX = mousePositionModel.getX();
                lastMouseY = mousePositionModel.getY();
            }
            else {                                                 // draging the screen
                offsetX -= (mousePositionModel.getX() - lastMouseX);
                offsetY -= (mousePositionModel.getY() - lastMouseY);
            }

            this.requestRedraw();
        });
        canvas.setOnKeyPressed(e -> {
            KeyEvent ke = (KeyEvent) e;
            switch (ke.getCode()) {
                case DELETE:
                    for (Object o : selection) {
                        if (o instanceof Vertex)
                            structure.removeVertex((Vertex) o);
                        else if (o instanceof Edge)
                            structure.removeEdge((Edge) o);
                    }

                    clearSelection();
                    this.requestRedraw();
                    break;
            }
        });
    }

    public void setVertexCreationMode() {
        canvas.setOnMousePressed(e -> {
            MouseEvent me = (MouseEvent) e;
            Point2D mousePositionModel = screenToModel(new Point2D(me.getX(), me.getY()));
            lastMouseX = mousePositionModel.getX();
            lastMouseY = mousePositionModel.getY();

            Vertex v = structure.createVertex();
            v.coordinates = new Vector2D(
                    mousePositionModel.getX(),
                    mousePositionModel.getY()
            );
            if (snapToGrid)
                v.snapToGrid(gridSize);

            structure.addVertex(v);
            clearSelection();
            select(v);

            this.requestRedraw();
        });
        canvas.setOnMouseReleased(e -> {
        });
        canvas.setOnMouseDragged(e -> {
        });
        canvas.setOnKeyReleased(e -> {
        });

    }

    public void setEdgeCreationMode() {
        canvas.setOnMousePressed(e -> {
            MouseEvent me = (MouseEvent) e;
            Point2D mousePositionModel = screenToModel(new Point2D(me.getX(), me.getY()));
            lastMouseX = mousePositionModel.getX();
            lastMouseY = mousePositionModel.getY();

            Object SelectionTemp = structure.findObject(lastMouseX, lastMouseY);
            if (SelectionTemp == null)
                return;

            clearSelection();
            dragging = null;
            if (SelectionTemp instanceof Vertex)
                select(SelectionTemp);
            else if (SelectionTemp instanceof Edge) {
                EdgeIntermediatePoint intermediatepoint = ((Edge) SelectionTemp).addIntermediatePoint(lastMouseX, lastMouseY);
                select(intermediatepoint);
                dragging = selection;
            }

            this.requestRedraw();
        });
        canvas.setOnMouseReleased(e -> {
            MouseEvent me = (MouseEvent) e;
            Point2D mousePositionModel = screenToModel(new Point2D(me.getX(), me.getY()));
            lastMouseX = mousePositionModel.getX();
            lastMouseY = mousePositionModel.getY();
            dragging = null;

            Object releasedOver = structure.findObject(lastMouseX, lastMouseY);
            if (releasedOver != null && (releasedOver instanceof Vertex)) {
                for (Object o : selection) {
                    if (!(o instanceof Vertex))
                        continue;
                    Edge edge = structure.createEdge((Vertex) o, (Vertex) releasedOver);
                    structure.addEdge(edge);
                }
                clearSelection();
            }
            this.requestRedraw();
        });
        canvas.setOnMouseDragged(e -> {
            MouseEvent me = (MouseEvent) e;
            if (!selection.isEmpty() && dragging == null) {
                this.requestRedraw(me.getX(), me.getY());
            }
            else {
                Point2D mousePositionModel = screenToModel(new Point2D(me.getX(), me.getY()));
                if (dragging != null) {
                    for (Object o : dragging)
                        if (o instanceof IMovable) {
                            Vector2D offset = new Vector2D(
                                    mousePositionModel.getX() - lastMouseX,
                                    mousePositionModel.getY() - lastMouseY
                            );
                            ((IMovable) o).move(offset);
                        }
                    // update model position under mouse
                    // this must not be done when we are dragging the screen!!!!!
                    lastMouseX = mousePositionModel.getX();
                    lastMouseY = mousePositionModel.getY();
                }
                else {                                                 // draging the screen
                    offsetX -= (mousePositionModel.getX() - lastMouseX);
                    offsetY -= (mousePositionModel.getY() - lastMouseY);
                }

                this.requestRedraw();
            }
        });
        canvas.setOnKeyReleased(e -> {
        });
    }

    double screenResolutionX = 96d; // dpi
    double screenResolutionY = 96d; // dpi
    double offsetX = -1d;
    double offsetY = -1d;
    double zoomFactor = 1d;

    public Point2D modelToScreen(Point2D point) {
        Point2D result = new Point2D(
                (point.getX() - offsetX) * zoomFactor * (screenResolutionX / 2.54),
                (point.getY() - offsetY) * zoomFactor * (screenResolutionY / 2.54)
        // dots per inch -> dots per cm
        );
        return result;
    }

    public Point2D screenToModel(Point2D point) {
        Point2D result = new Point2D(
                (point.getX() / (screenResolutionX / 2.54) / zoomFactor) + offsetX,
                (point.getY() / (screenResolutionY / 2.54) / zoomFactor) + offsetY
        // dots per inch -> dots per cm
        );
        return result;
    }

    public void draw(double mouseScreenX, double mouseScreenY) {
        this.needsRepaintLock.lock();
        try {
            if (needsRepaint) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                draw(gc);

                gc.setStroke(Color.BLACK);
                for (Object o : selection) {
                    if (!(o instanceof Vertex))
                        continue;
                    Vertex v = (Vertex) o;
                    Point2D selectionScreen = modelToScreen(new Point2D(v.coordinates.get(0), v.coordinates.get(1)));
                    gc.strokeLine(selectionScreen.getX(), selectionScreen.getY(), mouseScreenX, mouseScreenY);
                }

                needsRepaint = false;
            }
        }
        finally {
            this.needsRepaintLock.unlock();
        }
    }

    public void draw() {
        this.needsRepaintLock.lock();
        try {
            if (needsRepaint) {
                draw(canvas.getGraphicsContext2D());
                needsRepaint = false;
            }
        }
        finally {
            this.needsRepaintLock.unlock();
        }
    }

    protected void draw(GraphicsContext gc) {
        // clear
        double w = gc.getCanvas().getWidth();
        double h = gc.getCanvas().getHeight();
        gc.clearRect(0, 0, w, h);
        gc.setFill(Color.rgb(0xFA, 0xFB, 0xFF));
        gc.fillRect(0, 0, w, h);

        // grid
        if (zoomFactor * (screenResolutionX / 2.54) >= 10) {
            gc.setStroke(Color.rgb(0xcc, 0xcc, 0xcc));
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
        structure.render(ggc, this.selection);
    }

    public void select(Object obj) {
        selection.add(obj);
        this.fireEvent(new StructurePaneEvent(STRUCTUREPANE_SELECTIONCHANGED));
    }

    public void selectAll(Collection elems) {
        for (Object o : elems)
            selection.add(o);
        this.fireEvent(new StructurePaneEvent(STRUCTUREPANE_SELECTIONCHANGED));
    }

    public void clearSelection() {
        selection.clear();
        this.fireEvent(new StructurePaneEvent(STRUCTUREPANE_SELECTIONCHANGED));
    }

    public void structureChanged(StructureEvent e) {
        this.requestRedraw();
    }

    public void vertexChanged(VertexEvent e) {
    }

    public void edgeChanged(EdgeEvent e) {
    }
}
