package ast;

import java.util.List;

import parser.ParseTree;
import exceptions.ASTException;

public class PrefixExpression extends Expression {
    public Operator op;
    public Expression expr;

    public enum Operator { MINUS, NOT }

    public PrefixExpression(ParseTree pt) throws ASTException {
        List<ParseTree> subtrees = pt.getChildren();
        switch (subtrees.get(0).getTokenType()) {
          case MINUS:  op = Operator.MINUS;  break;
          case NOT:    op = Operator.NOT;    break;
          default: throw new ASTException("Unexpected node type " + subtrees.get(0).getTokenType());
        }
        expr = Expression.parseExpression(subtrees.get(1));
    }
    
    public void accept(Visitor v) throws Exception {
        v.visit(this);
    }
}
