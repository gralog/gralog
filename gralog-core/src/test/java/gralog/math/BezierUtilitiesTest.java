package gralog.math;

import gralog.rendering.Vector2D;
import org.junit.Test;

public class BezierUtilitiesTest {

    @Test
    public void LineIntersectionCubicBezier() {

        Vector2D source = new Vector2D(0, 0).plus(1, 1);
        Vector2D ctrl1 = new Vector2D(0, 1).plus(1, 1);
        Vector2D ctrl2 = new Vector2D(1, -1).plus(1, 1);
        Vector2D target = new Vector2D(1, 0).plus(1, 1);

        BezierCubic b = new BezierCubic();
        b.c0 = source;
        b.c1 = ctrl1;
        b.c2 = ctrl2;
        b.c3 = target;

        var intersections = BezierUtilities.xIntersectionCubicBezier(1.5, b);

        BezierUtilities.yIntersectionCubicBezier(0.9, b);


    }

}
