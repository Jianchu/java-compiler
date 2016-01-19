package scannertest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import scanner.Token;
import scanner.TokenType;

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
        assertEquals(TokenType.ID, tokens.get(0).getTokenType());
    }

    @Test
    public void testIdWithNumerals() {
        String in = "theAnswer42";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals(in, tokens.get(0).getLexeme());
        assertEquals(TokenType.ID, tokens.get(0).getTokenType());
    }

    @Test
    public void testIdWithUnderscoreDollar() {
        String in = "SOME_VARIABLE$1";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals(in, tokens.get(0).getLexeme());
        assertEquals(TokenType.ID, tokens.get(0).getTokenType());
    }

    @Test
    public void testIdsWhitespaceDelimited() {
        String in = "id0 id1\tid2\rid3\nid4\r\nid5";
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(6, tokens.size());
        for (int i = 0; i < tokens.size(); i++) {
            assertEquals("id" + i, tokens.get(i).getLexeme());
            assertEquals(TokenType.ID, tokens.get(i).getTokenType());
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
        TokenType[] keywords = {
            TokenType.ABSTRACT, TokenType.BOOLEAN, TokenType.BREAK, TokenType.BYTE, TokenType.CASE, TokenType.CATCH,
            TokenType.CHAR, TokenType.CLASS, TokenType.CONST, TokenType.CONTINUE, TokenType.DEFAULT, TokenType.DO,
            TokenType.DOUBLE, TokenType.ELSE, TokenType.EXTENDS, TokenType.FINAL, TokenType.FINALLY, TokenType.FLOAT,
            TokenType.FOR, TokenType.GOTO, TokenType.IF, TokenType.IMPLEMENTS, TokenType.IMPORT, TokenType.INSTANCEOF,
            TokenType.INT, TokenType.INTERFACE, TokenType.LONG, TokenType.NATIVE, TokenType.NEW, TokenType.PACKAGE,
            TokenType.PRIVATE, TokenType.PROTECTED, TokenType.PUBLIC, TokenType.RETURN, TokenType.SHORT, TokenType.STATIC,
            TokenType.STRICTFP, TokenType.SUPER, TokenType.SWITCH, TokenType.SYNCHRONIZED, TokenType.THIS, TokenType.THROW,
            TokenType.THROWS, TokenType.TRANSIENT, TokenType.TRY, TokenType.VOID, TokenType.VOLATILE, TokenType.WHILE
 };
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keywords.length; i++) {
            sb.append(keywords[i].toString()).append(" ");
        }
        String in = sb.toString();
        in.toLowerCase();
        List<Token> tokens = scannerTest.inputSetUp(in);
        assertEquals(keywords.length, tokens.size());
        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(keywords[i], tokens.get(i).getTokenType());
        }
    }

}
