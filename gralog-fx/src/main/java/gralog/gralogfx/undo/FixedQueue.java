package gralog.gralogfx.undo;

public class FixedQueue<T> {


    private final int size;
    private int count;
    private int startIndex;

    private T[] x;

    public int size(){
        return size;
    }

    public FixedQueue(int size){
        this.size = size;
        this.x = (T[])new Object[size];
    }

    public void removeBottom(){
        startIndex++;
        count--;
    }

    public void push(T obj){
        this.x[(startIndex + count) % size] = obj;
        if(count == size){
            startIndex++;
        }else{
            count++;
        }
    }

    public T pop(){
        count--;
        return x[(startIndex + count) % size];
    }



}
