package type_checking_test;

import name_resolution_test.TestingHelper;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import joosc.Joosc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestMarmosetA3 {

    private static Map<String, ArrayList<String>> testCases;
    private static TestingHelper helper = new TestingHelper("a3");
    private final PrintStream oldErr = System.err;
    private int expectedResult;
    private String testCase;

    public TestMarmosetA3(String testCase, int expectedResult) {
        this.testCase = testCase;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection testA1() throws Exception {
        testCases = helper.getTestCases();
        // pass J1&2, Je, or All
        Object[][] parameters = helper.getParameters("Je");
        return Arrays.asList(parameters);
    }

    @Test
    public void test() {
        if (testCase.contains("Je_")) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            System.setErr(ps);
        } else {
            System.out.flush();
            System.setErr(oldErr);
        }
        String[] input = new String[testCases.get(testCase).size()];
        input = testCases.get(testCase).toArray(input);
        int result = Joosc.compileSTL(input);
        if (result != expectedResult) {
            System.out.println(testCase);
        }
        assertEquals(expectedResult, result);
        // System.out.println(testCases.get(testCase));
    }
}