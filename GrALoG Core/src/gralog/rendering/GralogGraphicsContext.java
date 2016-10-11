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

    abstract public void line(double x1, double y1, double x2, double y2,
            GralogColor color, double width);

    public void arrow(double x1, double y1, double x2, double y2,
            double HeadAngle, double HeadLength, GralogColor color, double width) {
        Double ArrowHeadAngle = HeadAngle / 2.0d;
        Double ArrowHeadLength = HeadLength;

        Vector2D temp = new Vector2D(x2 - x1, y2 - y1);
        Double alpha = temp.theta();

        Double arrowX1 = x2 - ArrowHeadLength * Math.cos((alpha - ArrowHeadAngle) * Math.PI / 180.0d);
        Double arrowY1 = y2 - ArrowHeadLength * Math.sin((alpha - ArrowHeadAngle) * Math.PI / 180.0d);
        Double arrowX2 = x2 - ArrowHeadLength * Math.sin((90d - alpha - ArrowHeadAngle) * Math.PI / 180.0d);
        Double arrowY2 = y2 - ArrowHeadLength * Math.cos((90d - alpha - ArrowHeadAngle) * Math.PI / 180.0d);

        line(x1, y1, x2, y2, color, width);
        // arrow head
        line(arrowX1, arrowY1, x2, y2, color, width);
        line(arrowX2, arrowY2, x2, y2, color, width);
        line(arrowX1, arrowY1, arrowX2, arrowY2, color, width);
    }

    abstract public void circle(double centerx, double centery, double radius,
            GralogColor color);

    public void rectangle(double x1, double y1, double x2, double y2,
            GralogColor color, double width) {
        line(x1, y1, x2, y1, color, width);
        line(x2, y1, x2, y2, color, width);
        line(x2, y2, x1, y2, color, width);
        line(x1, y2, x1, y1, color, width);
    }

    abstract public void fillRectangle(double x1, double y1, double x2,
            double y2, GralogColor color);

    public void putText(double centerx, double centery, String text,
            double LineHeightCm, GralogColor color) {
    }
}
