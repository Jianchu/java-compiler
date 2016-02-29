package name_resolution_test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import joosc.Joosc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestMarmoset {

    private static final File files = new File(System.getProperty("user.dir")  + "/assignment_testcases/a2");
    private static final Map<String, ArrayList<String>> testCases = new HashMap<String, ArrayList<String>>();
    private final PrintStream oldErr = System.err;
    private int expectedResult;
    private String testCase;
    
    public TestMarmoset(String testCase, int expectedResult) {
        this.testCase = testCase;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection testA1() throws Exception {
        storeTestCases();
        // pass J1_, J2_, Je_, or All
        Object[][] parameters = getParameters("J2_");
        return Arrays.asList(parameters);
    }
    
    private static Object[][] getParameters(String name) throws Exception {
        boolean checkJ1, checkJ2, checkJe;
        checkJ1 = checkJ2 = checkJe = false;
        Object[][] parameters;
        if (name == "J1_") {
            parameters = new Object[101][2];
            checkJ1 = true;
        } else if (name == "J2_") {
            parameters = new Object[18][2];
            checkJ2 = true;
        } else if (name == "Je_") {
            parameters = new Object[109][2];
            checkJe = true;
        } else if (name == "All") {
            parameters = new Object[testCases.keySet().size()][2];
            checkJ1 = checkJ2 = checkJe = true;
        } else {
            throw new Exception("have to use J1_, J2_, Je_, or All");
        }

        int i = 0;
        for (String testcase : testCases.keySet()) {

            if (testcase.contains("J1_")) {
                if (checkJ1) {
                    parameters[i][0] = testcase;
                    parameters[i][1] = 0;
                    i++;
                }
            } else if (testcase.contains("J2_")) {
                // J2 should pass or fail?
                if (checkJ2) {
                    parameters[i][0] = testcase;
                    parameters[i][1] = 42;
                    i++;
                }
            } else if (testcase.contains("Je_")) {
                if (checkJe) {
                    parameters[i][0] = testcase;
                    parameters[i][1] = 42;
                    i++;
                }
            }
        }
        return parameters;

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

    private static void storeTestCases() {
        for (File file : files.listFiles()) {
            if (file.isFile()) {
                testCases.put(file.getName(), new ArrayList<String>(Arrays.asList(file.getAbsolutePath())));
            } else {
                testCases.put(file.getName(), getFileNames(file));
            }
        }
    }

    private static ArrayList<String> getFileNames(File dir) {
        List<String> fileNames = new ArrayList<String>();
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                fileNames.addAll(getFileNames(file));
            } else {
                fileNames.add(file.getAbsolutePath());
            }
        }
        return (ArrayList<String>) fileNames;
    }

}