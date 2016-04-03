public class Assignment {
    public Assignment() {
    }

    public static int test() {
	int a = 1;
	int b = (a = 3);
	return a;
    }

}