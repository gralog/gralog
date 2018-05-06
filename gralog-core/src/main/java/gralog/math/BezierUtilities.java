package gralog.math;

import gralog.math.sturm.ExpInterval;
import gralog.math.sturm.Interval;
import gralog.math.sturm.Polynomial;
import gralog.math.sturm.SturmRootIsolator;
import gralog.rendering.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class implements several utilities for calculating point projections
 * and collisions to bezier curves or polynomial functions.
 */
public final class BezierUtilities {

    /**
     * Encapsulates results of a projection operation
     */
    public static class ProjectionResults{
        public boolean successful;
        public Vector2D result;
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
    public static ProjectionResults pointProjectionAlgebraic(Vector2D m, Vector2D p0, Vector2D p1, Vector2D p2, Vector2D p3){
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

        //polynomial coeff for objective equation for cubic bezier curve with constant weights.
        //Paper reference: equation (4)
        double a = - 3 * (aa + dd + 6*(ac - ab + bd - cd) - 2*ad + 9*(bb + cc - 2*bc));
        double b = - 15 * (ad - aa + 5*ab - 4*ac - 6*bb + 9*bc - 2*bd - 3*cc + cd);
        double c = - (30*aa - 120*ab + 72*ac + 12*(bd - ad) + 108*(bb - bc) + 18*cc);
        double d = - (-30*aa + 90*ab - 36*ac - 54*bb + 27*bc - 3 * (pd - 3*pc + 3*pb - pa - ad));
        double e = - (15*aa - 30*ab + 9*bb - 6 * (pc - 2*pb + pa - ac));
        double f = -3 * (ab - aa - pb + pa);

        //derivative of function
        //double dG = evalPolynomial5(t, a, b, c, d, e, f);

        double x;
        double y;

        Polynomial p = new Polynomial(a,b,c,d,e,f);
        List<Interval> intervals = pruneIntervals(p, SturmRootIsolator.findIntervals(p));
        //List<Interval> intervals = new ArrayList<>();
        //intervals.add(new ExpInterval(2, 2));

        double[] roots = SturmRootIsolator.findRoots(p, intervals);
        double min = Double.MAX_VALUE;
        double finx = Double.MAX_VALUE, finy = Double.MAX_VALUE;

        for(int i = 0; i < roots.length; i++){
            t = roots[i];
            x = ((t * p3.getX() + 3 *(1 - t) * p2.getX())*t+3*Math.pow(1-t, 2) * p1.getX()) *t + Math.pow(1-t, 3) * p0.getX();
            y = ((t * p3.getY() + 3 *(1 - t) * p2.getY())*t+3*Math.pow(1-t, 2) * p1.getY()) *t + Math.pow(1-t, 3) * p0.getY();
            double dist = Math.pow(m.getX() - x,2) + Math.pow(m.getY() - y, 2);
            if(dist < min){
                min = dist;
                finx = x;
                finy = y;
            }
        }
        ProjectionResults result = new ProjectionResults();
        if(roots.length == 0){
            result.successful = false;
        }else{
            result.result = new Vector2D(finx, finy);
            result.successful = true;
        }

        return result;
    }

    /**
     * Prunes intervals so that only local minima of the difference function get selected
     */
    private static List<Interval> pruneIntervals(Polynomial p, List<Interval> intervals){
        List<Interval> res = new ArrayList<>();

        for(Interval v : intervals){
            if(p.eval(v.lowerBound()) > p.eval(v.upperBound())){
                res.add(v);
            }
        }
        return res;
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
