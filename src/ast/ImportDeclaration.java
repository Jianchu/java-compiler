package ast;

import exceptions.ASTException;
import parser.ParseTree;
import scanner.Symbol;

public class ImportDeclaration extends ASTNode {
	public ImportDeclaration next = null;
	public Name name = null;
	public boolean onDemand = false;
	
	public ImportDeclaration(ParseTree pt) throws ASTException {
		for (ParseTree child : pt.getChildren()) {
			switch(child.getTokenType()) {
			case ImportDeclarations:
				next = new ImportDeclaration(child);
				break;
			case ImportDeclaration:
				ParseTree singleOrDemand = child.getFirstChild();
				ParseTree nameTree = singleOrDemand.findChild(Symbol.Name);
				name = Name.parseName(nameTree);
				if (singleOrDemand.getTokenType() == Symbol.TypeImportOnDemandDeclaration) {
					onDemand = true;
				}
				break;
			default:
				throw new ASTException("Unexpected symbol");	
			}
		}
	}
	
	public boolean hasNext() {
		return next != null;
	}
	
	public ImportDeclaration next() {
		return next;
	}
	
	public void accept(Visitor v) throws ASTException {
		v.visit(this);
	}
	
}
