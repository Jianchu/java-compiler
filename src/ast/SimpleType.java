package ast;

import exceptions.ASTException;
import parser.ParseTree;
import scanner.Symbol;

/**
 * Class or interface type
 * @author zanel
 *
 */
public class SimpleType extends Type{
	Name name;
	
	public SimpleType(ParseTree pt) throws ASTException {
		// the node should be ClassOrInterfaceType
		if (pt.getTokenType() != Symbol.ClassOrInterfaceType)
			throw new ASTException();
		
		name = Name.parseName(pt.getFirstChild());
	}
	
	public SimpleType(Name type) {
		name = type;
	}
	
}
