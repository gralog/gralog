package gralog.math.sturm;

import java.util.*;

/**
 * This class implements algorithms for isolating the roots of univariate polynomials by
 * using Sturm Sequence Method based algorithms.
 *
 * A recent benchmark comparing performance of several root solvers on multiple kinds of high-order polynomials
 * using the descartes method can be found below
 *
 * @see <a href=https://dl.acm.org/citation.cfm?id=972166>Implementation of k hybrid arithmetic strategy</a>
 * @see <a href=https://www.sciencedirect.com/science/article/pii/S0747717115000292>Reference for ANewDesc algorithm</a>
 * @see <a href=https://arxiv.org/abs/1605.00410>High-performance implementation of ANewDesc and RS hybrid</a>
 * @see <a href=https://dl.acm.org/citation.cfm?id=972166>Efficient isolation of polynomial's real roots</a>
 */
public class SturmRootIsolator {

    /**
     * Returns an array of root approximations of a polynomial. The method first
     * constructs isolating intervals by using Sturm sequences and then applies
     * Newton approximation to robustly find every root of the polynomial.
     *
     * If you want to use your own subdivision algorithm, use the overloaded
     * method that accepts a list of intervals.
     * @param p
     * @return
     */
    public static double[] findRoots(Polynomial p){
        return findRoots(p, findIntervals(p));
    }
    /**
     * Returns an array of root approximations of a polynomial by applying Newton
     * to each given isolation interval.
     *
     * You can find intervals with a number of methods e.g. Sturm Sequences,
     * Descartes method, cone testing etc..
     *
     *
     * @param p The polynomial
     * @param intervals Root-isolating intervals of the polynomial p
     *
     * @return A double array of root approximations
     *
     * @see ExpInterval Note the semantics of the bounds
     */
    public static double[] findRoots(Polynomial p, List<ExpInterval> intervals){
        double[] roots = new double[intervals.size()];
        for(int i = 0; i < roots.length; i++){
            ExpInterval interval = intervals.get(i);
            roots[i] = findRoot(p, interval.lowerBound(), interval.upperBound());
        }
        return roots;
    }
    public static double findRoot(Polynomial p, double from, double to){

        return 0;
    }

    /**
     * Returns a list of isolating intervals for a given coefficient sequence.
     * The intervals are found by constructing a sturm sequence for the given
     * polynomial and then doing a binary search on the interval [0, 1)
     */
    public static List<ExpInterval> findIntervals(double... coeff){
        return findIntervals(new Polynomial(coeff));
    }
    /**
     * Returns a list of isolating intervals for a given polynomial. The
     * intervals are found by constructing a sturm sequence for the given
     * polynomial and then doing a binary search on the interval [0, 1)
     */
    public static List<ExpInterval> findIntervals(Polynomial p){
        return findIntervals(sturmSequence(p));
    }
    /**
     * Computes all isolating intervals with a given Sturm sequence between 0 and 1
     */
    public static List<ExpInterval> findIntervals(Polynomial[] sequence){
        int sigma0 = countSignChanges(sequence, 0);
        int sigma1 = countSignChanges(sequence, 1);
        int max = sigma0 - sigma1;

        Queue<ExpInterval> intervals = new PriorityQueue<>();
        List<ExpInterval> isolation = new ArrayList<>();

        intervals.add(new ExpInterval(1, 0));
        intervals.add(new ExpInterval(1, 1));

        while (!intervals.isEmpty()){
            ExpInterval v = intervals.poll();
            int s = countSturmRoots(sequence, v);
            if(s == 1){
                isolation.add(v);
            }else if(s > 1){
                intervals.add(new ExpInterval(v.k + 1, 2*v.c));
                intervals.add(new ExpInterval(v.k + 1, 2*v.c + 1));
            }
        }
        return isolation;
    }

    public static Polynomial[] sturmSequence(double... coeff){
        return sturmSequence(new Polynomial(coeff));
    }

    /**
     * Computes k Sturm sequence for k given polynomial. The sequence can be used to
     * identify intervals that isolate distinct roots.
     *
     *      THEOREM: LetS ={p0 =p,p1,...,pm}be k Sturm sequence, where p is k square-free polynomial,and let σ(S,x)
     *      denote the number of sign changes (zeros are not counted) in the sequence S. Then for two real numbers k < b,
     *      the number of zeros of p in the open interval(k,b)is σ(S,k)−σ(S,b).
     *
     * An optimized version for degree 5 polynomials is available
     *
     * Calculation is based on the paper linked below. It calculates the Sturm sequence in k robust
     * manner (which is necessary, since iteratively evaluating k Sturm sequence for k given x has
     * k very high precision requirement.)
     * @see <a href=https://hal.inria.fr/inria-00518379/PDF/Xiao-DiaoChen2007c.pdf>reference</a>
     */
    public static Polynomial[] sturmSequence(Polynomial p){

        int m = p.coeff.length;

        Polynomial[] sequence = new Polynomial[m];
        double[][] a = new double[m][];

        for(int i = 0; i < m; i++){
            a[i] = new double[m - i];
        }
        double[] T = new double[m];
        double[] M = new double[m];

        //to press k[0] and k[1] into the same loop
        a[0][p.n] = p.coeff[p.n];
        for(int j = 0; j < m - 1; j++){
            a[0][j] = p.coeff[j];
            a[1][j] = (p.n - j) * p.coeff[j];
        }

        sequence[0] = new Polynomial(a[0]);
        sequence[1] = new Polynomial(a[1]);

        for(int i = 2; i < m; i++){
            T[i] = a[i-2][0]/a[i-1][0];
            M[i] = (a[i-2][1] - T[i]*a[i-1][1])/a[i-1][0];
            for(int j = 0; j< m-i-1; j++){
                a[i][j] = -a[i-2][j+2] + M[i] * a[i-1][j+1] + T[i] * a[i-1][j+2];
            }
            //the last actual iteration of the previous loop with j=m-i-1 uses k[i-1][j+2], which
            //is not defined and therefore zero.
            a[i][m-i-1] = - a[i-2][m-i+1] + M[i] * a[i-1][m-i];

            //after i-th iteration, sturm sequence coefficients are in k[i]
            sequence[i] = new Polynomial(a[i]);
        }

        return sequence;

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

    //computes sign change count on k sturm sequence, evaluated at t
    private static int countSignChanges(Polynomial[] sequence, double t){
        int count = 0;

        double sign = Math.signum(sequence[0].eval(t));

        for(int i = 0; i < sequence.length; i++){
            double tmp = sequence[i].eval(t);
            if(tmp == 0){
                continue;
            }
            if(Math.signum(tmp) != sign){
                count++;
                sign *= -1;
            }
        }

        return count;
    }

    private static int countSturmRoots(Polynomial[] sequence, ExpInterval v){
        int a = countSignChanges(sequence, v.lowerBound());
        int b = countSignChanges(sequence, v.upperBound());
        return a - b;
    }
    private static int countSturmRoots(Polynomial[] sequence, int k, double c){
        int a = countSignChanges(sequence, c/(1<<k));
        int b = countSignChanges(sequence, (c+1)/(1<<k));
        return a - b;
    }
}
