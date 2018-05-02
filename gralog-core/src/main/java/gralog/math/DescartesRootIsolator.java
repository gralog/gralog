package gralog.math;

import java.util.SortedSet;
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
     * Description of univariate (k,c)-polynomial of degree n,
     * following the description of
     *
     * Rouillier, F., & Zimmermann, P. (2004).
     * Efficient isolation of polynomial's real roots.
     * Journal of Computational and Applied Mathematics, 162(1), 33-50.
     *
     */
    private static class Polynomial {
        public int n;
        public double[] params;
        Polynomial(double... params){
            this.n = params.length - 1;
            this.params = params;
        }
    }

    /**
     * Represents intervals for root isolation
     */
    private static class Interval implements Comparable<Interval>{
        int k;
        int c;
        Interval(int k, int c){ this.k = k; this.c = c; }

        @Override
        public int compareTo(Interval o) {
            return k + c - o.k - o.c;
        }
    }
    /**
     * Evaluates the (k,c)-polynomial according to it's definition in the paper.
     * Uses Horner's method.
     * @see <a href=https://dl.acm.org/citation.cfm?id=972166>P. 35 of the proceedings</a>
     */
    private static double eval(Polynomial p, Interval v, double x){
        x = (x + v.c)/(Math.pow(2, v.k));
        double result = 0;
        for(int i = p.n-1; i >= 0; i--){
            result = p.params[i] + (x * result);
        }
        return Math.pow(2, v.k * p.n) * result;
    }


    public static void genericDescartes(double... params){

        TreeSet<Interval> tree = new TreeSet<>();
        tree.add(new Interval(0, 0));

        Polynomial p = new Polynomial(params);

    }
    private static Interval getNode(TreeSet<Interval> tree){
        return tree.pollFirst();
    }

    private static int descartesBound(Polynomial p){
        return 1;
    }
}
