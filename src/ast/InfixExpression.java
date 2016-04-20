package ast;

import java.util.List;

import parser.ParseTree;
import exceptions.ASTException;

public class InfixExpression extends Expression {
    public Expression lhs;
    public Operator op;
    public Expression rhs;

    public enum Operator {
        LOR, AND, BITOR, BITAND,
        NEQ, EQUAL, LANGLE, RANGLE, GEQ, LEQ,
        PLUS, MINUS, STAR, SLASH, MOD,
    }

    public InfixExpression(ParseTree pt) throws ASTException {
        List<ParseTree> subtrees = pt.getChildren();
        switch (subtrees.get(1).getTokenType()) {
          case LOR:     op = Operator.LOR;     break;
          case AND:     op = Operator.AND;     break;
          case BITOR:   op = Operator.BITOR;   break;
          case BITAND:  op = Operator.BITAND;  break;
          case NEQ:     op = Operator.NEQ;     break;
          case EQUAL:   op = Operator.EQUAL;   break;
          case LANGLE:  op = Operator.LANGLE;  break;
          case RANGLE:  op = Operator.RANGLE;  break;
          case GEQ:     op = Operator.GEQ;     break;
          case LEQ:     op = Operator.LEQ;     break;
          case PLUS:    op = Operator.PLUS;    break;
          case MINUS:   op = Operator.MINUS;   break;
          case STAR:    op = Operator.STAR;    break;
          case SLASH:   op = Operator.SLASH;   break;
          case MOD:     op = Operator.MOD;     break;
          default: throw new ASTException("Unexpected node type " + subtrees.get(1).getTokenType());
        }
        lhs = Expression.parseExpression(subtrees.get(0));
        rhs = Expression.parseExpression(subtrees.get(2));
    }

    public void accept(Visitor v) throws Exception {
        v.visit(this);
    }
}
