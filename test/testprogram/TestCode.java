import java.lang.System;
import java.io.PrintStream;

public class TestCode extends System {
    public static PrintStream out = null;
    public TestCode() {}
    public static int test()  {
        if (1 == 2) {
            return 123;
        } else {
            return 12;
        }
    }
}