package ast;

import exceptions.ASTException;
import parser.ParseTree;

public class ArrayType extends Type {
	Type type;
	public ArrayType(ParseTree pt) throws ASTException {
		ParseTree child = pt.getFirstChild();
		switch(child.getTokenType()) {
		case PrimitiveType:
			type = new PrimitiveType(child);
			break;
		case Name:
			type = new SimpleType(Name.parseName(child));
		}
	}
}
