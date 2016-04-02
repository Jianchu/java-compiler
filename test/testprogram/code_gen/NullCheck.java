public class NullCheck{
    public NullCheck() {}
    public static int test() {
	NullCheck n = null;
	return n.m();
    }
    public int m() {
	return 123;
    }
}