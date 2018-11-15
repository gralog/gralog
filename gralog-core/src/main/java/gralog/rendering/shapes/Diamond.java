/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.rendering.shapes;

import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.Vector2D;

public class Diamond extends RenderingShape {

    public Diamond(SizeBox s) {
        super(s);
    }

    @Override
    public void render(GralogGraphicsContext gc, Vector2D center, String label, GralogColor strokeColor, GralogColor fillColor) {
        double x1 = center.getX() - sizeBox.width / 2;
        double y1 = center.getY() - sizeBox.height / 2;
        double x2 = center.getX() + sizeBox.width / 2;
        double y2 = center.getY() + sizeBox.height / 2;

        gc.fillDiamond(x1, y1, x2, y2, fillColor);
        gc.strokeDiamond(x1, y1, x2, y2, strokeWidth, strokeColor);

        super.render(gc, center, label, strokeColor, fillColor);
    }

    @Override
    public boolean containsCoordinate(Vector2D point, Vector2D center) {
        Vector2D p = point.minus(center); // the point relative to center
        double quo = sizeBox.width / sizeBox.height;
        return Math.abs(p.getX()) + Math.abs(quo * p.getY()) < sizeBox.width / 2;
    }

    @Override
    public Vector2D getEdgePoint(double alpha, Vector2D center) {
        double x;
        double y;
        alpha = alpha % 360;
        if (alpha < 0) {
            alpha += 360;
        }
        if (alpha < 90) {
            x = (1 - alpha / 90) * sizeBox.width / 2;
            y = (alpha / 90) * sizeBox.height / 2;
        } else if (alpha < 180) {
            alpha %= 90;
            x = -(alpha / 90) * sizeBox.width / 2;
            y = (1 - alpha / 90) * sizeBox.height / 2;
        } else if (alpha < 270) {
            alpha %= 90;
            x = -(1 - alpha / 90) * sizeBox.width / 2;
            y = -(alpha / 90) * sizeBox.height / 2;
        } else {
            alpha %= 90;
            x = (alpha / 90) * sizeBox.width / 2;
            y = -(1 - alpha / 90) * sizeBox.height / 2;
        }

        return new Vector2D(x, y).plus(center);
    }

    @Override
    public Vector2D getIntersection(Vector2D a, Vector2D b, Vector2D center) {
        //first, do a classification by angle between center and lineStart
        a = a.minus(center);
        b = b.minus(center);
        double alpha = a.theta();
        double w = sizeBox.width / 2;
        double h = sizeBox.height / 2;

        Vector2D r;
        Vector2D dir;

        double coeff;

        Vector2D diff = a.minus(b);
        final double counterRotation = Math.toRadians(diff.theta());
        /*
         * The shape consists of 4 lines that can be identified by the angle of a point that lies
         * on the edge of the shape. An edge point with the angle 45 is on the first line,
         * an edge point with angle 110 lies on the second etc..
         *
         *
         * Consider a point A outside of the diamond with an angle of <90 (w.r.t. center)
         *
         * Now, if the internal point B is at the center, the intersection point will always be
         * at the first line. If however the point B is below the center and A has a very flat angle
         * then the intersection could occur at the 4th line.
         *
         * This correction is being calculated with newC1/C2 and depending on the result, we classify
         * the correct line.
         *
         * */

        int lineNumber;

        if (alpha < 90) {
            double newC1 = (new Vector2D(w, 0)).rotate(b, -counterRotation).getY();
            double newC2 = (new Vector2D(0, h)).rotate(b, -counterRotation).getY();

            if (newC1 > b.getY()) {
                lineNumber = 3;
            } else if (newC2 < b.getY()) {
                lineNumber = 1;
            } else {
                lineNumber = 0;
            }
        } else if (alpha < 180) {
            double newC1 = (new Vector2D(0, h)).rotate(b, -counterRotation).getY();
            double newC2 = (new Vector2D(-w, 0)).rotate(b, -counterRotation).getY();

            if (newC1 > b.getY()) {
                lineNumber = 0;
            } else if (newC2 < b.getY()) {
                lineNumber = 2;
            } else {
                lineNumber = 1;
            }

        } else if (alpha < 270) {
            double newC1 = (new Vector2D(-w, 0)).rotate(b, -counterRotation).getY();
            double newC2 = (new Vector2D(0, -h)).rotate(b, -counterRotation).getY();

            if (newC1 > b.getY()) {
                lineNumber = 1;
            } else if (newC2 < b.getY()) {
                lineNumber = 3;
            } else {
                lineNumber = 2;
            }

        } else {
            double newC1 = (new Vector2D(0, -h)).rotate(b, -counterRotation).getY();
            double newC2 = (new Vector2D(w, 0)).rotate(b, -counterRotation).getY();

            if (newC1 > b.getY()) {
                lineNumber = 2;
            } else if (newC2 < b.getY()) {
                lineNumber = 0;
            } else {
                lineNumber = 3;
            }

        }
        LineParameters l = getParamsFromNumber(lineNumber, w, h);
        r = l.r;
        dir = l.dir;

        coeff = dir.getY() / dir.getX();
        double t = ((a.getY() - r.getY()) - coeff * (a.getX() - r.getX()))
                / (-b.getY() + a.getY() - coeff * (-b.getX() + a.getX()));

        return a.plus(b.minus(a).multiply(t)).plus(center); //line defined as a + (b-a)t
    }

    private LineParameters getParamsFromNumber(int lineNumber, double w, double h) {
        LineParameters l = new LineParameters();
        if (lineNumber == 0) {
            l.r = new Vector2D(w, 0);
            l.dir = new Vector2D(w, -h);
        } else if (lineNumber == 1) {
            l.r = new Vector2D(-w, 0);
            l.dir = new Vector2D(w, h);
        } else if (lineNumber == 2) {
            l.r = new Vector2D(-w, 0);
            l.dir = new Vector2D(w, -h);
        } else {
            l.r = new Vector2D(+w, 0);
            l.dir = new Vector2D(w, h);
        }

        return l;
    }

    private static class LineParameters {
        private Vector2D r;
        private Vector2D dir;
    }
}
