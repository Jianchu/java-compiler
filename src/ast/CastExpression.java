package ast;

import java.util.List;

import exceptions.ASTException;
import scanner.Symbol;
import parser.ParseTree;

public class CastExpression extends Expression {
    Type type;
    Expression expr;
    Expression unary;
    boolean isArray;

    public CastExpression(ParseTree pt) throws ASTException {
        List<ParseTree> subtrees = pt.getChildren();
        if (subtrees.get(1).getTokenType() == Symbol.PrimitiveType) {
            type = Type.parseType(subtrees.get(1));
        } else {
            expr = Expression.parseExpression(subtrees.get(1));
        }
        if (subtrees.size() == 5) {
            isArray = true;
            unary = Expression.parseExpression(subtrees.get(4));
        } else {
            isArray = false;
            unary = Expression.parseExpression(subtrees.get(3));
        }
    }
    
	public void accept(Visitor v) throws ASTException {
		v.visit(this);
	}
}
