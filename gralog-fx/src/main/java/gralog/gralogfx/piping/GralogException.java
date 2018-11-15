package gralog.gralogfx.piping;
public class GralogException extends Exception {
    public GralogException(String message) {
        super(message);
    }
    @Override
    public String toString() {
    	return this.getClass().getSimpleName() + ":\n" + this.getMessage();
    }
}
