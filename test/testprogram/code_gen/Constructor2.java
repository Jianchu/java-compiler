public class Constructor2 {
    public int x;
    public Constructor2(int i, String j, int k) {
	x = k;
    }
    public static int test() {
	//Constructor2 c = new Constructor2(1, "123",123);
	Constructor2 c = new Constructor2(1, (String) null, 123);
	return c.x;
    }

}