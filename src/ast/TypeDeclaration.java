package ast;

import java.util.List;

import parser.ParseTree;

/**
 * Either a class declaration or interface.
 * @author zanel
 *
 */
public class TypeDeclaration extends BodyDeclaration{
	// interface or class
	boolean isInterface;
	
	List<Modifier> modifiers;
	SimpleName id;
	
	// extends 
	Type superClass;
	
	// implements
	List<Type> superInterfaces;
	
	// field or method declarations, but no type delcarations
	List<BodyDeclaration> members;
	
	public TypeDeclaration(ParseTree pt) {
		
	}
}
