package parser;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import scanner.Scanner;
import scanner.Token;
import weeder.Weeder;

public class TestValidProgram {

    File grammar;
    File files;

    @Before
    public void setUp() {
        grammar = new File(System.getProperty("user.dir") + "/data/gen/grammar.lr1");
        files = new File(System.getProperty("user.dir") + "/test/testprogram");

    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void runTestCase() throws Exception {
        for (final File fileEntry : files.listFiles()) {
            if (fileEntry.getName().contains(".txt") || fileEntry.isDirectory()) {
                continue;
            }
            System.out.println(fileEntry);
            test(fileEntry);
        }
    }

    private void test(File f) throws Exception {
        // thrown.expect(Exception.class);
        Scanner scanner = new Scanner(new FileReader(f));
        List<Token> tokens = scanner.scan();
        Parser parser = new Parser(tokens, grammar);
        ParseTree parseTree = parser.parse();
        Weeder weeder = new Weeder(parseTree);
        weeder.weed();
    }
}
