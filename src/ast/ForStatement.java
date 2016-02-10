package ast;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.ASTException;

public class ForStatement extends Statement{

    // type for forInit? It could be VariableDeclaration or StatementExpression.
    // So use ASTNode for now

    private ASTNode forInit;
    private Expression forCondition;
    private Expression forUpdate;
    private Statement forBody;

    public ForStatement(ParseTree forNode) throws ASTException {
        for (ParseTree child : forNode.getChildren()) {
            switch (child.getTokenType()) {
            case ForInit:
                for (ParseTree forInitChild : child.getChildren()) {
                    if (checkNodeType(forInitChild, Symbol.StatementExpression)) {
                        this.forInit = Expression.parseExpression(child);
                    } else if (checkNodeType(forInitChild, Symbol.LocalVariableDeclaration)) {
                        this.forInit = new VariableDeclarationStatement(forInitChild);
                    }
                }
                break;
            case Expression:
                this.forCondition = Expression.parseExpression(child);
                break;
            case ForUpdate:
                // send StatementExpression to parseExpression
                this.forUpdate = Expression.parseExpression(child.getChildren().get(0));
                break;
            case Statement:
            case StatementNoShortIf:
                this.forBody = Statement.parseStatement(child);
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

    public ASTNode getForInit() {
        return this.forInit;
    }
}
