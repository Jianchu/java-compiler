package ast;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.ASTException;

public class VariableDeclarationStatement extends Statement {
    private VariableDeclaration varDeclar;

    public VariableDeclarationStatement(ParseTree variableDecNode) throws ASTException {
        ParseTree localVariableDecStatement = variableDecNode.findChild(Symbol.LocalVariableDeclaration);
        if (localVariableDecStatement != null) {
            this.varDeclar = new VariableDeclaration(localVariableDecStatement);
        }
    }
    
    public VariableDeclaration getVariableDec() {
        return this.varDeclar;
    }
}
