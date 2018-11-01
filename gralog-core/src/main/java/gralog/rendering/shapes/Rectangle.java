/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.rendering.shapes;

import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.Vector2D;

public class Rectangle extends RenderingShape {

    public Rectangle(SizeBox s) {
        super(s);
    }

    private static double mod360(double alpha) {
        alpha %= 360;
        if (alpha < 0) {
            alpha += 360;
        }
        return alpha;
    }

    private static double tan(double alphaInDeg) {
        return Math.tan(Math.toRadians(alphaInDeg));
    }

    @Override
    public void render(GralogGraphicsContext gc, Vector2D center, String label, GralogColor strokeColor, GralogColor fillColor) {
        //rendered shape is already given by sizeBox
        double x1 = center.getX() - sizeBox.width / 2;
        double y1 = center.getY() - sizeBox.height / 2;
        double x2 = center.getX() + sizeBox.width / 2;
        double y2 = center.getY() + sizeBox.height / 2;

        gc.fillRectangle(x1, y1, x2, y2, fillColor);
        gc.strokeRectangle(x1, y1, x2, y2, strokeWidth, strokeColor);

        super.render(gc, center, label, strokeColor, fillColor);
    }

    @Override
    public boolean containsCoordinate(Vector2D point, Vector2D center) {
        Vector2D p = point.minus(center); // the point relative to center
        return Math.abs(p.getX()) < sizeBox.width / 2 && Math.abs(p.getY()) < sizeBox.height / 2;
    }

    @Override
    public Vector2D getEdgePoint(double alpha, Vector2D center) {
        double x;
        double y;
        double thresholdAngle = Math.toDegrees(Math.atan(sizeBox.height / sizeBox.width));
        alpha = alpha % 360;
        if (alpha <= 0) {
            alpha += 360;
        }

        if (alpha <= thresholdAngle) {
            x = sizeBox.width / 2;
            y = tan(alpha) * x;
        } else if (alpha <= 90) {
            alpha = 90 - alpha;
            y = sizeBox.height / 2;
            x = tan(alpha) * y;
        } else if (alpha <= 180 - thresholdAngle) {
            alpha = alpha - 90;
            y = sizeBox.height / 2;
            x = -tan(alpha) * y;
        } else if (alpha <= 180) {
            alpha = 180 - alpha;
            x = -sizeBox.width / 2;
            y = tan(alpha) * sizeBox.width / 2;
        } else if (alpha <= 180 + thresholdAngle) {
            alpha = alpha - 180;
            x = -sizeBox.width / 2;
            y = tan(alpha) * x;
        } else if (alpha <= 270) {
            alpha = 270 - alpha;
            y = -sizeBox.height / 2;
            x = tan(alpha) * y;
        } else if (alpha <= 360 - thresholdAngle) {
            alpha = alpha - 270;
            y = -sizeBox.height / 2;
            x = tan(alpha) * sizeBox.height / 2;
        } else {
            alpha = 360 - alpha;
            x = sizeBox.width / 2;
            y = -tan(alpha) * x;
        }
        return new Vector2D(x, y).plus(center);
    }

    @Override
    public Vector2D getIntersection(Vector2D a, Vector2D b, Vector2D center) {
        final double mainLineAngle = a.minus(b).theta();

        //line from b to first corner
        final Vector2D corner1 = center.plus(sizeBox.width / 2, sizeBox.height / 2);
        final Vector2D corner2 = center.plus(-sizeBox.width / 2, sizeBox.height / 2);
        final Vector2D corner3 = center.plus(-sizeBox.width / 2, -sizeBox.height / 2);
        final Vector2D corner4 = center.plus(sizeBox.width / 2, -sizeBox.height / 2);

        //if line is between corner 4 and 1
        if (mainLineAngle < corner1.minus(b).theta() || mainLineAngle > corner4.minus(b).theta()) {
            double t = (corner1.getX() - a.getX()) / (b.getX() - a.getX());
            return a.plus(b.minus(a).multiply(t));
        } else if (mainLineAngle < corner2.minus(b).theta()) { //if line is between corner 1 and 2
            double t = (corner1.getY() - a.getY()) / (b.getY() - a.getY());
            return a.plus(b.minus(a).multiply(t));
        } else if (mainLineAngle < corner3.minus(b).theta()) { //if line is between corner 2 and 3
            double t = (corner2.getX() - a.getX()) / (b.getX() - a.getX());
            return a.plus(b.minus(a).multiply(t));
        } else { //if line is between corner 3 and 4
            double t = (corner3.getY() - a.getY()) / (b.getY() - a.getY());
            return a.plus(b.minus(a).multiply(t));
        }
    }
}
