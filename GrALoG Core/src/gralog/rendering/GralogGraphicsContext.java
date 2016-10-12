/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.rendering;

/**
 *
 */
public abstract class GralogGraphicsContext {

    abstract public void line(double x1, double y1, double x2, double y2,
            GralogColor color, double width);

    public void line(Vector2D from, Vector2D to, GralogColor color, double width) {
        line(from.getX(), from.getY(), to.getX(), to.getY(), color, width);
    }

    public void arrow(double x1, double y1, double x2, double y2,
            double headAngle, double headLength, GralogColor color, double width) {
        double ArrowHeadAngle = headAngle / 2.0d;
        double ArrowHeadLength = headLength;

        double alpha = (new Vector2D(x2 - x1, y2 - y1)).theta();

        double arrowX1 = x2 - ArrowHeadLength * Math.cos((alpha - ArrowHeadAngle) * Math.PI / 180.0d);
        double arrowY1 = y2 - ArrowHeadLength * Math.sin((alpha - ArrowHeadAngle) * Math.PI / 180.0d);
        double arrowX2 = x2 - ArrowHeadLength * Math.sin((90d - alpha - ArrowHeadAngle) * Math.PI / 180.0d);
        double arrowY2 = y2 - ArrowHeadLength * Math.cos((90d - alpha - ArrowHeadAngle) * Math.PI / 180.0d);

        line(x1, y1, x2, y2, color, width);
        // arrow head
        line(arrowX1, arrowY1, x2, y2, color, width);
        line(arrowX2, arrowY2, x2, y2, color, width);
        line(arrowX1, arrowY1, arrowX2, arrowY2, color, width);
    }

    public void arrow(Vector2D from, Vector2D to,
            double headAngle, double headLength, GralogColor color, double width) {
        arrow(from.getX(), from.getY(), to.getX(), to.getY(),
              headAngle, headLength, color, width);
    }

    abstract public void circle(double centerx, double centery, double radius,
            GralogColor color);

    public void circle(Vector2D center, double radius, GralogColor color) {
        circle(center.getX(), center.getY(), radius, color);
    }

    public void rectangle(double x1, double y1, double x2, double y2,
            GralogColor color, double width) {
        line(x1, y1, x2, y1, color, width);
        line(x2, y1, x2, y2, color, width);
        line(x2, y2, x1, y2, color, width);
        line(x1, y2, x1, y1, color, width);
    }

    public void rectangle(Vector2D corner1, Vector2D corner2,
            GralogColor color, double width) {
        rectangle(corner1.getX(), corner1.getY(),
                  corner2.getX(), corner2.getY(),
                  color, width);
    }

    abstract public void fillRectangle(
            double x1, double y1, double x2, double y2, GralogColor color);

    public void fillRectangle(Vector2D corner1, Vector2D corner2,
            GralogColor color) {
        fillRectangle(corner1.getX(), corner1.getY(),
                      corner2.getX(), corner2.getY(),
                      color);
    }

    public abstract void putText(double centerx, double centery, String text,
            double lineHeightCm, GralogColor color);

    public void putText(Vector2D center, String text,
            double lineHeightCm, GralogColor color) {
        putText(center.getX(), center.getY(), text, lineHeightCm, color);
    }
}
