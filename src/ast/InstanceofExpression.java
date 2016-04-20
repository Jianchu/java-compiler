package ast;

import java.util.List;

import parser.ParseTree;
import exceptions.ASTException;

public class InstanceofExpression extends Expression {
    public Expression expr;
    public Type type;

    public InstanceofExpression(ParseTree pt) throws ASTException{
        List<ParseTree> subtrees = pt.getChildren();
        expr = Expression.parseExpression(subtrees.get(0));
        type = Type.parseType(subtrees.get(2));
    }
    
    public void accept(Visitor v) throws Exception {
        v.visit(this);
    }
}
