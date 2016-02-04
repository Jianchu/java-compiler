package ast;

import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;

public class MethodInvocation extends Expression {
    Expression expr;
    String id;
    List<Expression> arglist;

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
            id = ASTBuilder.parseID(subtrees.get(2));
            arglist = null;
            if (subtrees.size() == 6) {
                arglist = Expression.parseArglist(subtrees.get(4));
            }
        }
    }
}
