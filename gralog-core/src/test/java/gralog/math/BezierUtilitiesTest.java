package gralog.math;

import static org.junit.Assert.*;

import gralog.rendering.Vector2D;
import org.junit.Test;

public class BezierUtilitiesTest {

    @Test
    public void LineIntersectionCubicBezier(){

        Vector2D source = new Vector2D(0, 0).plus(1, 1);
        Vector2D ctrl1  = new Vector2D(0, 1).plus(1, 1);
        Vector2D ctrl2  = new Vector2D(1, 1).plus(1, 1);
        Vector2D target = new Vector2D(1, 0).plus(1, 1);

        BezierUtilities.Line l = new BezierUtilities.Line();
        l.a = 1;
        l.b = 0;
        l.c = 1.5;

        BezierUtilities.lineIntersectionCubicBezier(l, source, ctrl1, ctrl2, target);
    }

}
