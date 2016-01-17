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
    	List<Token> tokens = intSetUp(num);
    	assertEquals(1, tokens.size());
    	assertEquals(num, tokens.get(0).getLexeme());
    }
    
    @Test
    public void testIntSpace() {
    	String num = "123" + " sdf";
    	List<Token> tokens = intSetUp(num);
    	assertEquals(2, tokens.size());
    	assertEquals("123", tokens.get(0).getLexeme());
    }
    
    @Test 
    public void testIntFail() {
//    	thrown.expect(IllegalIDException.class);
    	String num = "123c";
    	intSetUp(num);
    }

    public List<Token> intSetUp(String num) {
    	StringReader sr = new StringReader(num);
    	scan = new Scanner(sr);
    	List<Token> tokens = scan.scan();
    	return tokens;
    }
}
