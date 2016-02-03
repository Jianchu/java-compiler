package ast;

import parser.ParseTree;
import exceptions.ASTException;

public class WhileStatement extends Statement{

    private Expression whileCondition;
    private Statement whileStatement;

    public WhileStatement(ParseTree whileNode) throws ASTException {
        for (ParseTree child : whileNode.getChildren()) {
            switch (child.getTokenType()) {
            case Expression:
                this.whileCondition = ASTBuilder.parseExpression(child);
                break;
            case Statement:
            case StatementNoShortIf:
                this.whileStatement = ASTBuilder.parseStatement(child);
                break;
            default:
                throw new ASTException("Unexpected symbol");
            }
        }
    }

    public Expression getWhileCondition() {
        return this.whileCondition;
    }

    public Statement getWhileStatement() {
        return this.whileStatement;
    }
}
