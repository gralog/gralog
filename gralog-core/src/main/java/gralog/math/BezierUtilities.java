package gralog.math;

import gralog.rendering.Vector2D;

import java.util.Set;

/**
 * This class implements several utilities for calculating point projections
 * and collisions to bezier curves or polynomial functions.
 */
public final class BezierUtilities {

    /**
     * Used for isolating polynomial roots
     */
    private static class Interval{
        public double x;
        public double y;
    }

    /**
     * Description of univariate polynomial of degree n
     */
    private static class Polynomial{
        public int n, c, k;
        public Polynomial(int n, int c, int k){ this.n = n; this.c = c; this.k = k; }
    }
    /**
     * This method implements an algorithm on point projections for cubic bezier curves.
     *
     * The algorithm is outlined in
     *
     * Xiao-Diao Chen, Yin Zhou, Zhenyu Shu, Hua Su, Jean-Claude Paul.
     * "Improved Algebraic Algorithm On Point Projection For Bézier Curves",
     * (IMSCCS 2007), Iowa, United States.
     *
     * @param m The projection point.
     * @param p0 Bezier-curve starting point.
     * @param p1 1st bezier control point.
     * @param p2 2nd bezier control point.
     * @param p3 Bezier-curve ending point.
     * @return Returns the vector on the given bezier curve with minimal distance to m.
     *
     * @see <a href=https://hal.inria.fr/inria-00518379/PDF/Xiao-DiaoChen2007c.pdf>
     * https://hal.inria.fr/inria-00518379/PDF/Xiao-DiaoChen2007c.pdf</a>
     * @see <a href=https://ieeexplore.ieee.org/document/4392595/>alternative IEEE link</a>
     */
    public static Vector2D pointProjectionAlgebraic(Vector2D m, Vector2D p0, Vector2D p1, Vector2D p2, Vector2D p3){
        double aa = p0.multiply(p0);
        double ab = p0.multiply(p1);
        double ac = p0.multiply(p2);
        double ad = p0.multiply(p3);
        double bb = p1.multiply(p1);
        double bc = p1.multiply(p2);
        double bd = p1.multiply(p3);
        double cc = p2.multiply(p2);
        double cd = p2.multiply(p3);
        double dd = p3.multiply(p3);

        double pa = m.multiply(p0);
        double pb = m.multiply(p1);
        double pc = m.multiply(p2);
        double pd = m.multiply(p3);

        double t = 0.5;

        //polynomial params for objective equation for constant curve weights. Paper reference: equation (4)
        double a = - 3 * (aa + dd + 6*(ac - ab + bd - cd) - 2*ad + 9*(bb + cc - 2*bc));
        double b = - 15 * (ad - aa + 5*ab - 4*ac - 6*bb + 9*bc - 2*bd - 3*cc + cd);
        double c = - (30*aa - 120*ab + 72*ac + 12*(bd - ad) + 108*(bb - bc) + 18*cc);
        double d = - (-30*aa + 90*ab - 36*ac - 54*bb + 27*bc - 3 * (pd - 3*pc + 3*pb - pa - ad));
        double e = - (15*aa - 30*ab + 9*bb - 6 * (pc - 2*pb + pa - ac));
        double f = -3 * (ab - aa - pb + pa);

        //derivative of function
        double dG = evalPolynomial5(t, a, b, c, d, e, f);
        System.out.println(dG);
        //return dG;

        double x = Math.pow(t, 3) * p3.getX() + 3 * Math.pow(t, 2) * (1 - t) * p2.getX() + 3 * Math.pow(1-t, 2) * t *p1.getX() + Math.pow(1-t, 3) * p0.getX();
        double y = Math.pow(t, 3) * p3.getY() + 3 * Math.pow(t, 2) * (1 - t) * p2.getY() + 3 * Math.pow(1-t, 2) * t *p1.getY() + Math.pow(1-t, 3) * p0.getY();

        return new Vector2D(x, y);
    }
    private static double evalPolynomial5(double t, double a, double b, double c, double d, double e, double f){
        double v = a;
        v = b + (t * v);
        v = c + (t * v);
        v = d + (t * v);
        v = e + (t * v);
        return f + (t * v);

    }
    public static double projectionDerivativeMiddle(Vector2D m, Vector2D p0, Vector2D p1, Vector2D p2, Vector2D p3){
        double aa = p0.multiply(p0);
        double ab = p0.multiply(p1);
        double ac = p0.multiply(p2);
        double ad = p0.multiply(p3);
        double bb = p1.multiply(p1);
        double bc = p1.multiply(p2);
        double bd = p1.multiply(p3);
        double cc = p2.multiply(p2);
        double cd = p2.multiply(p3);
        double dd = p3.multiply(p3);

        double pa = m.multiply(p0);
        double pb = m.multiply(p1);
        double pc = m.multiply(p2);
        double pd = m.multiply(p3);

        double t = 0.5;

        //polynomial params
        double a = - 3 * (aa + dd + 6*(ac - ab + bd - cd) - 2*ad + 9*(bb + cc - 2*bc));
        double b = - 15 * (ad - aa + 5*ab - 4*ac - 6*bb + 9*bc - 2*bd - 3*cc + cd);
        double c = - (30*aa - 120*ab + 72*ac + 12*(bd - ad) + 108*(bb - bc) + 18*cc);
        double d = - (-30*aa + 90*ab - 36*ac - 54*bb + 27*bc - 3 * (pd - 3*pc + 3*pb - pa - ad));
        double e = - (15*aa - 30*ab + 9*bb - 6 * (pc - 2*pb + pa - ac));
        double f = -3 * (ab - aa - pb + pa);

        //derivative, horners method
        double dG = evalPolynomial5(t, a, b, c, d, e, f);

        return dG;
    }
    /**
     * Not particularly useful! Piecewise linear approximation doesn't scale well.
     * Implements a point projection to a cubic bezier curves using linear subdivision of the curve.
     *
     * @param m The projection point.
     * @param subdivisions number of linear pieces uniformly distributed on the curve
     * @param p0 Bezier-curve starting point.
     * @param p1 1st bezier control point.
     * @param p2 2nd bezier control point.
     * @param p3 Bezier-curve ending point.
     * @return Returns the vector on the given linear approximation with minimal distance to m.
     */
    public static Vector2D pointProjectionLinear(Vector2D m, int subdivisions, Vector2D p0, Vector2D p1, Vector2D p2, Vector2D p3){
        double t;
        double x,y;
        double minx = 0, miny = 0;
        double currentDistance;
        double minimalDistance = Double.POSITIVE_INFINITY;

        for(int i = 0; i < subdivisions; i++){
            t = ((double)i)/subdivisions;
            x = Math.pow(t, 3) * p3.getX() + 3 * Math.pow(t, 2) * (1 - t) * p2.getX() + 3 * Math.pow(1-t, 2) * t *p1.getX() + Math.pow(1-t, 3) * p0.getX();
            y = Math.pow(t, 3) * p3.getY() + 3 * Math.pow(t, 2) * (1 - t) * p2.getY() + 3 * Math.pow(1-t, 2) * t *p1.getY() + Math.pow(1-t, 3) * p0.getY();
            currentDistance = Math.pow(x - m.getX(), 2) + Math.pow(y - m.getY(), 2);

            if(currentDistance < minimalDistance){
                minimalDistance = currentDistance;
                minx = x;
                miny = y;
            }
        }
        return new Vector2D(minx, miny);
    }

}
