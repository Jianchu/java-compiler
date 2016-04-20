package ast;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.ASTException;

public class PackageDeclaration extends ASTNode{
    public Name name;

    public PackageDeclaration(ParseTree pt) throws ASTException {
        for (ParseTree child : pt.getChildren()) {
            if (child.getTokenType() == Symbol.Name) {
                name = Name.parseName(child);
            }
        }
    }

    public void accept(Visitor v) throws Exception {
        v.visit(this);
    }

}
