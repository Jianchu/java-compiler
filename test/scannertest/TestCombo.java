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
    public void testNumplusNum() throws Exception {
        String in = "3 + 3";
        List<Token> tokens = scannerTest.inputSetUp(in);
        // scannerTest.printTokens(tokens);
        assertEquals(3, tokens.size());
        assertEquals("<3, DECIMAL>", tokens.get(0).toString());
        assertEquals("<+, PLUS>", tokens.get(1).toString());
        assertEquals("<3, DECIMAL>", tokens.get(2).toString());
    }

    @Test
    public void testVaildProgram() throws Exception {
        String in = "public class A {\n public A() {}\n public int m1() {\n return 42;\n} public int m2() { \n return m1(); \n }\n}";
        String in1 = "public class A { \npublic A() {}\npublic int m() {\nint[] x = new int[42];\nreturn x.length;\n}\n}";
        String in2 = "public class A {public A() {}public boolean m(boolean x) {return (x & true) | !x;}}";
        List<Token> tokens = scannerTest.inputSetUp(in1);
        scannerTest.printTokens(tokens);
    }
    
    @Test
    public void t() throws Exception {
        String in = ";";
        List<Token> tokens = scannerTest.inputSetUp(in);
        scannerTest.printTokens(tokens);
    }

}
