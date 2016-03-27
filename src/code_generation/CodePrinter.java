package code_generation;

import java.io.File;

public class CodePrinter {

    private static final File output = new File(System.getProperty("user.dir") + "/output");
    private static final String uglyText = UglyTableBuilder.getUgly();

    public static void printCode() {
        if (!output.exists()) {
            output.mkdirs();
        } else {
            for (File file : output.listFiles()) {
                file.delete();
            }
        }
    }
}
