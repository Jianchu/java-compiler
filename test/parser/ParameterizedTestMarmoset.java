package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import scanner.Token;
import weeder.Weeder;
import ast.AST;

@RunWith(Parameterized.class)
public class ParameterizedTestMarmoset {

    private File input;
    private int expectedResult;
    private static File grammar;
    private static File files;

    public ParameterizedTestMarmoset(File input, int expectedResult) {
        this.input = input;
        this.expectedResult = expectedResult;
    }

    @Before
    public void setUp() {
        grammar = new File(System.getProperty("user.dir") + "/data/grammar.lr1");

    }

    @Parameterized.Parameters
    public static Collection testA1() throws FileNotFoundException {
        files = new File(System.getProperty("user.dir")
                + "/assignment_testcases/a1");
        Object[][] parameters = new Object[320][2];
        int i = 0;
        for (final File fileEntry : files.listFiles()) {

            if (fileEntry.getName().contains("Je_")) {
                parameters[i][0] = fileEntry;
                parameters[i][1] = 42;
            } else {
                parameters[i][0] = fileEntry;
                parameters[i][1] = 0;
            }
            i++;
        }
        return Arrays.asList(parameters);
    }
    
    @Test
    public void testMarmoset() {
        int result = 0;
        // assertEquals(expectedResult, result);
        try {

            scanner.Scanner scanner = new scanner.Scanner(new FileReader(input));
            List<Token> tokens = scanner.scan();
            Parser parser = new Parser(tokens, grammar);
            ParseTree parseTree = parser.parse();
            Weeder weeder = new Weeder(parseTree, input.getName().substring(0,
                    input.getName().lastIndexOf('.')));
            weeder.weed();
            AST ast = new AST(parseTree);
        } catch (Exception e) {
            result = 42;
            if (result != expectedResult) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    
    public int test(File input) {
        try {
            scanner.Scanner scanner = new scanner.Scanner(new FileReader(input));
            List<Token> tokens = scanner.scan();
            Parser parser = new Parser(tokens, grammar);
            ParseTree parseTree = parser.parse();
            Weeder weeder = new Weeder(parseTree, input.getName().substring(0,
                    input.getName().lastIndexOf('.')));
            weeder.weed();
            AST ast = new AST(parseTree);
        } catch (Exception e) {
            return 42;
        }
        return 0;
    }

}
