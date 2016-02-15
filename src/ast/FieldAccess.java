package ast;

import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;

public class FieldAccess extends Expression {
    public Expression expr;
    public String id;

    public FieldAccess(ParseTree pt) throws ASTException {
        List<ParseTree> subtrees = pt.getChildren();
        expr = Expression.parseExpression(subtrees.get(0));
        id = ASTHelper.parseID(subtrees.get(2));
    }
    
	public void accept(Visitor v) throws ASTException {
		v.visit(this);
	}
}
