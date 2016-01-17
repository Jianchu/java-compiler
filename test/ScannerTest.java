import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.io.Reader;
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
    		System.out.println(t.getLexeme() + "\t");
    	}
    }
}
