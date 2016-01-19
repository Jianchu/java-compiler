package scannertest;
import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import scanner.Scanner;
import scanner.Token;
import exceptions.IllegalCharException;
import exceptions.IllegalIDException;


public class ScannerTest {
	
	Scanner scan;
	@Rule public ExpectedException thrown= ExpectedException.none();

	
    @Before
    public void setUp() throws Exception {
    	
    }
    
    /*
     * For integers
     */
    
    @Test
    public void testInt() {
    	String num = "123712";
    	List<Token> tokens = inputSetUp(num);
    	assertEquals(1, tokens.size());
    	assertEquals(num, tokens.get(0).getLexeme());
    }
    
    @Test
    public void testIntSpace() {
    	String num = "123" + " sdf";
    	List<Token> tokens = inputSetUp(num);
    	assertEquals(2, tokens.size());
    	assertEquals("123", tokens.get(0).getLexeme());
    }
    
    @Test 
    public void testIntFail() throws Exception {
    	thrown.expect(IllegalIDException.class);
    	String num = "123c";
    	inputSetUpException(num);
    }
    
    /*
     * Characters
     */
    @Test
    public void testChar() {
    	String in = "\'c\'";
    	List<Token> tokens = inputSetUp(in);
    	assertEquals(1, tokens.size());
    	assertEquals(in, tokens.get(0).getLexeme());
    }
    
    @Test
    public void testCharEscape() {
    	String in = "\'\\b\'";
    	List<Token> tokens = inputSetUp(in);
    	assertEquals(1, tokens.size());
    	assertEquals(in, tokens.get(0).getLexeme());
//    	printTokens(tokens);
    }
    
    @Test
    public void testCharFail() throws Exception {
    	String in = "\'sa\'";
    	thrown.expect(IllegalCharException.class);
    	List<Token> tokens = inputSetUpException(in);
    }
    
    @Test
    public void testCharEscapeFail() throws Exception {
    	String in = "\'\\a\'";
    	thrown.expect(IllegalCharException.class);
    	List<Token> tokens = inputSetUpException(in);
    }
    
    /* 
     * Strings
     */
    
    @Test
    public void testString() throws Exception {
    	String in = "\"asdf\"";
    	List<Token> tokens = inputSetUp(in);
    	assertEquals(1, tokens.size());
    	assertEquals(in, tokens.get(0).getLexeme());
    }
    
    @Test
    public void testStringEscape() throws Exception {
    	String in = "\"as\tdf\"";
    	List<Token> tokens = inputSetUp(in);
        // printTokens(tokens);
    	assertEquals(3, tokens.size());
    	assertEquals(in, tokens.get(0).getLexeme());
    }
    
    /*
     * Equals
     */
    @Test
    public void testEqual() throws Exception {
        String in = "==";
        List<Token> tokens = inputSetUp(in);
        // printTokens(tokens);
        assertEquals(1, tokens.size());
        assertEquals("<==, EQUAL>", tokens.get(0).toString());
    }
    
    /*
     * URSHIFT_EQ
     */
    @Test
    public void testUrshift_eq() throws Exception {
        String in = ">>>=";
        List<Token> tokens = inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals("<>>>=, URSHIFT_EQ>", tokens.get(0).toString());
    }

    /*
     * Id + Id
     */

    @Test
    public void testIdplusId() throws Exception {
        String in = "3 + 3";
        List<Token> tokens = inputSetUp(in);
        printTokens(tokens);
        assertEquals(3, tokens.size());
        assertEquals("<3, DECIMAL>", tokens.get(0).toString());
        assertEquals("<+, PLUS>", tokens.get(1).toString());
        assertEquals("<3, DECIMAL>", tokens.get(2).toString());
    }

    /*
     * Identifiers
     */

    @Test
    public void testId() {
        String in = "id";
        List<Token> tokens = inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals(in, tokens.get(0).getLexeme());
        assertEquals(TokenType.ID, tokens.get(0).getTokenType());
    }

    @Test
    public void testIdWithNumerals {
        String in = "theAnswer42";
        List<Token> tokens = inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals(in, tokens.get(0).getLexeme());
        assertEquals(TokenType.ID, tokens.get(0).getTokenType());
    }

    @Test
    public void testIdWithUnderscoreDollar {
        String in = "SOME_VARIABLE$1";
        List<Token> tokens = inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals(in, tokens.get(0).getLexeme());
        assertEquals(TokenType.ID, tokens.get(0).getTokenType());
    }

    @Test
    public void testIdsWhitespaceDelimited {
        String in = "id0 id1\tid2\rid3\nid4\r\nid5";
        List<Token> tokens = inputSetUp(in);
        assertEquals(6, tokens.size())
        for (int i = 0; i < tokens.size(); i++) {
            assertEquals("id" + i, tokens.get(i).getLexeme());
            assertEquals(TokenType.ID, tokens.get(i).getTokenType());
        }
    }

