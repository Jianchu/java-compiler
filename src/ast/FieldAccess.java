package ast;

import java.util.List;

import parser.ParseTree;
import exceptions.ASTException;

public class FieldAccess extends Expression {
    public Expression expr;
    public SimpleName id;

    public FieldAccess(ParseTree pt) throws ASTException {
        List<ParseTree> subtrees = pt.getChildren();
        expr = Expression.parseExpression(subtrees.get(0));
        id = new SimpleName(ASTHelper.parseID(subtrees.get(2)));
    }
    
    public void accept(Visitor v) throws Exception {
        v.visit(this);
    }
}
