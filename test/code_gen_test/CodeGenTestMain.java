package code_gen_test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import joosc.Joosc;
import utility.FileUtility;

public class CodeGenTestMain {
    private static final File output = new File(System.getProperty("user.dir") + "/output");
    final static String myDir = System.getProperty("user.dir");

    public static void main(String[] args) throws IOException,InterruptedException {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(myDir + "/test/testprogram/code_gen/TestLogic.java").toArray(paths);
        paths = FileUtility.getFileNames(myDir + "/assignment_testcases/a5/J1_6_Assignable_Object_ObjectArray.java").toArray(paths);
        Joosc.compileSTL(paths);
        callBash();
    }

    private static String callBash() throws IOException, InterruptedException {
        final String[] exitCode = new String[1];
        String command = "bash " + myDir + "/test/runnasm.sh";
        final Process p = Runtime.getRuntime().exec(command);
        StringBuilder sb = new StringBuilder();
        Thread getOutPut = new Thread() {
            public void run() {
                String s = "";
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                try {
                    while ((s = stdInput.readLine()) != null) {
                        sb.append(s + "\n");
                        exitCode[0] = s;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        getOutPut.start();

        Thread getError = new Thread() {
            public void run() {
                String s = "";
                BufferedReader stdError = new BufferedReader( new InputStreamReader(p.getErrorStream()));
                try {
                    while ((s = stdError.readLine()) != null) {
                        sb.append(s + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        getError.start();
        getOutPut.join();
        getError.join();
        p.waitFor();
        // System.out.println(sb.toString());
        return exitCode[0];
    }

    public static int testA5(String[] paths) throws IOException, InterruptedException {
        Joosc.compileSTL(paths);
        String exitCode = callBash();
        int result = Integer.parseInt(exitCode);
        return result;
    }
 }
