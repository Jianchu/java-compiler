package ast;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.ASTException;

public class ReturnStatement extends Statement {

    public Expression returnExpression = null;

    public ReturnStatement(ParseTree returnNode) throws ASTException {
        ParseTree expressionNode = returnNode.findChild(Symbol.Expression);
        // don't check null intentionally
        this.returnExpression = Expression.parseExpression(expressionNode);
    }
    
	public void accept(Visitor v) throws Exception {
		v.visit(this);
	}
}
