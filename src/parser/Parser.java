package parser;

import java.io.File;
import java.util.List;
import java.util.Stack;

import scanner.Token;
import scanner.Symbol;

public class Parser {
	List<Token> tokens;
	ParseActions[] parseTable;
	List<List<Symbol>> productionRules;
	
	final int START = 0, HEAD = 0;
	
	public Parser(List<Token> tokenList, File grammar) throws Exception {
		tokens = tokenList;
		// grammar file. point to data/test.lr1 for now.
		ParseTableReader ptr = new ParseTableReader(grammar);
		parseTable = ptr.getParseActions();
		productionRules = ptr.getProductionRules();
		
		//augment token list
		tokens.add(0, new Token("", Symbol.BOF));
		tokens.add(new Token("", Symbol.EOF));
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
			
			try {
				action = parseTable[stateStack.peek()].getAction(a.getTokenType());
			} catch (Exception e) {
//				System.err.println(a);
//				System.err.println("Stack:");
//				while (!nodeStack.isEmpty()) {
//					nodeStack.pop().pprint(5);
//				}
				throw e;
			}
			/* for debugging only.
			*/
			
//			action = parseTable[stateStack.peek()].getAction(a.getTokenType());
			
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
					node.addChildToHead(nodeStack.pop());
				}
				tokens.add(0, node);
			}
		}
		
		nodeStack.pop();
		return nodeStack.pop(); 
	}
}
