package ast;

import java.util.LinkedList;
import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;
import scanner.Symbol;

public class MethodDeclaration extends BodyDeclaration{
	public List<Modifier> modifiers = new LinkedList<Modifier>();
	public boolean isConstructor = false;
	public boolean isAbstract = false;
	public Type returnType = null;	//null is void.
	public String id;
	
	// parameters are just variable declarations without initializers assignment
	List<VariableDeclaration> parameters = new LinkedList<VariableDeclaration>();
	public Block body = null;
	
	public MethodDeclaration(ParseTree pt) throws ASTException {
		if (pt.getTokenType() == Symbol.MethodDeclaration
				|| pt.getTokenType() == Symbol.AbstractMethodDeclaration) {
			for (ParseTree child : pt.getChildren()) {
				switch (child.getTokenType()) {
				case MethodHeader:
					parseMethodHeader(child);
					break;
				case MethodBody:
					ParseTree block = child.findChild(Symbol.Block);
					if (block != null) {
						body = (Block) Statement.parseStatement(child);
					} else {
						// abstract method in abstract class
						isAbstract = true;
					}
					break;
				case SEMICOLON:
					// only appears in AbstractMethodDeclaration
					isAbstract = true;
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
					modifiers.addAll(ASTHelper.getList(new Modifier(child)));
					break;
				case ConstructorDeclarator:
					parseConstructorDeclarator(child);
					break;
				case ConstructorBody:
					ParseTree block = child.findChild(Symbol.Block);
					if (block != null) {
						body = (Block) Statement.parseStatement(child);
					}
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
				modifiers.addAll(ASTHelper.getList(new Modifier(child)));
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
                        case ID:
                            id = ASTHelper.parseID(child);
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
				id = ASTHelper.parseID(child);
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
	
	public void accept(Visitor v) throws Exception {
		v.visit(this);
	}
	
}
