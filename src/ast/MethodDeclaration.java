package ast;

import java.util.List;

public class MethodDeclaration extends BodyDeclaration{
	List<Modifier> modifiers;
	Type returnType;	//what about void
	
	List<SingleVariableDeclaration> parameters;
	Block body;
}
