package scannertest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import scanner.Token;

public class TestProgram {

    ScannerTest scannerTest;
    File file;

    @Before
    public void setUp() throws Exception {
        scannerTest = new ScannerTest();
        file = new File(System.getProperty("user.dir") + "/test/testprogram");
    }

    @Test
    public void test() throws IOException {
        for (final File fileEntry : file.listFiles()) {
            if (fileEntry.getName().equals("super_long_string.txt")) {
                continue;
            }
            String content = new String(Files.readAllBytes(Paths.get(fileEntry
                    .getAbsolutePath())));
            List<Token> tokens = scannerTest.inputSetUp(content);
            System.out.println(fileEntry.getName());
                // scannerTest.printlnTokens(tokens);
            System.out.println("***********************");

        }
    }

}
