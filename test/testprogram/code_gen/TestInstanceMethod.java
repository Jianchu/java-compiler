public class TestInstanceMethod {
    public TestInstanceMethod() {}
    public static int test() {
        TestWhile a = new TestWhile();
        int b = a.m();
        return 1;
    }
    
    public int m() {
        return 123;
    }
}


