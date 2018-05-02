package gralog.math.descartes;

import static org.junit.Assert.*;
import org.junit.Test;

public class RootIsolatorTest {

    private static final double epsilon = 0.00001;

    @Test
    public void testGenericDescartes5(){
        //TODO: write test to validate generic descartes intervals
    }

    @Test
    public void testDescartesBounds(){
        Polynomial p = new Polynomial(1, 1, 1, -1, 1, -1);
        DescartesRootIsolator.descartesBound5(p, new Interval(6, 0));
    }

    @Test
    public void testKCPolynomial(){
        Polynomial p = new Polynomial(1, 2, 3, 4);
        Interval v;

        v = new Interval(0,0);
        assertEquals(6.125, DescartesRootIsolator.eval(p, v, 0.5), epsilon);


        v = new Interval(1,1);
        assertEquals(62.375, DescartesRootIsolator.eval(p, v, 0.5), epsilon);

        v = new Interval(2,1);
        assertEquals(349.375, DescartesRootIsolator.eval(p, v, 0.5), epsilon);
    }
}
