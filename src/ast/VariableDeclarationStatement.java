package ast;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.ASTException;

public class VariableDeclarationStatement extends Statement {
    public VariableDeclaration varDeclar;

    public VariableDeclarationStatement(ParseTree variableDecNode) throws ASTException {
        ParseTree localVariableDecStatement = variableDecNode.findChild(Symbol.LocalVariableDeclaration);
        if (localVariableDecStatement != null) {
            this.varDeclar = new VariableDeclaration(localVariableDecStatement);
        }
    }
    
    // public VariableDeclaration getVariableDec() {
    // return this.varDeclar;
    // }
    
	public void accept(Visitor v) throws ASTException {
		v.visit(this);
	}
}
