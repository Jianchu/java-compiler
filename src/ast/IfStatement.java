package ast;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.ASTException;

public class IfStatement extends Statement{

    public Expression ifCondition;
    public Statement ifStatement;
    public Statement elseStatement;
    private boolean hasElse = false;

    public IfStatement(ParseTree ifNode) throws ASTException {
        ParseTree elseNode = ifNode.findChild(Symbol.ELSE);
        if (elseNode != null) {
            hasElse = true;
        }
        // not sure what will happen...
        int ithChild = 0;
        for (ParseTree child : ifNode.getChildren()) {
            switch (child.getTokenType()) {
            case Expression:
                this.ifCondition = Expression.parseExpression(child);
                break;
            case StatementNoShortIf:
                if (ithChild == 4) {
                    this.ifStatement = Statement.parseStatement(child);
                } else if (ithChild == 6) {
                    this.elseStatement = Statement.parseStatement(child);
                }
                break;
            case Statement:
                if (hasElse) {
                    this.elseStatement = Statement.parseStatement(child);
                } else {
                    this.ifStatement = Statement.parseStatement(child);
                }
                break;
            default:
                throw new ASTException("Unexpected symbol");
            }
            ithChild++;
        }
    }

    // public Expression getIfCondition() {
    // return this.ifCondition;
    // }
    //
    // public Statement getIfStatement() {
    // return this.ifStatement;
    // }
    //
    // public Statement getElseStatement() {
    // return this.elseStatement;
    // }

}
