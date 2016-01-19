package exceptions;

public class IllegalCharException extends Exception {
	public IllegalCharException() {
		
	}
	
	public IllegalCharException(String m) {
		super(m + " is not valid character.");
	}
}
