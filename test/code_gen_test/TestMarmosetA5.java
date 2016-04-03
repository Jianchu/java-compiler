package code_gen_test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestMarmosetA5 {

    private static Map<String, ArrayList<String>> testCases;
    private static TestingHelperA5 helper = new TestingHelperA5();
    private int expectedResult;
    private String testCase;

    public TestMarmosetA5(String testCase, int expectedResult) {
        this.testCase = testCase;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection testA1() throws Exception {
        testCases = helper.getTestCases();
        // pass J1&2, J1e, or All
        Object[][] parameters = helper.getParameters("All");
        return Arrays.asList(parameters);
    }

    @Test
    public void test() throws IOException, InterruptedException {
        String[] input = new String[testCases.get(testCase).size()];
        input = testCases.get(testCase).toArray(input);
        int result = CodeGenTestMain.testA5(input);
        if (result != expectedResult) {
            System.err.println(testCase);
        }
        assertEquals(expectedResult, result);
    }
}