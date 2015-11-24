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
import javafx.scene.input.MouseEvent;

import javafx.event.EventType;
import javafx.event.EventHandler;
import javafx.event.Event;
import javafx.event.ActionEvent;

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
        canvas.setOnMousePressed(e -> {
            MouseEvent me = (MouseEvent)e;
            LastMouseX = (Double)me.getX();
            LastMouseY = (Double)me.getY();
                    
            Selection = structure.FindObject((Double)me.getX(), (Double)me.getY());
            this.fireEvent(new StructurePaneEvent(STRUCTUREPANE_SELECTIONCHANGED));
            
            Dragging = Selection;
        });
        canvas.setOnMouseReleased(e -> {
            Dragging = null;
        });
        canvas.setOnMouseDragged(e -> {
            if(Dragging != null)
            {
                MouseEvent me = (MouseEvent)e;
                
                Vector<Double> coords = ((Vertex)Dragging).Coordinates;
                coords.set(0, coords.get(0) + me.getX() - LastMouseX);
                coords.set(1, coords.get(1) + me.getY() - LastMouseY);
                ((Vertex)Dragging).Coordinates = coords;
                //((Vertex)Dragging).notifyVertexListeners();
                
                LastMouseX = (Double)me.getX();
                LastMouseY = (Double)me.getY();
                
                //this.draw();
                this.requestLayout();
            }
        });
        
        this.getChildren().add(canvas);
        // resize canvas with surrounding StructurePane
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        
        canvas.widthProperty().addListener(e -> draw());
        canvas.heightProperty().addListener(e -> draw());
        
        //canvas.widthProperty().addListener(e -> requestLayout());
        //canvas.heightProperty().addListener(e -> requestLayout());
    }
    
    public void draw() {
        draw(canvas.getGraphicsContext2D());
    }
    
    protected void draw(GraphicsContext gc) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        
        GralogGraphicsContext ggc = new JavaFXGraphicsContext(gc);
        
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
