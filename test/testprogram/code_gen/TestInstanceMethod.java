public class TestInstanceMethod {
    public TestInstanceMethod() {}
    public static int test() {
        int b = new TestInstanceMethod().m();
        return b;
    }
    
    public int m() {
        return 123;
    }
}


