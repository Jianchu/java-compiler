package ast;

import java.util.List;

public class MethodDeclaration extends BodyDeclaration{
	List<Modifier> modifiers;
	boolean constructor;
	Type returnType;	//what about void
	
	List<VariableDeclaration> parameters;
	Block body;
}
