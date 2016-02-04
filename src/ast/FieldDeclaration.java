package ast;

import java.util.LinkedList;
import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;


public class FieldDeclaration extends BodyDeclaration{
	// zero or more
	List<Modifier> modifiers = new LinkedList<Modifier>();
	Type type = null; 
	String id = null;
	Expression initializer = null;
	
	public FieldDeclaration(ParseTree pt) throws ASTException {
		for (ParseTree child : pt.getChildren()) {
			switch (child.getTokenType()) {
			case Modifiers:
				Modifier nextMod = new Modifier(child);
				modifiers.addAll(ASTBuilder.getList(nextMod));
				break;
			case Type:
				type = Type.parseType(child);
				break;
			case VariableDeclarator:
				parseVariableDeclarator(child);
				break;
			}
		}
	}
	
	private void parseVariableDeclarator(ParseTree pt) {
		for (ParseTree child : pt.getChildren()) {
			switch(child.getTokenType()) {
			case VariableDeclaratorId:
				id = child.getFirstChild().getFirstChild().getLexeme();
				break;
			case VariableInitializer:
				initializer = ASTBuilder.parseExpression(child.getFirstChild());
				break;
			}
		}
	}
	
}
