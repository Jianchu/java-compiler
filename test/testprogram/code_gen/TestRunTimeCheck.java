public class TestRunTimeCheck {
    public Object c = new Object();
    public TestRunTimeCheck() {}
    public static int test() {
        Object a = new Object();
        if (a instanceof Object) {
            return 10;
        } else {
            return 9;
        }
    }
}