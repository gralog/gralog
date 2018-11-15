/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.rendering;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.Arrays;

/**
 * This class offers abstract drawing and drawing utility methods.
 *
 * Utilities include drawing tooltips and transparent rectangles for selection boxes.
 */
public abstract class GralogGraphicsContext {

    public abstract void line(double x1, double y1, double x2, double y2,
                              GralogColor color, double width, LineType type);

    public void line(Vector2D from, Vector2D to, GralogColor color, double width, LineType type) {
        line(from.getX(), from.getY(), to.getX(), to.getY(), color, width, type);
    }

    public abstract void arrow(Vector2D dir, Vector2D pos, Arrow arrowType, double scale, GralogColor color);

    public void arrow(Vector2D dir, Vector2D pos, Arrow arrowType, double scale, GralogColor color, double lineWidth) {
        double theta = Math.toRadians(dir.theta());

        double[] xs = Arrays.copyOf(arrowType.xPoints, arrowType.xPoints.length);
        double[] ys = Arrays.copyOf(arrowType.yPoints, arrowType.yPoints.length);

        for (int i = 0; i < arrowType.xPoints.length; i++) {
            double oldX = xs[i];
            double cost = Math.cos(theta);
            double sint = Math.sin(theta);
            xs[i] = (xs[i] * cost - ys[i] * sint) * scale + pos.getX();
            ys[i] = (oldX * sint + ys[i] * cost) * scale + pos.getY();
        }

        if (arrowType.flag == Arrow.LineFlag.POLY) {
            polygon(xs, ys, arrowType.count, color);
        } else {
            lines(xs, ys, arrowType.count, color, lineWidth);
        }
    }

    public abstract void polygon(double[] x, double[] y, int count, GralogColor color);

    public abstract void lines(double[] x, double[] y, int count, GralogColor color, double lineWidth);

    /**
     * Draws a curved bezier line from start to end. The control points are positively perpendicular
     * to the line from start to end. The length of control points can be set.
     * <p>
     * Can use to draw self loops of vertices.
     *
     * @param l      all relevant vectors of loop
     * @param length The length of the control points.
     * @param color  Color of the line
     * @param width  Line width (will be scaled according to zoom)
     */
    public abstract void loop(Loop l, double length, double correction, GralogColor color, double width, LineType type);

    public abstract void circle(double centerx, double centery, double radius,
                                GralogColor color);

    public void circle(Vector2D center, double radius, GralogColor color) {
        circle(center.getX(), center.getY(), radius, color);
    }

    public abstract void strokeOval(double x, double y, double width, double height, double strokeWidth,
                                    GralogColor color);

    public void strokeOval(Vector2D center, double width, double height, double strokeWidth, GralogColor color) {
        strokeOval(center.getX(), center.getY(), width, height, strokeWidth, color);
    }

    public abstract void fillOval(double x, double y, double width, double height,
                                  GralogColor color);

    public void fillOval(Vector2D center, double width, double height, GralogColor color) {
        fillOval(center.getX(), center.getY(), width, height, color);
    }

    public void fillOval(double x, double y, double width, double height) {
        fillOval(x, y, width, height, GralogColor.BLACK);
    }

    public abstract void fillRectangle(
            double x1, double y1, double x2, double y2, GralogColor color);

    public abstract void strokeRectangle(
            double x1, double y1, double x2, double y2, double strokeWidth, GralogColor color);

    public abstract void strokeRectangle(
            double x1, double y1, double x2, double y2, double strokeWidth, LineType line);

    public abstract void strokeDiamond(
            double x1, double y1, double x2, double y2, double strokeWidth, GralogColor color);

    public abstract void fillDiamond(
            double x1, double y1, double x2, double y2, GralogColor color);

    public abstract void drawBezier(Bezier curve, GralogColor color, double width, LineType type);

    public abstract void drawQuadratic(Bezier curve, GralogColor color, double width, LineType type);

    public abstract void selectionRectangle(Point2D from, Point2D to, Color color);

    public abstract void putText(double centerx, double centery, String text,
                                 double lineHeightCm, GralogColor color);

    public void putText(Vector2D center, String text,
                        double lineHeightCm, GralogColor color) {
        putText(center.getX(), center.getY(), text, lineHeightCm, color);
    }

    public enum LineType {
        PLAIN,
        DOTTED,
        DASHED
    }

    public static class Loop {
        public Vector2D start;
        public Vector2D tangentStart;
        public Vector2D end;
        public Vector2D tangentEnd;
    }

    public static class Bezier {
        public Vector2D source;
        public Vector2D target;
        public Vector2D ctrl1;
        public Vector2D ctrl2;
    }
}
