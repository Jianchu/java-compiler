package ast;

import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;

public class ArrayCreationExpression extends Expression {
    Type type;
    Expression expr;

    public ArrayCreationExpression(ParseTree pt) throws ASTException {
        List<ParseTree> subtrees = pt.getChildren();
        type = Type.parseType(subtrees.get(1));
        expr = Expression.parseExpression(subtrees.get(2).getChildren().get(1));
    }
}
