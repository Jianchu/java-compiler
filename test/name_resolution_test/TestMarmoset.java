package name_resolution_test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class TestMarmoset {


    private static File files;
    private Map<String, ArrayList<String>> testCases;
    
    
    @Before
    public void setUp() {
        files = new File(System.getProperty("user.dir")  + "/assignment_testcases/a2");
        testCases = new HashMap<String, ArrayList<String>>();
    }
    
    @Test
    public void test() {
        storeTestCases();
        for (String testcase : testCases.keySet()) {
            System.out.println(testcase);
            System.out.println(testCases.get(testcase));
        }
    }

    private void storeTestCases() {
        for (File file : files.listFiles()) {
            if (file.isFile()) {
                testCases.put(file.getName(), new ArrayList<String>(Arrays.asList(file.getAbsolutePath())));
            } else {
                testCases.put(file.getName(), getFileNames(file));
            }
        }
    }

    private ArrayList<String> getFileNames(File dir) {
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