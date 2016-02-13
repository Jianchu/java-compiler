package ast;

import parser.ParseTree;
import scanner.Symbol;

public class PrimitiveType extends Type {
	public Value type;
	
	public PrimitiveType(ParseTree pt) {
		ParseTree child = pt.getFirstChild();
		switch (child.getTokenType()) {
		case BOOLEAN:
			type = Value.BOOLEAN;
			break;
		case NumericType:
			ParseTree Integral = child.getFirstChild();
			switch(Integral.getTokenType()) {
			case INT:
				type = Value.INT;
				break;
			case BYTE:
				type = Value.BYTE;
				break;
			case CHAR:
				type = Value.CHAR;
				break;
			case SHORT:
				type = Value.SHORT;
				break;
			}
			
		}
	}
	
	public enum Value {
		BOOLEAN,
		INT,
		BYTE,
		CHAR,
		SHORT
	}
}
