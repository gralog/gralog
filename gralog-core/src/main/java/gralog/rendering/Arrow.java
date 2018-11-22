/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.rendering;

import java.io.Serializable;

/**
 * All public static arrows should have their pointer at the origin and their maximum
 * x-length should be 1, so that scaling the arrow across all types becomes consistent.
 */
public class Arrow implements Serializable {
    public enum LineFlag {
        POLY,
        LINE
    }
 static final Arrow TYPE1 = new Arrow(
            new double[]{0, -0.5, -1.0, -0.8, -1.0, -0.5},
            new double[]{0, 0.14, 0.35, 0.0, -0.35, -0.14},
            6,
            -0.8,
            LineFlag.POLY);
    public static final Arrow TYPE2 = new Arrow(
            new double[]{-0.4, -0.31, -0.18, 0, -0.18, -0.31, -0.4},
            new double[]{0.40, +0.23, 0.10, 0, -0.10, -0.23, -0.40},
            7,
            -0.1,
            LineFlag.LINE);

    public double[] xPoints;
    public double[] yPoints;
    public int count;
    public double endPoint;
    public LineFlag flag;
    public Arrow(double[] xPoints, double[] yPoints, int count, double endPoint, LineFlag flag) {
        this.xPoints = xPoints;
        this.yPoints = yPoints;
        this.count = count;
        this.endPoint = endPoint;
        this.flag = flag;
    }
}
