package gralog.math.sturm;

import java.util.Arrays;

/**
 *
 */
public class Polynomial {

    public int n;
    public double[] coeff;

    /**
     * ax^5+.....+f
     *
     * Coefficient to largest monomial first.
     */
    public Polynomial(double... params) {
        this.n = params.length - 1;
        this.coeff = params;
    }

    public double eval(double x) {
        double result = 0;
        for(int i = 0; i < n + 1; i++) {
            result = coeff[i] + (x * result);
        }
        return result;
    }
    
    @Override
    public String toString() {
        return Arrays.toString(coeff);
    }

}
