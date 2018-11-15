/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.math;

import gralog.rendering.Vector2D;
import gralog.structure.Edge;

public class BezierCubic {

    public Vector2D c0, c1, c2, c3;

    public static BezierCubic createFromEdge(Edge e) {
        BezierCubic b = new BezierCubic();
        b.c0 = e.getStartingPointSource();
        b.c1 = e.controlPoints.get(0).getPosition();
        b.c2 = e.controlPoints.get(1).getPosition();
        b.c3 = e.getStartingPointTarget();
        return b;
    }

    public Vector2D eval(double t) {
        return c0.multiply(Math.pow(1 - t, 3)).plus(
                c1.multiply(3 * Math.pow(1 - t, 2) * t)).plus(
                c2.multiply(3 * (1 - t) * t * t)).plus(
                c3.multiply(Math.pow(t, 3)));
    }

}
