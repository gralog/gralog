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
import java.util.Vector;
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
import javafx.event.Event;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;


/**
 *
 * @author viktor
 */
//public class StructurePane extends ScrollPane implements StructureListener {
public class StructurePane extends StackPane implements StructureListener {
    
    boolean NeedsRepaint = true;
    Lock NeedsRepaintLock = new ReentrantLock();
    
    static EventType<StructurePaneEvent> ALL_STRUCTUREPANE_EVENTS = new EventType<>("ALL_STRUCTUREPANE_EVENTS");
    static EventType<StructurePaneEvent> STRUCTUREPANE_SELECTIONCHANGED = new EventType<>(ALL_STRUCTUREPANE_EVENTS, "STRUCTUREPANE_SELECTIONCHANGED");
    public void setOnSelectionChanged(EventHandler<StructurePaneEvent> handler) {
        this.setEventHandler(STRUCTUREPANE_SELECTIONCHANGED, handler);
    }
    
    Structure structure;
    Canvas canvas;
    
    Set<Object> Selection = new HashSet<Object>();
    Set<Object> Dragging = null;
    Double LastMouseX = -1d;
    Double LastMouseY = -1d;
    
    Double GridSize = 1.0; // cm
    Boolean SnapToGrid = true;
    
    
    public StructurePane(Structure structure) {
        this.structure = structure;
        canvas = new Canvas(500,500);

        this.getChildren().add(canvas);
        // resize canvas with surrounding StructurePane
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());
        canvas.widthProperty().addListener(e -> this.RequestRedraw());
        canvas.heightProperty().addListener(e -> this.RequestRedraw());
        
        canvas.setOnScroll(e->{
            ScrollEvent se = (ScrollEvent)e;
            
            Point2D oldMousePos = ScreenToModel(new Point2D(se.getX(), se.getY()));
            ZoomFactor *= Math.pow(1.2d, se.getDeltaY()/40d);
            Point2D newMousePos = ScreenToModel(new Point2D(se.getX(), se.getY()));
            
            OffsetX += oldMousePos.getX() - newMousePos.getX(); // sweet :-)
            OffsetY += oldMousePos.getY() - newMousePos.getY();
            this.RequestRedraw();
        });
        
        canvas.setFocusTraversable(true);
        canvas.addEventFilter(MouseEvent.ANY, (e) -> canvas.requestFocus());

