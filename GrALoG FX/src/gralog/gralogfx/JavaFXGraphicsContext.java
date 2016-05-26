/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.gralogfx;

import gralog.rendering.*;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;


/**
 *
 * @author viktor
 */
public class JavaFXGraphicsContext extends GralogGraphicsContext {
    
    protected GraphicsContext gc;
    protected StructurePane pane;
    public JavaFXGraphicsContext(GraphicsContext gc, StructurePane pane) {
        this.gc = gc;
        this.pane = pane;
    }
    
    @Override
    public void Line(double x1, double y1, double x2, double y2, GralogColor c, double width) {
        Point2D p1 = pane.ModelToScreen(new Point2D(x1,y1));
        Point2D p2 = pane.ModelToScreen(new Point2D(x2,y2));
        
        gc.setFill(Color.rgb(c.r, c.g, c.b));
        gc.setStroke(Color.rgb(c.r, c.g, c.b));
        gc.setLineWidth(width * pane.ZoomFactor * pane.ScreenResolutionX / 2.54);
        
        gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }
    
    @Override
    public void Circle(double centerx, double centery, double radius, GralogColor c) {
        Point2D p1 = pane.ModelToScreen(new Point2D(centerx-radius,centery-radius));
        Point2D p2 = pane.ModelToScreen(new Point2D(centerx+radius,centery+radius));

        gc.setFill(Color.rgb(c.r, c.g, c.b));
        gc.setStroke(Color.rgb(c.r, c.g, c.b));
        gc.setLineWidth(1);
        
        gc.fillOval(p1.getX(), p1.getY(), p2.getX()-p1.getX(), p2.getY() - p1.getY());
    }
    
    @Override
    public void PutText(double centerx, double centery, String text, double LineHeightCM, GralogColor c)
    {
        Point2D p1 = pane.ModelToScreen(new Point2D(centerx,centery));

        Font font = gc.getFont();
                                   // I have no idea, why this is 1959.5... I hate magic numbers
        double newSize = 2.54d * LineHeightCM * pane.ZoomFactor * 1959.5
                       / ( pane.ScreenResolutionY );
        gc.setFont(new Font(font.getName(), newSize));
        
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFill(Color.rgb(c.r, c.g, c.b));
        
        gc.fillText(text, p1.getX(), p1.getY());
    }
    
    @Override
    public void FillRectangle(double x1, double y1, double x2, double y2, GralogColor c)
    {
        Point2D p1 = pane.ModelToScreen(new Point2D(x1,y1));
        Point2D p2 = pane.ModelToScreen(new Point2D(x2,y2));

        gc.setFill(Color.rgb(c.r, c.g, c.b));

        gc.fillRect(p1.getX(), p1.getY(), p2.getX()-p1.getX(), p2.getY()-p1.getY());
    }
    
}
