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
	public boolean isInterface = false;
	
	public List<Modifier> modifiers = new LinkedList<Modifier>();
	public String id = null;
	
	// extends 
	public Type superClass = null;
	
	// implements
	public List<Type> interfaces = new LinkedList<Type>();
	
	// field or method declarations, but no type delcarations
	public List<BodyDeclaration> members = new LinkedList<BodyDeclaration>();
	
	public TypeDeclaration next = null;
	
	public TypeDeclaration(ParseTree pt) throws ASTException {
		for (ParseTree child : pt.getChildren()) {
			switch(child.getTokenType()) {
			case TypeDeclarations:
				next = new TypeDeclaration(child);
				break;
			case TypeDeclaration:
				parseSingleType(child);
				break;
			default:
				throw new ASTException("Unexpected node type.");	
			}
		}
	}
	
	public boolean hasNext() {
		return next != null;
	}
	
	public TypeDeclaration next() {
		return next;
	}
	
	private void parseSingleType(ParseTree pt) throws ASTException {
		ParseTree child = pt.getChildren().get(0);
		switch (child.getTokenType()) {
		case InterfaceDeclaration:
			isInterface = true;
			// intentional fall through
		case ClassDeclaration:
			parseClassDeclaration(child);
			break;
		default:
			throw new ASTException("Unexpected node type." + child.getTokenType());
		}
	}
	
	/**
	 * works for both Class and Interface
	 * @param pt
	 * @throws ASTException
	 */
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
				id = ASTHelper.parseID(child);
				break;
			
			/*
			 * class specific
			 */
			case Super:
				// parse extends
				superClass = Type.parseType(child.findChild(Symbol.ClassType));
				break;
			case Interfaces:
				// problem
				interfaces.addAll(Type.parseInterfaceTypeList(child.findChild(Symbol.InterfaceTypeList)));
				break;
			case ClassBody:
				// TODO: parse class body
				parseClassBody(child);
				break;
			
			/*
			 * Interface specific
			 */
			case ExtendsInterfaces:
				interfaces.addAll(Type.parseInterfaceTypeList(pt.findChild(Symbol.InterfaceTypeList)));
				break;
			case InterfaceBody:
				parseInterfaceBody(child);
				break;
			}
		}
	}
	

	private void parseInterfaceBody(ParseTree pt) throws ASTException {
		ParseTree declarations = pt.findChild(Symbol.InterfaceMemberDeclarations);
		if  (declarations != null) {
			parseInterfaceMemberDeclarations(declarations);
		}
	}
	
	private void parseInterfaceMemberDeclarations(ParseTree pt) {
		for (ParseTree child : pt.getChildren()) {
			switch(child.getTokenType()) {
			case InterfaceMemberDeclarations:
				parseInterfaceMemberDeclarations(child);
				break;
			case InterfaceMemberDeclaration:
				child.findChild(Symbol.AbstractMethodDeclaration);
				break;
			}
		}
	}
	
	
	private void parseClassBody(ParseTree pt) throws ASTException {
		ParseTree declarations = pt.findChild(Symbol.ClassBodyDeclarations);
		if  (declarations != null) {
			parseClassBodyDeclarations(declarations);
		}
	}
	
	private void parseClassBodyDeclarations(ParseTree pt) throws ASTException {
		for (ParseTree child : pt.getChildren()) {
			switch(child.getTokenType()) {
			case ClassBodyDeclarations:
				parseClassBodyDeclarations(child);
				break;
			case ClassBodyDeclaration:
				ParseTree member = child.getFirstChild();
				if (member.getTokenType() == Symbol.ClassMemberDeclaration) {
					ParseTree fieldOrMethod = member.getFirstChild();
					switch(fieldOrMethod.getTokenType()) {
					case FieldDeclaration:
						members.add(new FieldDeclaration(fieldOrMethod));
						break;
					case MethodDeclaration:
						members.add(new MethodDeclaration(fieldOrMethod));
						break;
					default:
						break;
					}
				} else if (member.getTokenType() == Symbol.ConstructorDeclaration) {
					members.add(new MethodDeclaration(member));
				}
				break;
			}
		}
	}
	
	public void accept(Visitor v) throws ASTException {
		v.visit(this);
	}
	
}
