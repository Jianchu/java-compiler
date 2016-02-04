package ast;

import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;

public class InstanceofExpression extends Expression {
    Expression expr;
    Type type;

    public InstanceofExpression(ParseTree pt) throws ASTException{
        List<ParseTree> subtrees = pt.getChildren();
        expr = Expression.parseExpression(subtrees.get(0));
        type = Type.parseType(subtrees.get(2));
    }
}
