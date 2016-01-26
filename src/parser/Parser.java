package parser;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import scanner.Token;
import scanner.Scanner;
import scanner.Symbol;

public class Parser {
	List<Token> tokens;
	ParseActions[] parseTable;
	List<List<Symbol>> productionRules;
	
	final int START = 0, HEAD = 0;
	
	public Parser(List<Token> tokenList) throws Exception {
		tokens = tokenList;
		// grammar file. point to data/test.lr1 for now.
		File parseIn = new File(System.getProperty("user.dir") + "/data/test.lr1");
		ParseTableReader ptr = new ParseTableReader(parseIn);
		parseTable = ptr.getParseActions();
		productionRules = ptr.getProductionRules();
		
	}
	
	public ParseTree parse() throws Exception {
		Stack<ParseTree> nodeStack = new Stack<ParseTree>();
		Stack<Integer> stateStack = new Stack<Integer>();
		
		Token bof = tokens.remove(HEAD);
		Action action = parseTable[START].getAction(bof.getTokenType());
		if (action.getShiftReduce() == ShiftReduce.SHIFT) {
			stateStack.push(action.getNum());
			nodeStack.push(new ParseTree(bof));
		} else {
			throw new Exception("BOF error.");
		}
		
		while (tokens.size() > 0) {
			Token a = tokens.get(HEAD);
			action = parseTable[stateStack.peek()].getAction(a.getTokenType());
			if (action.getShiftReduce() == ShiftReduce.SHIFT) {
				// shift
				stateStack.push(action.getNum());
				if (a instanceof ParseTree) {
					// if a is already a ParseTree, just push
					nodeStack.push((ParseTree) a);
				} else {
					nodeStack.push(new ParseTree(a));
				}
				tokens.remove(HEAD);	// remove a from tokens
			} else {
				// reduce
				List<Symbol> rule = productionRules.get(action.getNum());
				ParseTree node = new ParseTree(rule.get(0));
				
				for (int i = 0; i < rule.size()-1; i++) {
					stateStack.pop();
					node.addChild(nodeStack.pop());
				}
				tokens.add(0, node);
			}
		}
		
		return nodeStack.pop(); 
	}
	
	/**
	 * For testing purpose only.
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner scanner = new Scanner(new StringReader("i = 1"));
		List<Token> tokens = scanner.scan();
		// adding BOF and EOF by hand for experiment
		tokens.add(0, new Token("", Symbol.BOF));
		tokens.add(new Token("", Symbol.EOF));
		try {
			Parser parser = new Parser(tokens);
			ParseTree t = parser.parse();
	
		} catch (Exception e) {
			System.exit(1);
		}
		
	}
	

}
