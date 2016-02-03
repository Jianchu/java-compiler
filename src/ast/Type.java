package ast;

import parser.ParseTree;
import scanner.Symbol;

public abstract class Type extends ASTNode{
	
	public static Type parseType(ParseTree pt) {
		ParseTree PrimitiveOrRef = pt.getChildren().get(0);
		if (PrimitiveOrRef.getTokenType() == Symbol.PrimitiveType) {
			return new PrimitiveType(PrimitiveOrRef);
		} else if (PrimitiveOrRef.getTokenType() == Symbol.ReferenceType) {
			
		}
		
		return null;
	}
	

}
