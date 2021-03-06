package ast;

import java.util.List;

import parser.ParseTree;
import exceptions.ASTException;

public class MethodInvocation extends Expression {
    public Expression expr;
    public SimpleName id;
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
            id = new SimpleName(ASTHelper.parseID(subtrees.get(2)));
            arglist = null;
            if (subtrees.size() == 6) {
                arglist = Expression.parseArglist(subtrees.get(4));
            }
        }
    }

    public MethodInvocation(Expression expr, SimpleName id, List<Expression> arglist ) {
        this.expr = expr;
        this.id = id;
        this.arglist = arglist;
    }

    public void accept(Visitor v) throws Exception {
        v.visit(this);
    }
}
