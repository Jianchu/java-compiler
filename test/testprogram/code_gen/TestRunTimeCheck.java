public class TestRunTimeCheck {
    public Object c = new Object();
    public TestRunTimeCheck() {}
    public static int test() {
        Object a = new Object();
        Integer b = new Integer(3);
        if (a instanceof Integer) {
            return 10;
        } else {
            return 9;
        }
    }
}