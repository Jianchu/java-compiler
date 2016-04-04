public class Offset1 {
    public Offset1() {}
    public static int test() {
	int h =1;

	{
	    int x = 2;
	}

	int j =2;
	return 120 + j + h;
    }
}