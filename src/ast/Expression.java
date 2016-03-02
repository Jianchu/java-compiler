package ast;

import java.util.LinkedList;
import java.util.List;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.ASTException;

public abstract class Expression extends ASTNode{

    Type type;
    public static Expression parseExpression(ParseTree pt) throws ASTException {
        List<ParseTree> ptChildren = null;
        for ( ; ; ) {
            switch (pt.getTokenType()) {
              case StatementExpression: //fall through
              case Expression: //fall through
              case AssignmentExpression: //fall through
              case ConditionalExpression: //fall through
              case LeftHandSide: //fall through
              case PostfixExpression: //fall through
              case Primary:
                pt = pt.getChildren().get(0);
                break;
              case ConditionalOrExpression: //fall through
              case ConditionalAndExpression: //fall through
              case InclusiveOrExpression: //fall through
              case ExclusiveOrExpression: //fall through
              case AndExpression: //fall through
              case EqualityExpression: //fall through
              case ShiftExpression: //fall through
              case AdditiveExpression: //fall through
              case MultiplicativeExpression: 
                ptChildren = pt.getChildren();
                if (ptChildren.size() > 1) {
                    return new InfixExpression(pt);
                }

                pt = ptChildren.get(0);
                break;
              case RelationalExpression:
                ptChildren = pt.getChildren();
                if (ptChildren.size() > 1) {
                    if (ptChildren.get(1).getTokenType() == Symbol.INSTANCEOF) {
                        return new InstanceofExpression(pt);
                    }
                    return new InfixExpression(pt);
                }

                pt = ptChildren.get(0);
                break;
              case UnaryExpression: //fall through
              case UnaryExpressionNotPlusMinus:
                ptChildren = pt.getChildren();
                if (ptChildren.size() > 1) {
                    return new PrefixExpression(pt);
                }

                pt = ptChildren.get(0);
                break;
              case PrimaryNoNewArray:
                ptChildren = pt.getChildren();
                if (ptChildren.get(0).getTokenType() == Symbol.THIS) {
                    return new ThisExpression();
                }

                pt = ptChildren.get(0);
                if (ptChildren.size() > 1) { // LPAREN expr RPAREN
                    pt = ptChildren.get(1);
                }
                break;
              case Literal:
                ParseTree ptChild = pt.getChildren().get(0);
                switch (ptChild.getTokenType()) {
                  case BooleanLiteral:
                    return new BooleanLiteral(ptChild);
                  case CHARACTER:
                    return new CharacterLiteral(ptChild);
                  case DECIMAL:
                    return new IntegerLiteral(ptChild);
                  case STRING:
                    return new StringLiteral(ptChild);
                  case NULL:
                    return new NullLiteral();
                  default:
                    throw new ASTException("Unexpected Literal derivation");
                }
              case ClassInstanceCreationExpression:
                return new ClassInstanceCreationExpression(pt);
              case FieldAccess:
                return new FieldAccess(pt);
              case MethodInvocation:
                return new MethodInvocation(pt);
              case ArrayAccess:
                return new ArrayAccess(pt);
              case ArrayCreationExpression:
                return new ArrayCreationExpression(pt);
              case CastExpression:
                return new CastExpression(pt);
              case Assignment:
                return new AssignmentExpression(pt);
              case Name:
                return Name.parseName(pt);
              default:
                throw new ASTException("Unexpected node type " + pt.getTokenType());
            }
        }
    }

    public static List<Expression> parseArglist(ParseTree pt) throws ASTException {
        List<Expression> arglist = new LinkedList<>();
        for ( ; ; ) {
            List<ParseTree> ptChildren = pt.getChildren();
            if (ptChildren.size() == 1) {
                arglist.add(0, parseExpression(ptChildren.get(0)));
                return arglist;
            }
            arglist.add(0, parseExpression(ptChildren.get(2)));
            pt = ptChildren.get(0);
        }
    }

    public void attachType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
