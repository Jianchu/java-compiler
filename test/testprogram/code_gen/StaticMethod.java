public class StaticMethod {
    public StaticMethod() {}
    public static int test() {
	return StaticMethod.a(13, 123);
    }
    public static int a(int i, int j) {
	return j;
    }
}