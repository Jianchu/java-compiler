package ast;

import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;

public class MethodInvocation extends Expression {
    public Expression expr;
    public String id;
    public List<Expression> arglist;

    public MethodInvocation(ParseTree pt) throws ASTException {
        List<ParseTree> subtrees = pt.getChildren();
        expr = Expression.parseExpression(subtrees.get(0));
        if (subtrees.size() < 5) {
            id = null;
            arglist = null;
            if (subtrees.size() == 4) {
                arglist = Expression.parseArglist(subtrees.get(2));
            }
        } else {
            id = ASTHelper.parseID(subtrees.get(2));
            arglist = null;
            if (subtrees.size() == 6) {
                arglist = Expression.parseArglist(subtrees.get(4));
            }
        }
    }
    
	public void accept(Visitor v) throws Exception {
		v.visit(this);
	}
}
