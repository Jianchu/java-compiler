public class TestInstanceMethod {
    public TestInstanceMethod() {}
    public static int test() {
        TestInstanceMethod a = new TestInstanceMethod();
        int b = a.m();
        return 1;
    }
    
    public int m() {
        return 123;
    }
}


