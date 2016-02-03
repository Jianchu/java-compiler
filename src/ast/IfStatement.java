package ast;

import parser.ParseTree;

public class IfStatement extends Statement{

    private Expression ifCondition;
    private Statement ifStatement;
    private Statement elseStatement;

    public IfStatement(ParseTree ifNode) {
        for (ParseTree child : ifNode.getChildren()) {
            switch (child.getTokenType()) {
            case Expression:
                this.ifCondition = ASTBuilder.parseExpression(child);
                break;
            case StatementNoShortIf:
                break;
            case Statement:
                break;
            }
        }
    }

}
