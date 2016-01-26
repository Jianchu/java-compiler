package scannertest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import scanner.Token;
import scanner.Symbol;

public class TestIdentifier {

    @Before
    public void setUp() throws Exception {
    }

    /*
     * Identifiers
     */
    ScannerTest scannerTest = new ScannerTest();
    @Test
    public void testId() {
        String in = "id";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals(in, tokens.get(0).getLexeme());
        assertEquals(Symbol.ID, tokens.get(0).getTokenType());
    }

    @Test
    public void testIdWithNumerals() {
        String in = "theAnswer42";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals(in, tokens.get(0).getLexeme());
        assertEquals(Symbol.ID, tokens.get(0).getTokenType());
    }

    @Test
    public void testIdWithUnderscoreDollar() {
        String in = "SOME_VARIABLE$1";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals(in, tokens.get(0).getLexeme());
        assertEquals(Symbol.ID, tokens.get(0).getTokenType());
    }

    @Test
    public void testIdsWhitespaceDelimited() {
        String in = "id0 id1\tid2\rid3\nid4\r\nid5";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(6, tokens.size());
        for (int i = 0; i < tokens.size(); i++) {
            assertEquals("id" + i, tokens.get(i).getLexeme());
            assertEquals(Symbol.ID, tokens.get(i).getTokenType());
        }
    }

    @Test
    public void testIdsOtherDelimited() {
        String in = "id1/**/id2/id3";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(4, tokens.size());
    }
    
    /*
     * Keywords
     */

    @Test
    public void testKeywords() {
        Symbol[] keywords = {
            Symbol.ABSTRACT, Symbol.BOOLEAN, Symbol.BREAK, Symbol.BYTE, Symbol.CASE, Symbol.CATCH,
            Symbol.CHAR, Symbol.CLASS, Symbol.CONST, Symbol.CONTINUE, Symbol.DEFAULT, Symbol.DO,
            Symbol.DOUBLE, Symbol.ELSE, Symbol.EXTENDS, Symbol.FINAL, Symbol.FINALLY, Symbol.FLOAT,
            Symbol.FOR, Symbol.GOTO, Symbol.IF, Symbol.IMPLEMENTS, Symbol.IMPORT, Symbol.INSTANCEOF,
            Symbol.INT, Symbol.INTERFACE, Symbol.LONG, Symbol.NATIVE, Symbol.NEW, Symbol.PACKAGE,
            Symbol.PRIVATE, Symbol.PROTECTED, Symbol.PUBLIC, Symbol.RETURN, Symbol.SHORT, Symbol.STATIC,
            Symbol.STRICTFP, Symbol.SUPER, Symbol.SWITCH, Symbol.SYNCHRONIZED, Symbol.THIS, Symbol.THROW,
            Symbol.THROWS, Symbol.TRANSIENT, Symbol.TRY, Symbol.VOID, Symbol.VOLATILE, Symbol.WHILE
 };
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keywords.length; i++) {
            sb.append(keywords[i].toString()).append(" ");
        }
        String in = sb.toString();
        in = in.toLowerCase();
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(keywords.length, tokens.size());
        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(keywords[i], tokens.get(i).getTokenType());
        }
    }

}
