import java.io.*;

public class Print extends PrintStream{
    public Print() {}
    public static int test() {
	PrintStream p = new PrintStream();
	p.println("Hello World");
	
	return 123;
    }
}