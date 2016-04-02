public class B implements A {
    public B() {}
    public static int test() {
	A b = new B();
	return b.m();
    }

    public int m() {
	return 123;
    }
}