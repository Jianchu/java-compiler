package scannertest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import scanner.Token;

public class TestCombo {
    ScannerTest scannerTest;

    @Before
    public void setUp() throws Exception {
        scannerTest = new ScannerTest();
    }

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
        String in = "?:~??::~";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(8, tokens.size());
        assertEquals("<?, QUESTION>", tokens.get(0).toString());
        assertEquals("<:, COLON>", tokens.get(1).toString());
        assertEquals("<~, BIT_COMP>", tokens.get(2).toString());
        assertEquals("<?, QUESTION>", tokens.get(3).toString());
        assertEquals("<?, QUESTION>", tokens.get(4).toString());
        assertEquals("<:, COLON>", tokens.get(5).toString());
        assertEquals("<:, COLON>", tokens.get(6).toString());
        assertEquals("<~, BIT_COMP>", tokens.get(7).toString());
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

}
