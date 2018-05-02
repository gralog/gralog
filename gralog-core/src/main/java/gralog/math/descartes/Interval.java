package gralog.math.descartes;

/**
 * Represents intervals for root isolation
 */
public class Interval implements Comparable<Interval>{

    int k;
    int c;

    public Interval(int k, int c){
        this.k = k;
        this.c = c;
    }

    @Override
    public int compareTo(Interval o) {
        return k + c - o.k - o.c;
    }
}
