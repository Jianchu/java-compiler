package ast;

import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;

public class AssignmentExpression extends Expression {
    public Expression lhs;
    public Expression expr;

    public AssignmentExpression(ParseTree pt) throws ASTException {
        List<ParseTree> subtrees = pt.getChildren();
        lhs = Expression.parseExpression(subtrees.get(0));
        expr = Expression.parseExpression(subtrees.get(2));
    }
    
	public void accept(Visitor v) throws ASTException {
		v.visit(this);
	}
}
