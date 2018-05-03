package gralog.math.descartes;


import java.util.Arrays;
import java.util.HashSet;
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
 * @see <a href=https://dl.acm.org/citation.cfm?id=972166>Efficient isolation of polynomial's real roots</a>
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
        HashSet<Interval> isol = new HashSet<>();

        tree.add(new Interval(0, 0));

        Polynomial p = new Polynomial(coeff);

        while(!tree.isEmpty()){
            Interval q = tree.pollFirst();
            int s = descartesBound5(p, q);
            System.out.printf("s=%d,\tk=%d,\tc=%d\n",s,q.k,q.c);
            if(s == 1){
                isol.add(q);
            }else if(s > 1){
                //addSuc from p.37, referencing paper no. 4 in class description
                tree.add(new Interval(q.k + 1, 2 * q.c));
                tree.add(new Interval(q.k + 1, 2 * q.c + 1));
            }
        }

        System.out.println(isol);

    }

    public static void sturmSequenceIsolation(double... coeff){
        sturmSequenceIsolation(new Polynomial(coeff));
    }

    /**
     * Computes a Sturm sequence for a given polynomial. The sequence can be used to
     * identify intervals that isolate distinct roots.
     *
     *      THEOREM: LetS ={p0 =p,p1,...,pm}be a Sturm sequence, where p is a square-free polynomial,and let σ(S,x)
     *      denote the number of sign changes (zeros are not counted) in the sequence S. Then for two real numbers a < b,
     *      the number of zeros of p in the open interval(a,b)is σ(S,a)−σ(S,b).
     *
     * An optimized version for degree 5 polynomials is available
     *
     * Calculation is based on the paper linked below. It calculates the Sturm sequence in a robust
     * manner (which is necessary, since iteratively evaluating a Sturm sequence for a given x has
     * a very high precision requirement.)
     * @see <a href=https://hal.inria.fr/inria-00518379/PDF/Xiao-DiaoChen2007c.pdf>reference</a>
     */
    public static void sturmSequenceIsolation(Polynomial p){

        int m = p.coeff.length;

        Polynomial[] sturmSequence = new Polynomial[m];
        double[][] a = new double[m][];

        for(int i = 0; i < m; i++){
            a[i] = new double[m - i];
        }
        double[] T = new double[m];
        double[] M = new double[m];

        //to press a[0] and a[1] into the same loop
        a[0][p.n] = p.coeff[p.n];
        for(int j = 0; j < m - 1; j++){
            a[0][j] = p.coeff[j];
            a[1][j] = (p.n - j) * p.coeff[j];
        }
        for(int i = 2; i < m; i++){
            T[i] = a[i-2][0]/a[i-1][0];
            M[i] = (a[i-2][1] - T[i]*a[i-1][1])/a[i-1][0];
            for(int j = 0; j< m-i-1; j++){
                a[i][j] = a[i-2][j+2] - M[i] * a[i-1][j+1] - T[i] * a[i-1][j+2];
            }
            //the last actual iteration of the previous loop with j=m-i-1 uses a[i-1][j+2], which
            //is not defined and therefore zero.
            a[i][m-i-1] = a[i-2][m-i+1] - M[i] * a[i-1][m-i];

        }

        double u = 0.5;

        //WTF
        for(int j=0; j < m-2; j++){
            a[2][j] *= -1;
        }
        for(int j=0; j < m-3; j++){
            a[3][j] *= -1;
        }
        //TODO: Find out why at index 2 and 3 the coefficients are all inverted....how does that behaviour change
        //for different sized polynomials

        for(int i = 0; i<m; i++){
            sturmSequence[i] = new Polynomial(a[i]);
            System.out.println(sturmSequence[i]);
            a[0][i] = sturmSequence[i].eval(u);
        }

        System.out.println(countSignChanges(a[0]));


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

        double a0 = twoK*(twoK*(twoK*(twoK*(twoK*(p.coeff[0])+c1 * p.coeff[1])+c2* p.coeff[2])+c3* p.coeff[3])+c4* p.coeff[4])+c5* p.coeff[5];
        double a1 = 16 * p.coeff[1] + 16 * p.coeff[2] * c1 + 4 * p.coeff[3] * 3 * c2 + 2 * p.coeff[4] * 4 * c3 + p.coeff[5] * 5 * c4;
        double a2 = 8 * p.coeff[2] + 4 * p.coeff[3] * 3 * c1 + 2 * p.coeff[4] * 6 * c2 + p.coeff[5] * 10 * c3;
        double a3 = 4 * p.coeff[3] + 2 * p.coeff[4] * 4 * c1 + p.coeff[5] * 10 * c2;
        double a4 = 2 * p.coeff[4] + p.coeff[5] * 5 * c1;
        double a5 = p.coeff[5];
        //System.out.printf("%.2f\t\t%.2f\t\t%.2f\t\t%.2f\t\t%.2f\t\t%.2f\t\t\n", a0, a1, a2, a3, a4, a5);
        System.out.println("" + a0);
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
