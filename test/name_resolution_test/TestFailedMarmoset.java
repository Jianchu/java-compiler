package name_resolution_test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import joosc.Joosc;

import org.junit.Before;
import org.junit.Test;

public class TestFailedMarmoset {

    private final static File Failedfiles = new File(System.getProperty("user.dir")
            + "/test/testprogram/failed");
    private final static Map<String, ArrayList<String>> testCases = new HashMap<String, ArrayList<String>>();

    @Before
    public void setUp() {
        storeTestCases();
    }

    @Test
    public void test() {
        int expect = 0;
        for (String testcase : testCases.keySet()) {
            System.out.println(testcase);
            if (testcase.contains("J1_") || testcase.contains("J2_")) {
                expect = 0;
            } else {
                expect = 42;
            }
            String[] input = new String[testCases.get(testcase).size()];
            input = testCases.get(testcase).toArray(input);
            int result = Joosc.compileSTL(input);
            if (result != expect) {
                System.exit(0);
            }
        }
    }
    
    private static void storeTestCases() {
        for (File file : Failedfiles.listFiles()) {
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
