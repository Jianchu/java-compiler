package exceptions;

public class IllegalInputCharException extends Exception {
    public IllegalInputCharException() {
        super("Input included non-ASCII character");
    }
}
