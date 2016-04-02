public class StrConcat {
    public StrConcat() {}
    public static int test() {
	char[] x = new char[3];
	
	String s = new String();
	s.chars = x;
	
	return s.length();
	
    }

    
}