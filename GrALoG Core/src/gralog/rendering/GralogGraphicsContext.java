/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.rendering;

/**
 *
 * @author viktor
 */
public abstract class GralogGraphicsContext {

    abstract public void Line(double x1, double y1, double x2, double y2, GralogColor color);

    public void Line(double x1, double y1, double x2, double y2) {
        Line(x1, y1, x2, y2, GralogColor.black);
    }

    abstract public void Circle(double centerx, double centery, double radius, GralogColor color);

    public void Circle(double centerx, double centery, double radius) {
        Circle(centerx, centery, radius, GralogColor.black);
    }
    
    public void PutText(double centerx, double centery, String text, GralogColor color) {
    }

}
