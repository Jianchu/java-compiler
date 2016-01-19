package scannertest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import scanner.Token;

public class TestComments {

    @Before
    public void setUp() throws Exception {
    }

    ScannerTest scannerTest = new ScannerTest();
    
    /*
     * Comments
     */
    
    @Test
    public void testComment1() {
        String in = "//this is a comment";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(0, tokens.size());
    }

    @Test
    public void testIdBeforeComment() {
        String in = "notComment //this is a comment";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(1, tokens.size());
    }

    @Test
    public void testIdAfterComment() {
        String in = "//this is a comment\nbutNotThis";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(1, tokens.size());
    }

    @Test
    public void testMultiComment() {
        String in = "var1//commentA\nvar2//commentB\rvar3//commentC\r\nvar4";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(4, tokens.size());
    }

    @Test
    public void testBlockComment() {
        String in = "/* This is a block comment*/";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(0, tokens.size());
    }

    @Test
    public void testBlockCommentNewline() {
        String in = "/* This is a block comment\n   with a newline in it */";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(0, tokens.size());
    }

    @Test
    public void testBlockCommentStar() {
        String in = "/* This block comment* has * extra stars in it **/";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(0, tokens.size());
    }

    @Test
    public void testCommentedBlockComment() {
        String in = "// /*\nnotAComment\n// */";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(1, tokens.size());

    }
}
