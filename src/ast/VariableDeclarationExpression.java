package ast;

import exceptions.ASTException;
import parser.ParseTree;

public class VariableDeclarationExpression extends Expression {
    public VariableDeclaration variableDeclaration;

    public VariableDeclarationExpression(ParseTree variableDecNode) throws ASTException {
        this.variableDeclaration = new VariableDeclaration(variableDecNode);
    }
    
	public void accept(Visitor v) throws ASTException {
		v.visit(this);
	}
}