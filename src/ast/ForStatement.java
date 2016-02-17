package ast;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.ASTException;

public class ForStatement extends Statement{

    // type for forInit? It could be VariableDeclaration or StatementExpression.
    // So use ASTNode for now

    public Expression forInit;
    public Expression forCondition;
    public Expression forUpdate;
    public Statement forBody;

    public ForStatement(ParseTree forNode) throws ASTException {
        System.out.println(forNode.getTokenType());
        for (ParseTree child : forNode.getChildren()) {
            switch (child.getTokenType()) {
            case ForInit:
                for (ParseTree forInitChild : child.getChildren()) {
                    if (checkNodeType(forInitChild, Symbol.StatementExpression)) {
                        this.forInit = Expression.parseExpression(forInitChild);
                    } else if (checkNodeType(forInitChild, Symbol.LocalVariableDeclaration)) {
                        this.forInit = new VariableDeclarationExpression(forInitChild);
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
                break;
            }
        }
    }

    // public Expression getForCondition() {
    // return this.forCondition;
    // }
    //
    // public Expression getForUpdate() {
    // return this.forUpdate;
    // }
    //
    // public Statement getForBody() {
    // return this.forBody;
    // }
    //
    // public ASTNode getForInit() {
    // return this.forInit;
    // }
    
	public void accept(Visitor v) throws Exception {
		v.visit(this);
	}
}
