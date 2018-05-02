package gralog.math.descartes;

import java.util.TreeSet;

/**
 * This class implements algorithms for isolating the roots of univariate polynomials by
 * using Descartes Method based algorithms.
 *
 * A recent benchmark comparing performance of several root solvers on multiple kinds of high-order polynomials
 *
 * @see <a href=https://dl.acm.org/citation.cfm?id=972166>Implementation of a hybrid arithmetic strategy</a>
 * @see <a href=https://www.sciencedirect.com/science/article/pii/S0747717115000292>Reference for ANewDesc algorithm</a>
 * @see <a href=https://arxiv.org/abs/1605.00410>High-performance implementation of ANewDesc and RS hybrid</a>
 */
public class DescartesRootIsolator {


    /**
     * Evaluates the (k,c)-polynomial according to it's definition in the paper.
     * Uses Horner's method.
     * @see <a href=https://dl.acm.org/citation.cfm?id=972166>P. 35 of the proceedings</a>
     */
    public static double eval(Polynomial p, Interval v, double x){
        x = (x + v.c)/(1 << v.k);
        double result = 0;
        for(int i = 0; i < p.n + 1; i++){
            result = p.coeff[i] + (x * result);
        }
        return Math.pow(2, v.k * p.n) * result;
    }


    /**
     * Computes and prints descartes intervals of a given polynomial with degree 5.
     */
    public static void genericDescartes5(double... coeff){
        TreeSet<Interval> tree = new TreeSet<>();
        tree.add(new Interval(0, 0));

        Polynomial p = new Polynomial(coeff);

    }
    private static Interval getNode(TreeSet<Interval> tree){
        return tree.pollFirst();
    }

    public static int descartesBound5(Polynomial p, Interval v){
        //coefficients similar to polynomial T_1(R(P_kc(x)))
        //I only need to have correct signs, so these parameters are scaled for efficiency
        double c1 = v.c;
        double c2 = v.c * c1;
        double c3 = v.c * c2;
        double c4 = v.c * c3;
        double c5 = v.c * c4;
        //2 ^ k
        double twoK = 1 << v.k;

        double a0 = twoK*(twoK*(twoK*(twoK*(twoK*(p.coeff[0])+c1)+c2)+c3)+c4)+c5;
        double a1 = 16 * p.coeff[1] + 16 * p.coeff[2] * c1 + 4 * p.coeff[3] * 3 * c2 + 2 * p.coeff[4] * 4 * c3 + p.coeff[5] * 5 * c4;
        double a2 = 8 * p.coeff[2] + 4 * p.coeff[3] * 3 * c1 + 2 * p.coeff[4] * 6 * c2 + p.coeff[5] * 10 * c3;
        double a3 = 4 * p.coeff[3] + 2 * p.coeff[4] * 4 * c1 + p.coeff[5] * 10 * c2;
        double a4 = 2 * p.coeff[4] + p.coeff[5] * 5 * c1;
        double a5 = p.coeff[5];

        return countSignChanges(a0, a1, a2, a3, a4, a5);
    }

    /**
     * Counts sign changes of an array of coefficients
     */
    private static int countSignChanges(double... coeff){
        int count = 0;
        double sign = Math.signum(coeff[0]);
        double signNew;
        for(int i = 0; i < coeff.length; i++){
            if(coeff[i] == 0){
                continue;
            }
            signNew = Math.signum(coeff[i]);
            if(signNew != sign){
                count++;
            }
            sign = signNew;
        }
        return count;
    }
}
