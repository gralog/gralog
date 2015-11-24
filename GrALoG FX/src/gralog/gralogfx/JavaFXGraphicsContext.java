/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.gralogfx;

import gralog.rendering.*;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


/**
 *
 * @author viktor
 */
public class JavaFXGraphicsContext extends GralogGraphicsContext {
    
    protected GraphicsContext gc;
    public JavaFXGraphicsContext(GraphicsContext gc) {
        this.gc = gc;
    }
    
    @Override
    public void Line(double x1, double y1, double x2, double y2, GralogColor c) {
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);        
        gc.strokeLine(x1, y1, x2, y2);
        
    }
    
    @Override
    public void Circle(double centerx, double centery, double radius, GralogColor c) {
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.fillOval(centerx-radius,centery-radius, 2d*radius, 2d*radius);
    }
}
