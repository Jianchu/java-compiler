package ast;

import parser.ParseTree;
import scanner.Symbol;
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
                for (ParseTree forInitChild : child.getChildren()) {
                    if (checkNodeType(forInitChild, Symbol.StatementExpression)) {
                        this.forInit = ASTBuilder.parseExpression(child);
                    } else if (checkNodeType(forInitChild, Symbol.LocalVariableDeclaration)) {
                        // need local variable dec?
                    }
                }
                break;
            case Expression:
                this.forCondition = ASTBuilder.parseExpression(child);
                break;
            case ForUpdate:
                this.forUpdate = ASTBuilder.parseExpression(child);
                break;
            case Statement:
            case StatementNoShortIf:
                this.forBody = ASTBuilder.parseStatement(child);
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

    private boolean checkNodeType(ParseTree node, Symbol symbol) {
        if (node.getTokenType().equals(symbol)) {
            return true;
        }
        return false;
    }

}
