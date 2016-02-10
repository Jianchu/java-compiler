package ast;

import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;

public class FieldAccess extends Expression {
    Expression expr;
    String id;

    public FieldAccess(ParseTree pt) throws ASTException {
        List<ParseTree> subtrees = pt.getChildren();
        expr = Expression.parseExpression(subtrees.get(0));
        id = ASTHelper.parseID(subtrees.get(2));
    }
}
