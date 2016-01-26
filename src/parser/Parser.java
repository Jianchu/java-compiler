package parser;

import java.io.File;
import java.util.List;

import scanner.Token;

public class Parser {
	List<Token> tokens;
	
	
	public Parser(List<Token> tokenList) throws Exception {
		tokens = tokenList;
		// grammar file. point to test.lr1 for now.
		File parseIn = new File(System.getProperty("user.dir") + "/data/test.lr1");
		ParseTableReader ptr = new ParseTableReader(parseIn);
		
		
		
	}
	
	public void parse() {
		
	}
}
