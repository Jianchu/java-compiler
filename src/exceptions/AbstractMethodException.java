package exceptions;

public class AbstractMethodException extends Exception {
    public AbstractMethodException() {
        super("non-abstract class has abstract method");
    }

    public AbstractMethodException(String m) {
        super(m);
    }
}
