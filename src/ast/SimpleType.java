package ast;

import exceptions.ASTException;
import parser.ParseTree;
import scanner.Symbol;

/**
 * Class or interface type
 * This form includes both compound and simple name
 * @author zanel
 *
 */
public class SimpleType extends Type{
	Name name;
	
	public SimpleType(ParseTree pt) throws ASTException {
		// the node should be ClassOrInterfaceType
		switch(pt.getTokenType()) {
		case ClassOrInterfaceType:
			name = Name.parseName(pt.getFirstChild());
		case ClassType:
		case InterfaceType:
			name = Name.parseName(pt.getFirstChild().getFirstChild());
		default:
			throw new ASTException();
		}
	}
	
	public SimpleType(Name type) {
		name = type;
	}
	
}
