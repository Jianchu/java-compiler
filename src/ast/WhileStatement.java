package ast;

import parser.ParseTree;
import exceptions.ASTException;

public class WhileStatement extends Statement{

    public Expression whileCondition;
    public Statement whileStatement;

    public WhileStatement(ParseTree whileNode) throws ASTException {
        for (ParseTree child : whileNode.getChildren()) {
            switch (child.getTokenType()) {
            case Expression:
                this.whileCondition = Expression.parseExpression(child);
                break;
            case Statement:
            case StatementNoShortIf:
                this.whileStatement = Statement.parseStatement(child);
                break;
            default:
                break;
            }
        }
    }

    // public Expression getWhileCondition() {
    // return this.whileCondition;
    // }
    //
    // public Statement getWhileStatement() {
    // return this.whileStatement;
    // }
    public void accept(Visitor v) throws Exception {
        v.visit(this);
    }
}
