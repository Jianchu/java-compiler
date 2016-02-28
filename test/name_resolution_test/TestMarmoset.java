package name_resolution_test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
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
    private int expectedResult;
    private String testCase;
    
    public TestMarmoset(String testCase, int expectedResult) {
        this.testCase = testCase;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection testA1() throws FileNotFoundException {
        storeTestCases();
        Object[][] parameters = new Object[testCases.keySet().size()][2];
        int i = 0;
        for (String testcase : testCases.keySet()) {
            if (testcase.contains("Je_")) {
                parameters[i][0] = testcase;
                parameters[i][1] = 42;
            } else if (testcase.contains("J1_")) {
                parameters[i][0] = testcase;
                parameters[i][1] = 0;
            } else {
                // J2 should fail?
                parameters[i][0] = testcase;
                parameters[i][1] = 0;
            }
            i++;
        }
        return Arrays.asList(parameters);
    }
    
    @Test
    public void test() {
        System.out.println(testCase);
        String[] input = new String[testCases.get(testCase).size()];
        input = testCases.get(testCase).toArray(input);
        int result = Joosc.compileSTL(input);
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