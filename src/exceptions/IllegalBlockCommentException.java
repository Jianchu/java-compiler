package exceptions;

public class IllegalBlockCommentException extends Exception {
    public IllegalBlockCommentException() {
        super("block comment not terminated");
    }
}