package gralog.math;

import gralog.rendering.Vector2D;
import gralog.structure.Edge;

public class BezierCubic {

    public Vector2D c0, c1, c2, c3;

    public static BezierCubic createFromEdge(Edge e){
        BezierCubic b = new BezierCubic();
        b.c0 = e.getStartingPointSource();
        b.c1 = e.controlPoints.get(0).getPosition();
        b.c2 = e.controlPoints.get(1).getPosition();
        b.c3 = e.getStartingPointTarget();
        return b;
    }
}
