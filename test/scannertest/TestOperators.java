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

    // /*
    // * Operators
    // */
    // // =, ==
    // ASSIGN, EQUAL,
    // // >, >>, >>>, >=, >>=, >>>= (unsigned right shift assignment)
    // RANGLE, DBRANGLE, TPRANGLE, GEQ, RSHIFT_EQ, URSHIFT_EQ,
    // // <, <<, <=, <<=
    // LANGLE, DBLANGLE, LEQ, LSHIFT_EQ,
    // // ~
    // BIT_COMP,
    // // !, !=
    // NOT, NEQ,
    // // ?, :
    // QUESTION, COLON,
    // // &, &&, &=
    // BITAND, AND, AND_EQ,
    // // |, ||, |=
    // BITOR, LOR, OR_EQ,
    // // ^, ^=
    // EXOR, EXOR_EQ,
    // // +, ++, -, --, +=, -=
    // PLUS, INCREMENT, MINUS, DECREMENT, PLUS_EQ, MINUS_EQ,
    // // *, *=, /, /=,
    // STAR, STAR_EQ, SLASH, SLASH_EQ,
    // // %, %=
    // MOD, MOD_EQ,
    
    @Test
    public void testLangle() throws Exception {
        String in = "<<<<=<<=<=<<<";
        List<Token> tokens = scannerTest.inputSetUp(in);
        scannerTest.printTokens(tokens);
        assertEquals(6, tokens.size());
        assertEquals("<<<, DBLANGLE>", tokens.get(0).toString());
        assertEquals("<<<=, LSHIFT_EQ>", tokens.get(1).toString());
        assertEquals("<<<=, LSHIFT_EQ>", tokens.get(2).toString());
        assertEquals("<<=, LEQ>", tokens.get(3).toString());
        assertEquals("<<<, DBLANGLE>", tokens.get(4).toString());
        assertEquals("<<, LANGLE>", tokens.get(5).toString());
    }

    @Test
    public void testQuestionColon() throws Exception {
        String in = "?:??::";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(6, tokens.size());
        assertEquals("<?, QUESTION>", tokens.get(0).toString());
        assertEquals("<:, COLON>", tokens.get(1).toString());
        assertEquals("<?, QUESTION>", tokens.get(2).toString());
        assertEquals("<?, QUESTION>", tokens.get(3).toString());
        assertEquals("<:, COLON>", tokens.get(4).toString());
        assertEquals("<:, COLON>", tokens.get(5).toString());
    }

    /*
     * not
     */
    @Test
    public void testNot() throws Exception {
        String in = "!=!=!";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(3, tokens.size());
        assertEquals("<!=, NEQ>", tokens.get(0).toString());
        assertEquals("<!=, NEQ>", tokens.get(1).toString());
        assertEquals("<!, NOT>", tokens.get(2).toString());
    }

    /*
     * Equals
     */
    @Test
    public void testEqual() throws Exception {
        String in = "=====";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(3, tokens.size());
        assertEquals("<==, EQUAL>", tokens.get(0).toString());
        assertEquals("<==, EQUAL>", tokens.get(1).toString());
        assertEquals("<=, ASSIGN>", tokens.get(2).toString());
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
        // scannerTest.printTokens(tokens);
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
