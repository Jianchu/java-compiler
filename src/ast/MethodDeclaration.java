package ast;

import java.util.LinkedList;
import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;
import scanner.Symbol;

public class MethodDeclaration extends BodyDeclaration{
	List<Modifier> modifiers = new LinkedList<Modifier>();
	boolean isConstructor = false;
	Type returnType = null;	//what about void
	String id;
	
	// parameters are just variable declarations without initializers assignment
	List<VariableDeclaration> parameters = new LinkedList<VariableDeclaration>();
	Block body = null;
	
	public MethodDeclaration(ParseTree pt) throws ASTException {
		if (pt.getTokenType() == Symbol.MethodDeclaration) {
			for (ParseTree child : pt.getChildren()) {
				switch (child.getTokenType()) {
				case MethodHeader:
					parseMethodHeader(child);
					break;
				case MethodBody:
					body = (Block) ASTBuilder.parseStatement(child.getFirstChild());
					break;
				default:
					throw new ASTException();
				}
			}
		} else if (pt.getTokenType() == Symbol.ConstructorDeclaration) {
			isConstructor = true;
			for (ParseTree child : pt.getChildren()) {
				switch (child.getTokenType()) {
				case Modifiers:
					modifiers.addAll(ASTBuilder.getList(new Modifier(child)));
					break;
				case ConstructorDeclarator:
					parseConstructorDeclarator(child);
				case ConstructorBody:
					body = (Block) ASTBuilder.parseStatement(child.getFirstChild());
					break;
				default:
					throw new ASTException();
				}
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void parseMethodHeader(ParseTree pt) throws ASTException {
		for (ParseTree child : pt.getChildren()) {
			switch(child.getTokenType()) {
			case Modifiers:
				modifiers.addAll(ASTBuilder.getList(new Modifier(child)));
				break;
			case Type:
				returnType = Type.parseType(child);
				break;
			case MethodDeclarator:
				parseMethodDeclarator(child);
				break;
			case VOID:
				break;
			default:
				throw new ASTException();
			}
		}
	}
	
	private void parseConstructorDeclarator(ParseTree pt) throws ASTException {
		for (ParseTree child : pt.getChildren()) {
			switch(child.getTokenType()) {
			case SimpleName:
				id = ASTBuilder.parseID(child.getFirstChild());
				break;
			case FormalParameterList:
				parseFormalParameterList(child);
				break;
			default:
				break;
			}
		}
		
	}

	
	private void parseMethodDeclarator(ParseTree pt) throws ASTException {
		for (ParseTree child : pt.getChildren()) {
			switch(child.getTokenType()) {
			case ID:
				id = ASTBuilder.parseID(child);
				break;
			case FormalParameterList:
				parseFormalParameterList(child);
				break;
			default:
				break;
			}
		}
	}
	
	private void parseFormalParameterList(ParseTree pt) throws ASTException {
		for (ParseTree child : pt.getChildren()) {
			switch(child.getTokenType()) {
			case FormalParameterList:
				parseFormalParameterList(child);
				break;
			case FormalParameter:
				parameters.add(new VariableDeclaration(child));
				break;
			default:
				break;
			}
		}
	}
	
}
