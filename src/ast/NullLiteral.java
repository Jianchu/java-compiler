package ast;

import exceptions.ASTException;

/**
 * for now empty class is enough.
 * @author zanel
 *
 */
public class NullLiteral extends Expression {
	
	public NullLiteral() {
		
	}
	
	public void accept(Visitor v) throws ASTException {
		v.visit(this);
	}
}
