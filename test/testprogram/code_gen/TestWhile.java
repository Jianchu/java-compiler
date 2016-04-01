public class TestWhile {
    public TestWhile() {}
    public int m(int x) {
        while (x>0) { 
            int y = x;
            y=y-1;  
            x = y;
        };
        return x;
    }
    public static int test() {
        return new TestWhile().m(17)+123;
    }
}

