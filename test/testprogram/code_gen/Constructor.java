
public class Constructor {
    public int a;
    public Constructor(int i) {
	a = i;
    }
    public static int test() {
	Constructor c = new Constructor(123);
	return c.a;
    }
}