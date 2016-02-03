package ast;

import exceptions.ASTException;
import parser.ParseTree;

public class BooleanLiteral extends Expression {
	boolean value;
	
	public BooleanLiteral(ParseTree pt) throws ASTException {
		switch(pt.getFirstChild().getTokenType()) {
		case TRUE:
			value = true;
		case FALSE:
			value = false;
		default:
			throw new ASTException();
		}
	}
	
}
