package ast;

import exceptions.ASTException;
import parser.ParseTree;
import scanner.Symbol;

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
