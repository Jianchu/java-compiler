package parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import scanner.Token;
import weeder.Weeder;

public class TestValidProgram {

    File grammar;
    static File files;



    @Before
    public void setUp() {
        grammar = new File(System.getProperty("user.dir") + "/data/gen/grammar.lr1");
        files = new File(System.getProperty("user.dir") + "/test/assignment_testcases/a1");

    }

    @Test
    public void testProgram() throws FileNotFoundException {
        for (final File fileEntry : files.listFiles()) {
            System.out.println(fileEntry);
            String content = new Scanner(fileEntry).useDelimiter("\\Z").next();
            if (content.contains("EXCEPTION")) {
                assertEquals(42, test(fileEntry));
            } else {
                assertEquals(0, test(fileEntry));
            }
        }
    }

    public int test(File input) {
        try {
            scanner.Scanner scanner = new scanner.Scanner(new FileReader(input));
            List<Token> tokens = scanner.scan();
            Parser parser = new Parser(tokens, grammar);
            ParseTree parseTree = parser.parse();
            Weeder weeder = new Weeder(parseTree);
            weeder.weed();
        } catch (Exception e) {
            return 42;
        }
        return 0;
    }
    

//    public TestValidProgram(File input, int expectedResult) {
//        this.input = input;
//        this.expectedResult = expectedResult;
//    }
    
//  @Rule
//  public ExpectedException thrown = ExpectedException.none();
//
//  @Parameterized.Parameters
//  public static Collection testA1() throws FileNotFoundException {
//      files = new File(System.getProperty("user.dir")
//              + "/assignment_testcases/a1");
//      Object[][] parameters = new Object[320][2];
//      int i = 0;
//      for (final File fileEntry : files.listFiles()) {
//
//          String content = new Scanner(fileEntry).useDelimiter("\\Z").next();
//          if (content.contains("EXCEPTION")) {
//              parameters[i][0] = 42;
//              parameters[i][1] = fileEntry;
//          } else {
//              parameters[i][0] = 0;
//              parameters[i][1] = fileEntry;
//          }
//          i++;
//      }
//      return Arrays.asList(parameters);
//  }
}
