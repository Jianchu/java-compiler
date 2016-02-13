package ast;

import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;

public class ArrayAccess extends Expression {
    Expression array;
    Expression index;

    public ArrayAccess(ParseTree pt) throws ASTException {
        List<ParseTree> subtrees = pt.getChildren();
        array = Expression.parseExpression(subtrees.get(0));
        index = Expression.parseExpression(subtrees.get(2));
    }
    
	public void accept(Visitor v) throws ASTException {
		v.visit(this);
	}
}
