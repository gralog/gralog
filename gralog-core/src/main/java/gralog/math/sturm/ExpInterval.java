/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.math.sturm;

/**
 * Represents intervals for root isolation. The interval
 */
public class ExpInterval implements Interval, Comparable<ExpInterval> {

    int k;
    double c;

    public ExpInterval(int k, double c) {
        this.k = k;
        this.c = c;
    }

    public double upperBound() {
        return (c + 1) / (1 << k);
    }

    public double lowerBound() {
        return c / (1 << k);
    }

    /**
     * Tests if a real number is in the interval
     *
     * @return
     */
    public boolean contains(double t) {
        return t > lowerBound() && t < upperBound();
    }

    @Override
    public String toString() {
        return String.format("(%.2f,%.2f)", c / (1 << k), (c + 1) / (1 << k));
    }

    @Override
    public int compareTo(ExpInterval o) {
        return (int) Math.signum(c / (1 << k) - o.c / (1 << o.k));
    }
}
