import java.io.*;

public class Print {
    public Print() {}
    public static int test() {
	PrintStream p = new PrintStream();
	p.println('a');
	return 123;
    }
}