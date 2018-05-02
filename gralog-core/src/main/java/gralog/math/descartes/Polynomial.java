package gralog.math.descartes;

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

}
