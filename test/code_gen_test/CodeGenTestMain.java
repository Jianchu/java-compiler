package code_gen_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import joosc.Joosc;
import utility.FileUtility;

public class CodeGenTestMain {

    public static void main(String[] args) throws IOException,
            InterruptedException {

        final String myDir = System.getProperty("user.dir");

        String[] paths = new String[0];
        paths = FileUtility.getFileNames(myDir + "/test/testprogram/TestCode.java").toArray(paths);
        Joosc.compileSTL(paths);

        callBash();
    }

    private static void callBash() throws IOException, InterruptedException {
        String myDir = System.getProperty("user.dir");
        String command = "bash " + myDir + "/test/runnasm.sh";
        // String command = "ls";
        final Process p = Runtime.getRuntime().exec(command);
        StringBuilder sb = new StringBuilder();
        Thread getOutPut = new Thread() {
            public void run() {
                String s = "";
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                try {
                    while ((s = stdInput.readLine()) != null) {
                        sb.append(s + "\n");
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
        System.out.println(sb.toString());
    }
 }
