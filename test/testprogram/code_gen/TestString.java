public class TestString {
    public TestString() {
        
    }
    public static int test() {
        boolean a = false;
        if ("" + a instanceof String) {
            return 123;
        } else {
            return 12;
        }
    }
}