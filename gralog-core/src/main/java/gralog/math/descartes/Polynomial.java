package gralog.math.descartes;

import java.util.Arrays;

/**
 * Description of univariate (k,c)-polynomial of degree n,
 * following the description of
 *
 * Rouillier, F., & Zimmermann, P. (2004).
 * Efficient isolation of polynomial's real roots.
 * Journal of Computational and Applied Mathematics, 162(1), 33-50.
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
    public Polynomial(double... params){
        this.n = params.length - 1;
        this.coeff = params;
    }

    public double eval(double x){
        double result = 0;
        for(int i = 0; i < n + 1; i++){
            result = coeff[i] + (x * result);
        }
        return result;
    }

    public Polynomial derivative(){
        double[] c = new double[coeff.length - 1];
        for(int i = 0; i < c.length; i++){
            c[i] = (n - i) * coeff[i];
        }
        return new Polynomial(c);
    }

    @Override
    public String toString(){
        return Arrays.toString(coeff);
    }

}
