package scannertest;
import java.io.StringReader;
import java.util.List;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import scanner.Scanner;
import scanner.Token;


public class ScannerTest {
	
	Scanner scan;
	@Rule public ExpectedException thrown= ExpectedException.none();

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
