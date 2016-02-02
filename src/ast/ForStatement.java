package ast;

import parser.ParseTree;
import exceptions.ASTException;

public class ForStatement extends Statement{

    public Expression forInit;
    public Expression forCondition;
    public Expression forUpdate;
    public Statement forBody;

    public ForStatement(ParseTree forNode) throws ASTException {
        for (ParseTree child : forNode.getChildren()) {
            switch (child.getTokenType()) {
            case ForInit:
                break;
            case Expression:
                // Expression has not been implemented
                // this.forCondition = Expression.(child);
                break;
            case ForUpdate:
                break;
            case Statement:
            case StatementNoShortIf:
                forBody = ASTBuilder.parseStatement(child);
                break;
            default:
                throw new ASTException("Unexpected symbol");
            }
        }
    }

    public Expression getForCondition() {
        return this.forCondition;
    }

    public Expression getForUpdate() {
        return this.forUpdate;
    }

    public Statement getForBody() {
        return this.forBody;
    }

}
