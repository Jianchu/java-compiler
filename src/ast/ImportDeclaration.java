package ast;

import exceptions.ASTException;
import parser.ParseTree;
import scanner.Symbol;

public class ImportDeclaration {
	ImportDeclaration next;
	Name name;
	boolean onDemand;
	
	public ImportDeclaration(ParseTree pt) throws ASTException {
		onDemand = false;
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
}
