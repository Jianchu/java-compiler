package ast;

import parser.ParseTree;
import scanner.Symbol;

public class PackageDeclaration {
	Name name;
	public PackageDeclaration(ParseTree pt) {
		for (ParseTree child : pt.getChildren()) {
			if (child.getTokenType() == Symbol.Name) {
				name = Name.parseName(child);
			}
		}
	}
}
