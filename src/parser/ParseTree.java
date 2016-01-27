package parser;

import java.util.LinkedList;
import java.util.List;

import scanner.Symbol;
import scanner.Token;

/**
 * Extends Token class so that in reduce phase it is possible to prepend
 * the result of reduction back to the Token list.
 * @author zanel
 *
 */
public class ParseTree extends Token{
	
	List<ParseTree> children;
	
	public ParseTree(Symbol s) {
		super(null, s);
		children = new LinkedList<ParseTree>();
	}
	
	public ParseTree(Token t) {
		super(t.getLexeme(), t.getTokenType());
		children = new LinkedList<ParseTree>();
	}
	
	public void addChild(ParseTree subTree) {
		children.add(subTree);
	}
	
	public void addChildren(List<ParseTree> subTrees) {
		children.addAll(subTrees);
	}
	
	public List<ParseTree> getChildren() {
		return children;
	}
	
	public void pprint() {
		pprint(0);
	}
	
	public void pprint(int indent) {
		int distance = 15;
		for (int i = 0; i < indent; i++) {
			System.out.print(" ");
		}
		System.out.println(this.getTokenType());
		for (ParseTree child : children) {
			child.pprint(indent + distance);
		}
	}
}
