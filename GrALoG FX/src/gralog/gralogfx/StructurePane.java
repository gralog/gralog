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
import java.util.Vector;
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
import javafx.scene.input.ScrollEvent;


/**
 *
 * @author viktor
 */
//public class StructurePane extends ScrollPane implements StructureListener {
public class StructurePane extends StackPane implements StructureListener {
    
    static EventType<StructurePaneEvent> ALL_STRUCTUREPANE_EVENTS = new EventType<>("ALL_STRUCTUREPANE_EVENTS");
    static EventType<StructurePaneEvent> STRUCTUREPANE_SELECTIONCHANGED = new EventType<>(ALL_STRUCTUREPANE_EVENTS, "STRUCTUREPANE_SELECTIONCHANGED");
    public void setOnSelectionChanged(EventHandler<StructurePaneEvent> handler) {
        this.setEventHandler(STRUCTUREPANE_SELECTIONCHANGED, handler);
    }
    
    Structure structure;
    Canvas canvas;
    
    Object Selection = null;
    Object Dragging = null;
    Double LastMouseX = -1d;
    Double LastMouseY = -1d;
    
    public StructurePane(Structure structure) {
        this.structure = structure;
        canvas = new Canvas(500,500);

        this.getChildren().add(canvas);
        // resize canvas with surrounding StructurePane
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());
        canvas.widthProperty().addListener(e -> draw());
        canvas.heightProperty().addListener(e -> draw());
        
        canvas.setOnScroll(e->{
            ScrollEvent se = (ScrollEvent)e;
            
            Point2D oldMousePos = ScreenToModel(new Point2D(se.getX(), se.getY()));
            ZoomFactor *= Math.pow(1.2d, se.getDeltaY()/40d);
            Point2D newMousePos = ScreenToModel(new Point2D(se.getX(), se.getY()));
            
            OffsetX += oldMousePos.getX() - newMousePos.getX(); // sweet :-)
            OffsetY += oldMousePos.getY() - newMousePos.getY();
            draw();
        });
        
        SetSelectMode();
    }
    
    
    
    
    public void SetSelectMode()
    {
        canvas.setOnMousePressed(e -> {
            MouseEvent me = (MouseEvent)e;
            Point2D mousePositionModel = ScreenToModel(new Point2D(me.getX(), me.getY()));
            LastMouseX = (Double)mousePositionModel.getX();
            LastMouseY = (Double)mousePositionModel.getY();
            
            Selection = structure.FindObject((Double)LastMouseX, (Double)LastMouseY);
            Dragging = Selection;
            this.fireEvent(new StructurePaneEvent(STRUCTUREPANE_SELECTIONCHANGED));
        });
        canvas.setOnMouseReleased(e -> {
            Dragging = null;
        });
        canvas.setOnMouseDragged(e -> {
            MouseEvent me = (MouseEvent)e;
            Point2D mousePositionModel = ScreenToModel(new Point2D(me.getX(), me.getY()));
            if(Dragging != null)
            {
                Vector<Double> coords = ((Vertex)Dragging).Coordinates;
                coords.set(0, coords.get(0) + mousePositionModel.getX() - LastMouseX);
                coords.set(1, coords.get(1) + mousePositionModel.getY() - LastMouseY);
                ((Vertex)Dragging).Coordinates = coords;
                //((Vertex)Dragging).notifyVertexListeners();
    
                // this must not be done in the screen-draging!!!
                LastMouseX = (Double)mousePositionModel.getX();
                LastMouseY = (Double)mousePositionModel.getY();

            } else {                                                 // draging the screen
                OffsetX -= (mousePositionModel.getX() - LastMouseX);
                OffsetY -= (mousePositionModel.getY() - LastMouseY);
            }
            
            
            this.draw();
            //this.requestLayout();
        });
    }

    public void SetVertexCreationMode()
    {
        canvas.setOnMousePressed(e -> {
            MouseEvent me = (MouseEvent)e;
            Point2D mousePositionModel = ScreenToModel(new Point2D(me.getX(), me.getY()));

            Vertex v = structure.CreateVertex();
            v.Coordinates.add(mousePositionModel.getX());
            v.Coordinates.add(mousePositionModel.getY());
            structure.AddVertex(v);
            
            Selection = v;
            this.fireEvent(new StructurePaneEvent(STRUCTUREPANE_SELECTIONCHANGED));
            draw();
        });
        canvas.setOnMouseReleased(e -> {});
        canvas.setOnMouseDragged(e -> {});
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
    
    
    
    
    public void draw() {
        draw(canvas.getGraphicsContext2D());
    }
    
    protected void draw(GraphicsContext gc) {

        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setFill(Color.rgb(0xFA, 0xFB, 0xFF));
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        Point2D center = ModelToScreen(new Point2D(0d,0d));
        gc.strokeLine(center.getX(), 0, center.getX(), gc.getCanvas().getHeight());
        gc.strokeLine(center.getX(), 0, center.getX(), gc.getCanvas().getHeight());
        gc.strokeLine(0, center.getY(), gc.getCanvas().getWidth(), center.getY());
        
        GralogGraphicsContext ggc = new JavaFXGraphicsContext(gc, this);
        structure.Render(ggc);
    }
    
    
    public void StructureChanged(StructureEvent e) {
        draw();
    }
    
    public void VertexChanged(VertexEvent e) {
        
    }
    
    public void EdgeChanged(EdgeEvent e) {
        
    }
}
