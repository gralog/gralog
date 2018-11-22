/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.rendering.shapes;

import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.Vector2D;

public class Cycle extends RenderingShape {

    public Cycle(SizeBox s) {
        super(s);
    }

    public static Cycle create(double radius) {
        return new Cycle(new SizeBox(radius, radius));
    }

    public void setRadius (double radius) {
    	sizeBox.width = Math.min(radius, MAX_WIDTH);
        sizeBox.height = Math.min(radius, MAX_HEIGHT);
    }

    @Override
    public void render(GralogGraphicsContext gc, Vector2D center, String label, GralogColor strokeColor, GralogColor fillColor) {
    	gc.strokeOval(center, sizeBox.width - strokeWidth,
    			sizeBox.width - strokeWidth, strokeWidth, strokeColor);
        gc.fillOval(center, sizeBox.width - 2 * strokeWidth,
                sizeBox.width - 2 * strokeWidth, fillColor);

        super.render(gc, center, label, strokeColor, fillColor);
    }

    @Override
    public boolean containsCoordinate(Vector2D point, Vector2D center) {
        Vector2D p = point.minus(center); // the point relative to center
        double a = sizeBox.width * sizeBox.width / 4;
        return (p.getX() * p.getX()) / a + (p.getY() * p.getY()) / a < 1;
    }

    @Override
    public Vector2D getEdgePoint(double alpha, Vector2D center) {
        //TODO: also for cycle
        alpha = Math.toRadians(alpha);
        return new Vector2D(sizeBox.width / 2 * Math.cos(alpha),
                sizeBox.height / 2 * Math.sin(alpha)).plus(center);
    }

    @Override
    public Vector2D getIntersection(Vector2D a, Vector2D b, Vector2D center) {
        //TODO: Ellipse-Function! Shorten for cycle
        //move origin to center
        a = a.minus(center);
        b = b.minus(center);
        //parameters that define the ellipse
        final double w2 = Math.pow(sizeBox.width, 2);
        final double h2 = Math.pow(sizeBox.height, 2);

        //slope
        double d;

        if (a.getX() != b.getX()) {
            d = (a.getY() - b.getY()) / (a.getX() - b.getX());
        } else {
            //special case where a is on top or below b
            double y = Math.signum(a.getY()) * Math.sqrt(h2 / 4 - a.getX() * a.getX() * h2 / w2);
            return new Vector2D(a.getX(), y).plus(center);
        }

        //constant factor for linear function of a + (b-a)t
        double c;

        if (b.getX() != 0) {
            c = b.getY() - (d * b.getX());
        } else {
            c = b.getY();
        }

        //pq formula
        double bracketTerm = (4 / w2 + 4 * d * d / h2);
        double p = (8 * d * c) / (bracketTerm * h2);
        double q = (4 * c * c * (1 / h2) - 1) / bracketTerm;

        double sqrt = Math.sqrt(p * p / 4 - q);
        double x1 = -p / 2 + sqrt;
        double x2 = -p / 2 - sqrt;

        if (Math.abs(x1 - a.getX()) < Math.abs(x2 - a.getX())) {
            return new Vector2D(x1, d * x1 + c).plus(center);
        } else {
            return new Vector2D(x2, d * x2 + c).plus(center);
        }
    }
}
