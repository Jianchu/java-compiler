package ast;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.ASTException;

public class IfStatement extends Statement{

    private Expression ifCondition;
    private Statement ifStatement;
    private Statement elseStatement;
    private boolean hasElse = false;

    public IfStatement(ParseTree ifNode) throws ASTException {
        ParseTree elseNode = ASTBuilder.findChild(ifNode, Symbol.ELSE);
        if (elseNode != null) {
            hasElse = true;
        }
        int ithChild = 0;
        for (ParseTree child : ifNode.getChildren()) {
            switch (child.getTokenType()) {
            case Expression:
                this.ifCondition = ASTBuilder.parseExpression(child);
                break;
            case StatementNoShortIf:
                if (ithChild == 4) {
                    this.ifStatement = ASTBuilder.parseStatement(child);
                } else if (ithChild == 6) {
                    this.elseStatement = ASTBuilder.parseStatement(child);
                }
                break;
            case Statement:
                if (hasElse) {
                    this.elseStatement = ASTBuilder.parseStatement(child);
                } else {
                    this.ifStatement = ASTBuilder.parseStatement(child);
                }
                break;
            default:
                throw new ASTException("Unexpected symbol");
            }
            ithChild++;
        }
    }

    public Expression getIfCondition() {
        return this.ifCondition;
    }

    public Statement getIfStatement() {
        return this.ifStatement;
    }

    public Statement getElseStatement() {
        return this.elseStatement;
    }

}
