package ast;

import java.util.LinkedList;
import java.util.List;

import parser.ParseTree;
import exceptions.ASTException;


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
				modifiers.addAll(ASTHelper.getList(nextMod));
				break;
			case Type:
				type = Type.parseType(child);
				break;
			case VariableDeclarator:
				parseVariableDeclarator(child);
				break;
			default:
				break;
			}
		}
	}
	
    private void parseVariableDeclarator(ParseTree pt) throws ASTException {
		for (ParseTree child : pt.getChildren()) {
			switch(child.getTokenType()) {
			case VariableDeclaratorId:
				id = child.getFirstChild().getFirstChild().getLexeme();
				break;
			case VariableInitializer:
				initializer = Expression.parseExpression(child.getFirstChild());
				break;
			default:
				break;
			}
		}
	}
	
}
