package code_gen_test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestingHelperA5 {

    private final File files;
    private final Map<String, ArrayList<String>> testCases = new HashMap<String, ArrayList<String>>();
    private int j12, j1e;
    
    public TestingHelperA5() {
        this.files = new File(System.getProperty("user.dir") + "/assignment_testcases/a5");
        j12 = j1e = 0;
        storeTestCases();
    }

    private void storeTestCases() {
        for (File file : files.listFiles()) {
            if (file.getName().contains("J1_") || file.getName().contains("J2_")) {
                j12++;
            } else if (file.getName().contains("j1e_")) {
                j1e++;
            }
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

    public Object[][] getParameters(String name) throws Exception {
        boolean checkJ12, checkJe;
        checkJ12 = checkJe = false;
        Object[][] parameters;
        if (name == "J1&2") {
            parameters = new Object[j12][2];
            checkJ12 = true;
        } else if (name == "J1e") {
            parameters = new Object[j1e][2];
            checkJe = true;
        } else if (name == "All") {
            parameters = new Object[testCases.keySet().size()][2];
            checkJ12 = checkJe = true;
        } else {
            throw new Exception("have to use J1&2, J1e, or All");
        }

        int i = 0;
        for (String testcase : testCases.keySet()) {
            if (testcase.contains("J1_") || testcase.contains("J2_")) {
                if (checkJ12) {
                    parameters[i][0] = testcase;
                    parameters[i][1] = 123;
                    i++;
                }
            } else if (testcase.contains("J1e_")) {
                if (checkJe) {
                    parameters[i][0] = testcase;
                    parameters[i][1] = 13;
                    i++;
                }
            }
        }
        return parameters;
    }

    public Map<String, ArrayList<String>> getTestCases() {
        return testCases;
    }

}
