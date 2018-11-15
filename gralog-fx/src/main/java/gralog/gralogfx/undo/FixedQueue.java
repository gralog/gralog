package gralog.gralogfx.undo;

public class FixedQueue<T> {


    private final int size;
    private int count;
    private int startIndex;

    public int poppedInRow = 0;

    private T[] x;

    public int size() {
        return size;
    }

    public int count() {
        return count;
    }

    public FixedQueue(int size) {
        this.size = size;
        this.x = (T[])new Object[size];
    }

    /**
     * Reverts the pop-operation (you can revert n times if you popped
     * n consecutive times). Every push discards the reversion
     */
    public T revertPop() {
        if(poppedInRow > 0) {
            count++;
            poppedInRow--;
            return x[(startIndex + count - 1) % size];
        }
        else {
            return null;
        }
    }

    public void push(T obj) {
        this.x[(startIndex + count) % size] = obj;
        if(count == size) {
            startIndex++;
        }else {
            count++;
        }
        poppedInRow = 0;
    }

    public T pop() {
        if(count == 0) { return null; }
        count--;
        poppedInRow++;
        return x[(startIndex + count) % size];
    }

    public T last() {
        if(count == 0) { return null; }
        return x[(startIndex + count - 1) % size];
    }



}
