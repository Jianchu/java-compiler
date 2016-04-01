public class TestFor {
    public TestFor() {}
    public static int test() {
        int b = 0;
        for (int i = 0; i < 100; i = i + 1) {
            b = i;
        }
        return b;
    }
}

