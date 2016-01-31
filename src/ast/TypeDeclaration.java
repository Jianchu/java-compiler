package ast;

import java.util.List;

/**
 * Either a class declaration or interface.
 * @author zanel
 *
 */
public class TypeDeclaration extends BodyDeclaration{
	// interface or class
	boolean isInterface;
	
	List<Modifier> modifiers;
	String id;
	
	// extends 
	Name superClass;
	
	// implements
	List<Name> superInterfaces;
	
	// field or method declarations, but no type delcarations
	List<BodyDeclaration> members;
}
