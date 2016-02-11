package ast;

import java.util.LinkedList;
import java.util.List;

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
	 * This method parse four types of parse tree nodes:
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
				return parseType(PrimitiveOrRef);
			}
			break;

		case ReferenceType:
			return parseType(pt.getFirstChild());
		case ClassOrInterfaceType:
			return new SimpleType(pt);
		case ClassType:
		case InterfaceType:
			return new SimpleType(pt.getFirstChild());
		
		case ArrayType:
			return new ArrayType(pt);
		}
		throw new ASTException();
	}
	
	public static List<Type> parseInterfaceTypeList(ParseTree pt) throws ASTException {
		List<Type> result = new LinkedList<Type>();
		for (ParseTree child : pt.getChildren()) {
			switch(child.getTokenType()) {
			case InterfaceTypeList:
				result.addAll(parseInterfaceTypeList(child));
				break;
			case InterfaceType:
				result.add(parseType(child));
			default:
				break;
			}
		}
		return result;
	}
	
}
