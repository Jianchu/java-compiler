package parser;

import java.io.File;
import java.util.List;

import scanner.Token;
import scanner.Symbol;

public class Parser {
	List<Token> tokens;
	ParseActions[] parseTable;
	List<List<Symbol>> productionRules;
	
	public Parser(List<Token> tokenList) throws Exception {
		tokens = tokenList;
		// grammar file. point to data/test.lr1 for now.
		File parseIn = new File(System.getProperty("user.dir") + "/data/test.lr1");
		ParseTableReader ptr = new ParseTableReader(parseIn);
		parseTable = ptr.getParseActions();
		productionRules = ptr.getProductionRules();
		
	}
	
	public void parse() {
		
	}
}
