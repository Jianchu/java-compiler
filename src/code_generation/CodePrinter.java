package code_generation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CodePrinter {

    private static final File output = new File(System.getProperty("user.dir") + "/output");
    private static final String uglyText = UglyTableBuilder.getUgly();

    public static void printCode() throws FileNotFoundException {
        if (!output.exists()) {
            output.mkdirs();
        } else {
            for (File file : output.listFiles()) {
                file.delete();
            }
        }
        wirteUgly();
    }

    private static void wirteUgly() throws FileNotFoundException {
        File uglyFile = new File(output.getAbsolutePath() + "/ugly.s");
        PrintWriter writer = new PrintWriter(uglyFile);
        writer.write(uglyText);
        writer.close();
    }

}
