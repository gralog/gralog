package gralog.gralogfx.piping;
public class GralogException extends Exception {
    public GralogException(String message) {
        super(message);
    }
    @Override
    public String toString() {
    	System.out.println("schuwupps! canonlical mname is; " + this.getClass().getCanonicalName());
    	return this.getClass().getSimpleName() + ":\n" + this.getMessage();
    }
}
