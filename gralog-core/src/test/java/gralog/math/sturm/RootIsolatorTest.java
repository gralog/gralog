package gralog.math.sturm;

import static org.junit.Assert.*;

import gralog.math.sturm.Interval;
import gralog.math.sturm.Polynomial;
import gralog.math.sturm.RootIsolator;
import org.junit.Test;

public class RootIsolatorTest {

    private static final double epsilon = 0.00001;

    @Test
    public void testGenericDescartes5(){
        //TODO: write test to validate generic descartes intervals
        //RootIsolator.genericDescartes5(-4, 3, -5, 6, -0.2, -1);
    }

    @Test
    public void testSturmSequenceIsolation(){
        Polynomial[] sequence = RootIsolator.sturmSequence(1, -3, 3.4, -1.8, 0.4384, -0.0384);
        validateDoubleArrays(sequence[2].coeff, 2d/25, -18d/125, 254d/3125, -222d/15625);
        validateDoubleArrays(sequence[3].coeff, 7d/25, -42d/125, 59d/625);
        validateDoubleArrays(sequence[4].coeff, 72d/21875, -216d/109375);
        validateDoubleArrays(sequence[5].coeff, 4d/625);
    }
    private static boolean validateDoubleArrays(double[] input, double... expected){
        for(int i = 0; i < input.length; i++){
            assertEquals(input[i], expected[i], epsilon);
        }
        return true;
    }
    @Test
    public void testKCPolynomial(){
        Polynomial p = new Polynomial(1, 2, 3, 4);
        Interval v;

        v = new Interval(0,0);
        assertEquals(6.125, RootIsolator.eval(p, v, 0.5), epsilon);


        v = new Interval(1,1);
        assertEquals(62.375, RootIsolator.eval(p, v, 0.5), epsilon);

        v = new Interval(2,1);
        assertEquals(349.375, RootIsolator.eval(p, v, 0.5), epsilon);
    }
}
