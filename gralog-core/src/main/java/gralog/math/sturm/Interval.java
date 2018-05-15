package gralog.math.sturm;

public interface Interval {

    double lowerBound();
    double upperBound();

    boolean contains(double t);
}
