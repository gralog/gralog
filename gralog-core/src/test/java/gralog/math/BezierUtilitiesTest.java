package gralog.math;

import gralog.rendering.Vector2D;
import org.junit.Test;
import static org.junit.Assert.*;

public class BezierUtilitiesTest
{

    @Test
    public void testAlgebraicPointProjection(){
        Vector2D point = Vector2D.zero();
        Vector2D source = new Vector2D(-1, 3);
        Vector2D target = new Vector2D(1, 3);
        Vector2D ctrl1 = new Vector2D(-1, 1);
        Vector2D ctrl2 = new Vector2D(1, 1);

        double deriv;

        deriv = Math.abs(BezierUtilities.projectionDerivativeMiddle(point, source, ctrl1, ctrl2, target));
        assertTrue(deriv < 0.01);

        ctrl1 = new Vector2D(1, 1);
        ctrl2 = new Vector2D(-1, 1);
        deriv = Math.abs(BezierUtilities.projectionDerivativeMiddle(point, source, ctrl1, ctrl2, target));
        assertTrue(deriv < 0.01);
    }
}
