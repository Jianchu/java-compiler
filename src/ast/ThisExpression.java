package ast;

import exceptions.ASTException;

public class ThisExpression extends Expression {
	
	public ThisExpression() {
		
	}
	
	public void accept(Visitor v) throws ASTException {
		v.visit(this);
	}
}
