package scannertest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import scanner.Token;
import scanner.TokenType;

public class TestOperators {

    ScannerTest scannerTest;
    @Before
    public void setUp() throws Exception {
        scannerTest = new ScannerTest();
    }

    

    /*
     * Equals
     */
    @Test
    public void testEqual() throws Exception {
        String in = "==";
        List<Token> tokens = scannerTest.inputSetUp(in);
        scannerTest.printTokens(tokens);
        assertEquals(1, tokens.size());
        assertEquals("<==, EQUAL>", tokens.get(0).toString());
    }

    /*
     * URSHIFT_EQ
     */
    @Test
    public void testUrshift_eq() throws Exception {
        String in = ">>=";
        List<Token> tokens = scannerTest.inputSetUp(in);
        // scannerTest.printTokens(tokens);
        assertEquals(1, tokens.size());
        assertEquals("<>>=, RSHIFT_EQ>", tokens.get(0).toString());
    }

    /*
     * num + num
     */

    @Test
    public void testIdplusId() throws Exception {
        String in = "3 + 3";
        List<Token> tokens = scannerTest.inputSetUp(in);
        // scannerTest.printTokens(tokens);
        assertEquals(3, tokens.size());
        assertEquals("<3, DECIMAL>", tokens.get(0).toString());
        assertEquals("<+, PLUS>", tokens.get(1).toString());
        assertEquals("<3, DECIMAL>", tokens.get(2).toString());
    }
    
    /*
     * Increment
     */
    @Test
    public void testIncrement() throws Exception {
        String in = "++";
        List<Token> tokens = scannerTest.inputSetUp(in);
        scannerTest.printTokens(tokens);
        assertEquals(1, tokens.size());
        assertEquals("<++, INCREMENT>", tokens.get(0).toString());
    }

    /*
     * Slash
     */
    
    @Test
    public void testSlash() {
        String in = "/";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals(TokenType.SLASH, tokens.get(0).getTokenType());
    }

    @Test
    public void testSlashEq() {
        String in = "/=";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals(TokenType.SLASH_EQ, tokens.get(0).getTokenType());
    }

    @Test
    public void testSlashSlash() {
        String in = "/ /";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(2, tokens.size());
        assertEquals(TokenType.SLASH, tokens.get(0).getTokenType());
    }
}
