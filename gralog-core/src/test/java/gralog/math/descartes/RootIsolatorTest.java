package gralog.math.descartes;

import static org.junit.Assert.*;
import org.junit.Test;

public class RootIsolatorTest {

    private static final double epsilon = 0.00001;

    @Test
    public void testGenericDescartes5(){
        //TODO: write test to validate generic descartes intervals
        //DescartesRootIsolator.genericDescartes5(-4, 3, -5, 6, -0.2, -1);
    }

    @Test
    public void testSturmSequenceIsolation(){
        DescartesRootIsolator.sturmSequenceIsolation(1, -3, 3.4, -1.8, 0.4384, -0.0384);
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
