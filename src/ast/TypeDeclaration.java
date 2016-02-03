package ast;

import java.util.LinkedList;
import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;

/**
 * Either a class declaration or interface.
 * @author zanel
 *
 */
public class TypeDeclaration extends BodyDeclaration{
	// interface or class
	boolean isInterface = false;
	
	List<Modifier> modifiers = new LinkedList<Modifier>();
	SimpleName id;
	
	// extends 
	Type superClass;
	
	// implements
	List<Type> superInterfaces;
	
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
			
		case InterfaceDeclaration:
			isInterface = true;
			break;
		default:
			throw new ASTException("Unexpected node type.");
		}
	}
	
	private void parseClassDeclaration(ParseTree pt) {
		for (ParseTree child : pt.getChildren()) {
			switch (child.getTokenType()) {
			case Modifiers:
				// parse modifiers
				break;
			case ID:
				// parse name
				break;
			case Super:
				// parse extends
				break;
			case Interfaces:
				break;
			case  ClassBody:
				break;
			}
		}
	}
	
}
