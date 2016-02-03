package ast;

import exceptions.ASTException;
import parser.ParseTree;
import scanner.Symbol;

public class ImportDeclaration {
	ImportDeclaration next = null;
	Name name = null;
	boolean onDemand = false;
	
	public ImportDeclaration(ParseTree pt) throws ASTException {
		for (ParseTree child : pt.getChildren()) {
			switch(child.getTokenType()) {
			case ImportDeclarations:
				next = new ImportDeclaration(child);
				break;
			case ImportDeclaration:
				ParseTree singleOrDemand = child.getChildren().get(0);
				ParseTree nameTree = ASTBuilder.findChild(child, Symbol.Name);
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
		return next == null;
	}
	
	public ImportDeclaration next() {
		return next;
	}
	
	
	
}