    @Test
    public void testIdsOtherDelimited {
        String in = "id1/**/id2/id3"
        List<Token> tokens = inputSetUp(in);
        assertEquals(4, tokens.size());
    }

    /*
     * Keywords
     */

    @Test
    public void testKeywords {
        TokenType[] keywords = {
            TokenType.ABSTRACT, TokenType.BOOLEAN, TokenType.BREAK, TokenType.BYTE, TokenType.CASE, TokenType.CATCH,
            TokenType.CHAR, TokenType.CLASS, TokenType.CONST, TokenType.CONTINUE, TokenType.DEFAULT, TokenType.DO,
            TokenType.DOUBLE, TokenType.ELSE, TokenType.EXTENDS, TokenType.FINAL, TokenType.FINALLY, TokenType.FLOAT,
            TokenType.FOR, TokenType.GOTO, TokenType.IF, TokenType.IMPLEMENTS, TokenType.IMPORT, TokenType.INSTANCEOF,
            TokenType.INT, TokenType.INTERFACE, TokenType.LONG, TokenType.NATIVE, TokenType.NEW, TokenType.PACKAGE,
            TokenType.PRIVATE, TokenType.PROTECTED, TokenType.PUBLIC, TokenType.RETURN, TokenType.SHORT, TokenType.STATIC,
            TokenType.STRICTFP, TokenType.SUPER, TokenType.SWITCH, TokenType.SYNCHRONIZED, TokenType.THIS, TokenType.THROW,
            TokenType.THROWS, TokenType.TRANSIENT, TokenType.TRY, TokenType.VOID, TokenType.VOLATILE, TokenType.WHILE
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keywords.length; i++) {
            sb.append(keywords[i].toString()).append(" ");
        }
        String in = sb.toString();
        in.toLowerCase();
        List<Token> tokens = inputSetUp(in);
        assertEquals(keywords.length, tokens.size());
        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(keywords[i], tokens.get(i).getTokenType());
        }
    }

    @Test
    public void testNull {
        String in = "null";
        List<Token> tokens = inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals(TokenType.NULL, tokens.get(0).getTokenType());
    }

    /*
     * Slash
     */

    @Test
    public void testSlash {
        String in = "/";
        List<Token> tokens = inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals(TokenType.SLASH, tokens.get(0).getTokenType());
    }

    @Test
    public void testSlashEq {
        String in = "/=";
        List<Token> tokens = inputSetUp(in);
        assertEquals(1, tokens.size());
        assertEquals(TokenType.SLASH_EQ, tokens.get(0).getTokenType());
    }

    @Test
    public void testSlashSlash {
        String in = "/ /";
        List<Token> tokens = inputSetUp(in);
        assertEquals(2, tokens.size());
        assertEquals(TokenType.SLASH, tokens.get(0).getTokenType());
    }

    /*
     * Comments
     */

    @Test
    public void testComment {
        String in = "//this is a comment";
        List<Token> tokens = inputSetUp(in);
        assertEquals(0, tokens.size());
    }

    @Test
    public void testIdBeforeComment {
        String in = "notComment //this is a comment";
        List<Token> tokens = inputSetUp(in);
        assertEquals(1, tokens.size());
    }

    @Test
    public void testIdAfterComment {
        String in = "//this is a comment\nbutNotThis";
        List<Token> tokens = inputSetUp(in);
        assertEquals(1, tokens.size());
    }

    @Test
    public void testMultiComment {
        String in = "var1//commentA\nvar2//commentB\rvar3//commentC\r\nvar4";
        List<Token> tokens = inputSetUp(in);
        assertEquals(4, tokens.size());
    }

    @Test
    public void testBlockComment {
        String in = "/* This is a block comment*/";
        List<Token> tokens = inputSetUp(in);
        assertEquals(0, tokens.size());
    }

    @Test
    public void testBlockCommentNewline {
        String in = "/* This is a block comment\n   with a newline in it */";
        List<Token> tokens = inputSetUp(in);
        assertEquals(0, tokens.size());
    }

    @Test
    public void testBlockCommentStar {
        String in = "/* This block comment* has * extra stars in it **/";
        List<Token> tokens = inputSetUp(in);
        assertEquals(0, tokens.size());
    }

    @Test
    public void testCommentedBlockComment {
        String in = "// /*\nnotAComment\n// */";
        List<Token> tokens = inputSetUp(in);
        assertEquals(1, tokens.size());
    }

    /*
     * Basic setup
     */
    
    public List<Token> inputSetUp(String num) {
    	StringReader sr = new StringReader(num);
    	scan = new Scanner(sr);
    	List<Token> tokens = scan.scan();
    	return tokens;
    }
    
    public List<Token> inputSetUpException(String num) throws Exception {
    	StringReader sr = new StringReader(num);
    	scan = new Scanner(sr);
    	List<Token> tokens = scan.scanThrow();
    	return tokens;
    }
    
    public void printTokens(List<Token> tokens) {
    	for (Token t : tokens) {
            System.out.println(t.toString() + "\t");
    	}
    }
}
