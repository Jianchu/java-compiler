import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


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
    public void testURSHIFT_EQ() throws Exception {
        String in = ">>>=";
        List<Token> tokens = inputSetUp(in);
        printTokens(tokens);
        assertEquals(1, tokens.size());
        assertEquals("<>>>=, URSHIFT_EQ>", tokens.get(0).toString());
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
