/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.math;

import gralog.rendering.Vector2D;
import gralog.structure.Edge;

public class BezierQuadratic {

    public Vector2D c0, c1, c2;

    public static BezierQuadratic createFromEdge(Edge e) {
        BezierQuadratic b = new BezierQuadratic();
        b.c0 = e.getStartingPointSource();
        b.c1 = e.controlPoints.get(0).getPosition();
        b.c2 = e.getStartingPointTarget();
        return b;
    }

    public Vector2D eval(double t) {
        return c0.multiply((1 - t) * (1 - t)).plus(
                c1.multiply(2 * t * (1 - t))).plus(
                c2.multiply(t * t));
    }
}
