public class StrConcat {
    public char[] chars;
    public StrConcat() {}
    public static int test() {
	char[] x = new char[3];
	for (int i = 0; i < 3; i=i+1) {
	    x[i] = '1';
	}
	
	//StrConcat s = new StrConcat(x);
	String s = new String(x);
	String s2 = new String(x);
	String s3 = s.concat(s2);
	return s3.length();
	
    }

    public StrConcat(char[] chars) {
	this.chars = new char[chars.length];
	//int i = 1;
	//this.chars[0] = '1';
        for(int i = 0; i < chars.length; i = i + 1) this.chars[i] = chars[i];
    }
}