        SetSelectMode();
    }
    
    
    public void RequestRedraw()
    {
        NeedsRepaintLock.lock();
        if(!NeedsRepaint) {
            Platform.runLater(()->{this.draw();});
            NeedsRepaint = true;
        }
        NeedsRepaintLock.unlock();
    }
    public void RequestRedraw(Double screenX, Double screenY)
    {
        NeedsRepaintLock.lock();
        if(!NeedsRepaint) {
            Platform.runLater(()->{this.draw(screenX,screenY);});
            NeedsRepaint = true;
        }
        NeedsRepaintLock.unlock();
    }
    
    
    public void SetSelectMode()
    {
        canvas.setOnMousePressed(e -> {
            MouseEvent me = (MouseEvent)e;
            Point2D mousePositionModel = ScreenToModel(new Point2D(me.getX(), me.getY()));
            LastMouseX = (Double)mousePositionModel.getX();
            LastMouseY = (Double)mousePositionModel.getY();
            
            if(!e.isControlDown())
                Selection.clear();
            
            IMovable selected = structure.FindObject((Double)LastMouseX, (Double)LastMouseY);
            if(selected != null)
            {
                //if(!Selection.contains(selected))
                    Selection.add(selected);
                //else
                //    Selection.remove(selected);
                
                Dragging = Selection;
            }
            this.fireEvent(new StructurePaneEvent(STRUCTUREPANE_SELECTIONCHANGED));
        });
        canvas.setOnMouseReleased(e -> {
            if(Dragging != null && SnapToGrid)
            {
                structure.SnapToGrid(GridSize);
                this.RequestRedraw();
            }
            Dragging = null;
        });
        canvas.setOnMouseDragged(e -> {
            MouseEvent me = (MouseEvent)e;
            Point2D mousePositionModel = ScreenToModel(new Point2D(me.getX(), me.getY()));
            if(Dragging != null)
            {
                for(Object o : Dragging)
                    if(o instanceof IMovable)
                    {
                        Vector<Double> offsets = new Vector<Double>();
                        offsets.add(mousePositionModel.getX() - LastMouseX);
                        offsets.add(mousePositionModel.getY() - LastMouseY);
                        ((IMovable)o).Move(offsets);
                    }
                // update model position under mouse
                // this must not be done when we are dragging the screen!!!!!
                LastMouseX = (Double)mousePositionModel.getX();
                LastMouseY = (Double)mousePositionModel.getY();
            } else {                                                 // draging the screen
                OffsetX -= (mousePositionModel.getX() - LastMouseX);
                OffsetY -= (mousePositionModel.getY() - LastMouseY);
            }
            
            this.RequestRedraw();
        });
        canvas.setOnKeyPressed(e -> {
            KeyEvent ke = (KeyEvent)e;
            switch(ke.getCode())
            {
                case DELETE:
                    for(Object o : Selection)
                    {
                        if(o instanceof Vertex)
                            structure.RemoveVertex((Vertex)o);
                        else if(o instanceof Edge)
                            structure.RemoveEdge((Edge)o);
                    }
                    
                    Selection.clear();
                    this.fireEvent(new StructurePaneEvent(STRUCTUREPANE_SELECTIONCHANGED));
                    this.RequestRedraw();
                    break;
            }
        });
    }

    public void SetVertexCreationMode()
    {
        canvas.setOnMousePressed(e -> {
            MouseEvent me = (MouseEvent)e;
            Point2D mousePositionModel = ScreenToModel(new Point2D(me.getX(), me.getY()));
            LastMouseX = (Double)mousePositionModel.getX();
            LastMouseY = (Double)mousePositionModel.getY();

            Vertex v = structure.CreateVertex();
            v.Coordinates.add(mousePositionModel.getX());
            v.Coordinates.add(mousePositionModel.getY());
            if(SnapToGrid)
                v.SnapToGrid(GridSize);
            
            structure.AddVertex(v);
            Selection.clear();
            Selection.add(v);
            
            this.fireEvent(new StructurePaneEvent(STRUCTUREPANE_SELECTIONCHANGED));
            this.RequestRedraw();
        });
        canvas.setOnMouseReleased(e -> {});
        canvas.setOnMouseDragged(e -> {});
        canvas.setOnKeyReleased(e -> {});

    }    
    
    public void SetEdgeCreationMode()
    {
        canvas.setOnMousePressed(e -> {
            MouseEvent me = (MouseEvent)e;
            Point2D mousePositionModel = ScreenToModel(new Point2D(me.getX(), me.getY()));
            LastMouseX = (Double)mousePositionModel.getX();
            LastMouseY = (Double)mousePositionModel.getY();
            
            Object SelectionTemp = structure.FindObject(LastMouseX, LastMouseY);
            if(SelectionTemp == null)
                return;
            
            Selection.clear();
            Dragging = null;
            if(SelectionTemp instanceof Vertex)
                Selection.add(SelectionTemp);
            else if(SelectionTemp instanceof Edge) {
                EdgeIntermediatePoint intermediatepoint = ((Edge)SelectionTemp).addIntermediatePoint(LastMouseX, LastMouseY);
                Selection.add(intermediatepoint);
                Dragging = Selection;
            }
            
            this.fireEvent(new StructurePaneEvent(STRUCTUREPANE_SELECTIONCHANGED));
            this.RequestRedraw();
        });
        canvas.setOnMouseReleased(e -> {
            MouseEvent me = (MouseEvent)e;
            Point2D mousePositionModel = ScreenToModel(new Point2D(me.getX(), me.getY()));
            LastMouseX = (Double)mousePositionModel.getX();
            LastMouseY = (Double)mousePositionModel.getY();
            Dragging = null;
            
            Object releasedOver = structure.FindObject((Double)LastMouseX, (Double)LastMouseY);
            if(releasedOver != null && (releasedOver instanceof Vertex))
            {
                for(Object o : Selection)
                {
                    if(!(o instanceof Vertex))
                        continue;
                    Edge edge = structure.CreateEdge((Vertex)o, (Vertex)releasedOver);
                    structure.AddEdge(edge);
                }
                Selection.clear();
                this.fireEvent(new StructurePaneEvent(STRUCTUREPANE_SELECTIONCHANGED));
            }
            this.RequestRedraw();
        });
        canvas.setOnMouseDragged(e -> {
            MouseEvent me = (MouseEvent)e;
            if(!Selection.isEmpty() && Dragging == null) {
                this.RequestRedraw(me.getX(), me.getY());
            } else {
                Point2D mousePositionModel = ScreenToModel(new Point2D(me.getX(), me.getY()));
                if(Dragging != null)
                {
                    for(Object o : Dragging)
                        if(o instanceof IMovable)
                        {
                            Vector<Double> offsets = new Vector<Double>();
                            offsets.add(mousePositionModel.getX() - LastMouseX);
                            offsets.add(mousePositionModel.getY() - LastMouseY);
                            ((IMovable)o).Move(offsets);
                        }
                    // update model position under mouse
                    // this must not be done when we are dragging the screen!!!!!
                    LastMouseX = (Double)mousePositionModel.getX();
                    LastMouseY = (Double)mousePositionModel.getY();
                } else {                                                 // draging the screen
                    OffsetX -= (mousePositionModel.getX() - LastMouseX);
                    OffsetY -= (mousePositionModel.getY() - LastMouseY);
                }

                this.RequestRedraw();                
            }
        });
        canvas.setOnKeyReleased(e -> {});
    }
    
    
    
    double ScreenResolutionX = 96d; // dpi
    double ScreenResolutionY = 96d; // dpi
    double OffsetX = -1d;
    double OffsetY = -1d;
    double ZoomFactor = 1d;
    
    public Point2D ModelToScreen(Point2D point)
    {
        Point2D result = new Point2D(
            (point.getX() - OffsetX) * ZoomFactor * (ScreenResolutionX / 2.54),
            (point.getY() - OffsetY) * ZoomFactor * (ScreenResolutionY / 2.54)
                                                 // dots per inch -> dots per cm
        );
        return result;
    }
    
    public Point2D ScreenToModel(Point2D point)
    {
        Point2D result = new Point2D(
            (point.getX() / (ScreenResolutionX/2.54) / ZoomFactor) + OffsetX,
            (point.getY() / (ScreenResolutionY/2.54) / ZoomFactor) + OffsetY
                       // dots per inch -> dots per cm
        );
        return result;
    }
    
    
    

    public void draw(Double MouseScreenX, Double MouseScreenY) {
        
        this.NeedsRepaintLock.lock();
        if(NeedsRepaint) {
            
            GraphicsContext gc = canvas.getGraphicsContext2D();
            draw(gc);

            gc.setStroke(Color.BLACK);
            for(Object o : Selection)
            {
                if(!(o instanceof Vertex))
                    continue;
                Vertex v = (Vertex)o;
                Point2D selectionScreen = ModelToScreen(new Point2D(v.Coordinates.get(0), v.Coordinates.get(1)));
                gc.strokeLine(selectionScreen.getX(),selectionScreen.getY(),MouseScreenX, MouseScreenY);
            }
            
            NeedsRepaint = false;
        }
        this.NeedsRepaintLock.unlock();
    }
    
    public void draw() {
        
        this.NeedsRepaintLock.lock();
        if(NeedsRepaint) {
            draw(canvas.getGraphicsContext2D());
            NeedsRepaint = false;
        }
        this.NeedsRepaintLock.unlock();
    }
    
    protected void draw(GraphicsContext gc) {
        // clear
        Double w = gc.getCanvas().getWidth();
        Double h = gc.getCanvas().getHeight();
        gc.clearRect(0, 0, w, h);
        gc.setFill(Color.rgb(0xFA, 0xFB, 0xFF));
        gc.fillRect(0, 0, w, h);

        // grid
        if(ZoomFactor * (ScreenResolutionX / 2.54) >= 10) {
            
            gc.setStroke(Color.rgb(0xcc,0xcc,0xcc));
            Point2D leftupper = ScreenToModel(new Point2D(0d,0d));
            Point2D rightlower = ScreenToModel(new Point2D(w, h));
            for(Double x = leftupper.getX() - (leftupper.getX() % GridSize); x <= rightlower.getX(); x += GridSize) {
                Point2D lineScreen = ModelToScreen(new Point2D(x, 0));
                gc.strokeLine(lineScreen.getX(), 0, lineScreen.getX(), h);
            }
            for(Double y = leftupper.getY() - (leftupper.getY() % GridSize); y <= rightlower.getY(); y += GridSize) {
                Point2D lineScreen = ModelToScreen(new Point2D(0, y));
                gc.strokeLine(0, lineScreen.getY(), w, lineScreen.getY());
            }
        }

        // origin
        gc.setStroke(Color.BLACK);
        Point2D center = ModelToScreen(new Point2D(0d,0d));
        gc.strokeLine(center.getX(), 0, center.getX(), h);
        gc.strokeLine(0, center.getY(), w, center.getY());
        
        // draw the graph
        GralogGraphicsContext ggc = new JavaFXGraphicsContext(gc, this);
        structure.Render(ggc, this.Selection);
    }
    
    
    public void StructureChanged(StructureEvent e) {
        this.RequestRedraw();
    }
    
    public void VertexChanged(VertexEvent e) {
        
    }
    
    public void EdgeChanged(EdgeEvent e) {
        
    }
}
