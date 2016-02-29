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
	public Name name;
	
	public SimpleType(ParseTree pt) throws ASTException {
		// the node should be ClassOrInterfaceType
		switch(pt.getTokenType()) {
		case ClassOrInterfaceType:
			name = Name.parseName(pt.getFirstChild());
			break;
		case ClassType:	//fall through
		case InterfaceType:
			name = Name.parseName(pt.getFirstChild().getFirstChild());
			break;
		default:
			throw new ASTException();
		}
	}
	
	public SimpleType(Name type) {
		name = type;
	}
	
	public void accept(Visitor v) throws Exception {
		v.visit(this);
	}
	
	public String toString() {
		return name.toString();
	}
}
