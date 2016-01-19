package scannertest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import scanner.Token;
import scanner.TokenType;
import exceptions.IllegalCharException;
import exceptions.IllegalIDException;

public class TestLiteral {

    @Before
    public void setUp() throws Exception {
    }

    ScannerTest scannerTest = new ScannerTest();
    /*
     * For integers
     */

    @Test
    public void testInt() {
        String num = "123712";
        List<Token> tokens = scannerTest.inputSetUp(num);
        assertEquals(1, tokens.size());
        assertEquals(num, tokens.get(0).getLexeme());
    }

    @Test
    public void testIntSpace() {
        String num = "123" + " sdf";
        List<Token> tokens = scannerTest.inputSetUp(num);
        assertEquals(2, tokens.size());
        assertEquals("123", tokens.get(0).getLexeme());
    }

    @Test
    public void testIntFail() throws Exception {
        scannerTest.thrown.expect(IllegalIDException.class);
        String num = "123c";
        scannerTest.inputSetUpException(num);
    }

    /*
     * Characters
     */
    @Test
    public void testChar() {
        String in = "\'c\'";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals(in, tokens.get(0).getLexeme());
    }

    @Test
    public void testCharEscape() {
        String in = "\'\\b\'";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals(in, tokens.get(0).getLexeme());
        // printTokens(tokens);
    }

    @Test
    public void testCharFail() throws Exception {
        String in = "\'sa\'";
        scannerTest.thrown.expect(IllegalCharException.class);
        List<Token> tokens = scannerTest.inputSetUpException(in);
    }

    @Test
    public void testCharEscapeFail() throws Exception {
        String in = "\'\\a\'";
        scannerTest.thrown.expect(IllegalCharException.class);
        List<Token> tokens = scannerTest.inputSetUpException(in);
    }

    /* 
     * Strings
     */

    @Test
    public void testString() throws Exception {
        String in = "\"asdf\"";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals(in, tokens.get(0).getLexeme());
    }

    @Test
    public void testStringEscape() throws Exception {
        String in = "\"as\tdf\"";
        List<Token> tokens = scannerTest.inputSetUp(in);
        // printTokens(tokens);
        assertEquals(3, tokens.size());
        assertEquals(in, tokens.get(0).getLexeme());
    }
    
    /*
     * null
     */
    
    @Test
    public void testNull() {
        String in = "null";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals(TokenType.NULL, tokens.get(0).getTokenType());
    }
}
