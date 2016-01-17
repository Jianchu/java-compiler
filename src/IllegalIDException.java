
public class IllegalIDException extends Exception {
	public IllegalIDException() {
	}
	
	public IllegalIDException(String message) {
		super(message + " is not recoginzed.");
	}
}
