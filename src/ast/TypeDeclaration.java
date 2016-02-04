package ast;

import java.util.LinkedList;
import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;
import scanner.Symbol;

/**
 * Either a class declaration or interface.
 * @author zanel
 *
 */
public class TypeDeclaration extends BodyDeclaration{
	// interface or class
	boolean isInterface = false;
	
	List<Modifier> modifiers = new LinkedList<Modifier>();
	String id;
	
	// extends 
	Type superClass = null;
	
	// implements
	List<Type> interfaces = new LinkedList<Type>();
	
	// field or method declarations, but no type delcarations
	List<BodyDeclaration> members;
	
	TypeDeclaration next;
	
	public TypeDeclaration(ParseTree pt) throws ASTException {
		for (ParseTree child : pt.getChildren()) {
			switch(child.getTokenType()) {
			case TypeDeclarations:
				next = new TypeDeclaration(child);
				break;
			case TypeDeclaration:
				parseSingleType(pt);
				break;
			default:
				throw new ASTException("Unexpected node type.");	
			}
		}
	}
	
	public boolean hasNext() {
		return next == null;
	}
	
	public TypeDeclaration next() {
		return next;
	}
	
	private void parseSingleType(ParseTree pt) throws ASTException {
		ParseTree child = pt.getChildren().get(0);
		switch (child.getTokenType()) {
		case ClassDeclaration:
			parseClassDeclaration(child);
		case InterfaceDeclaration:
			isInterface = true;
			break;
		default:
			throw new ASTException("Unexpected node type.");
		}
	}
	
	private void parseClassDeclaration(ParseTree pt) throws ASTException {
		for (ParseTree child : pt.getChildren()) {
			switch (child.getTokenType()) {
			case Modifiers:
				// parse modifiers
				Modifier nextMod = new Modifier(child);
				modifiers.add(nextMod);
				while (nextMod.next != null) {
					nextMod = nextMod.next;
					modifiers.add(nextMod);
				}
				break;
			case ID:
				// parse name
				id = ASTBuilder.parseID(child);
				break;
			case Super:
				// parse extends
				superClass = Type.parseType(child);
				break;
			case Interfaces:
				interfaces.add(Type.parseType(child));
				break;
			case ClassBody:
				// TODO: parse class body
				break;
			}
		}
	}
	
	private void parseClassBody(ParseTree pt) {
		
	}
	
}
