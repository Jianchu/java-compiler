package ast;

import exceptions.ASTException;
import parser.ParseTree;
import scanner.Symbol;

/**
 * For types. I have decide not to use QualifiedType for now.
 * 
 * 
 * 
 * @author zanel
 *
 */
public abstract class Type extends ASTNode{
	
	/**
	 * This method parse three kinds of parse tree nodes:
	 * 	Type,
	 * 	ClassType,
	 *  InterfaceType,
	 *  ArrayType
	 *  
	 * @param pt
	 * @return
	 * @throws ASTException
	 */
	public static Type parseType(ParseTree pt) throws ASTException {
		switch (pt.getTokenType()) {
		case Type:
			ParseTree PrimitiveOrRef = pt.getChildren().get(0);
			if (PrimitiveOrRef.getTokenType() == Symbol.PrimitiveType) {
				return new PrimitiveType(PrimitiveOrRef);
			} else if (PrimitiveOrRef.getTokenType() == Symbol.ReferenceType) {
				return new SimpleType(PrimitiveOrRef);
			}
			break;
		case ClassType:
		case InterfaceType:
			return new SimpleType(pt.getFirstChild());
		}
		throw new ASTException();
	}
	
	
}
