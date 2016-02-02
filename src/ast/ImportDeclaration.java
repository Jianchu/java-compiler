package ast;

import exceptions.ASTException;
import parser.ParseTree;
import scanner.Symbol;

public class ImportDeclaration {
	ImportDeclaration next;
	Name name;
	boolean onDemand;
	
	public ImportDeclaration(ParseTree pt) throws ASTException {
		for (ParseTree child : pt.getChildren()) {
			switch(child.getTokenType()) {
			case ImportDeclarations:
				next = new ImportDeclaration(child);
				break;
			case ImportDeclaration:
				ParseTree singleOrDemand = child.getChildren().get(0);
				if (singleOrDemand.getTokenType() == Symbol.SingleTypeImportDeclaration) {
					
				}
				break;
			default:
				throw new ASTException("Unexpected symbol");	
			}
		}
	}
}
