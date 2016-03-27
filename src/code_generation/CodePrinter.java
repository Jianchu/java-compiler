package code_generation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import utility.StringUtility;

public class CodePrinter {

    private static final File output = new File(System.getProperty("user.dir") + "/output");
    private static final String uglyText = UglyTableBuilder.getUgly();
    private static final String staticFieldInit = CodeGenerator.getStaticFieldInit();

    public static void printCode() throws FileNotFoundException {
        if (!output.exists()) {
            output.mkdirs();
        } else {
            for (File file : output.listFiles()) {
                file.delete();
            }
        }
        wirteUgly();
        wirteStaticFieldInit();
    }

    private static void wirteUgly() throws FileNotFoundException {
        File uglyFile = new File(output.getAbsolutePath() + "/ugly.s");
        PrintWriter writer = new PrintWriter(uglyFile);
        writer.write(uglyText);
        writer.close();
    }

    private static void wirteStaticFieldInit() throws FileNotFoundException {
        File staticFieldInitFile = new File(output.getAbsolutePath() + "/staticinit.s");
        PrintWriter writer = new PrintWriter(staticFieldInitFile);
        StringBuilder fileHead = new StringBuilder();
        StringUtility.appendLine(fileHead, "section. text");
        StringUtility.appendLine(fileHead, "gloabl static_init");
        StringUtility.appendIndLn(fileHead, "static_init:");
        writer.write(fileHead.toString());
        writer.write(staticFieldInit);
        writer.close();
    }
}
