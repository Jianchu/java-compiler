package ast;

import java.util.List;

public class FieldDeclaration extends BodyDeclaration{
	// zero or more
	List<Modifier> modifiers;
	Type type; 
	String id;
	Expression initializer;
}
