package gralog.rendering;

/**
 * All public static arrows should have their pointer at the origin and their maximum
 * x-length should be 1, so that scaling the arrow across all types becomes consistent.
 */
public class Arrow
{
    public static final Arrow TYPE1 = new Arrow(
            new double[]{0, -0.5, -1.0, -0.8, -1.0,  -0.5},
            new double[]{0, 0.14,  0.35,  0.0, -0.35, -0.14},
            6,
            -0.8);

    public double[] xPoints;
    public double[] yPoints;
    public int count;
    public double endPoint;

    public Arrow(double[] xPoints, double[] yPoints, int count, double endPoint){
        this.xPoints = xPoints;
        this.yPoints = yPoints;
        this.count = count;
        this.endPoint = endPoint;
    }
}
