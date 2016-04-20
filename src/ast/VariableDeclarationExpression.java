package ast;

import parser.ParseTree;
import exceptions.ASTException;

public class VariableDeclarationExpression extends Expression {
    public VariableDeclaration variableDeclaration;

    public VariableDeclarationExpression(ParseTree variableDecNode) throws ASTException {
        this.variableDeclaration = new VariableDeclaration(variableDecNode);
    }
    
    public void accept(Visitor v) throws Exception {
        v.visit(this);
    }
}
