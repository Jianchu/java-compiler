public class TestWhile {
    public TestWhile() {}
    public static int test() {
        int x = 17;
        while (x > 0) {
            int y = x;
            y = y - 1;
            x = y;
        }
        return x + 123;
    }
}

