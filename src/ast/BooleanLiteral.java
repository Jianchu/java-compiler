package ast;

import exceptions.ASTException;
import parser.ParseTree;

public class BooleanLiteral extends Expression {
	public boolean value;
	
	public BooleanLiteral(ParseTree pt) throws ASTException {
		switch(pt.getFirstChild().getTokenType()) {
		case TRUE:
			value = true;
			break;
		case FALSE:
			value = false;
			break;
		default:
			throw new ASTException();
		}
	}
	
	public void accept(Visitor v) throws Exception {
		v.visit(this);
	}
	
}
