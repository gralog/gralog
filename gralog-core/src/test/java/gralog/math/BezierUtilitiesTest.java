package gralog.math;

import static org.junit.Assert.*;

import gralog.rendering.Vector2D;
import org.junit.Test;

public class BezierUtilitiesTest {

    @Test
    public void LineIntersectionCubicBezier(){

        Vector2D source = new Vector2D(0, 0).plus(1, 1);
        Vector2D ctrl1  = new Vector2D(0, 1).plus(1, 1);
        Vector2D ctrl2  = new Vector2D(1, -1).plus(1, 1);
        Vector2D target = new Vector2D(1, 0).plus(1, 1);

        BezierCubic b = new BezierCubic();
        b.c0 = source;
        b.c1 = ctrl1;
        b.c2 = ctrl2;
        b.c3 = target;

        System.out.println("X-Collision at 1.5");
        var intersections = BezierUtilities.xIntersectionCubicBezier(1.5, b);

        System.out.println("\n\nY-Collision at 1");
        System.out.println(ctrl2);
        BezierUtilities.yIntersectionCubicBezier(0.9, b);

        for(Vector2D v : intersections){
            System.out.println(v);
        }

    }

}
