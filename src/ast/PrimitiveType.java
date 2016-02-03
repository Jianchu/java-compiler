package ast;

import parser.ParseTree;

public class PrimitiveType extends Type {
	SimpleName name;
	
	public PrimitiveType(ParseTree pt) {
		switch (pt.getChildren().get(0).getTokenType()) {
		case BOOLEAN:
			// return boolean literal
		case NumericType:
			
		}
	}
}
