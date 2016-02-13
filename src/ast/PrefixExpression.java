package ast;

import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;

public class PrefixExpression extends Expression {
    // op must be NOT
    Expression expr;

    public PrefixExpression(ParseTree pt) throws ASTException {
        List<ParseTree> subtrees = pt.getChildren();
        expr = Expression.parseExpression(subtrees.get(0));
    }
    
	public void accept(Visitor v) throws ASTException {
		v.visit(this);
	}
}
