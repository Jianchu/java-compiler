package environment;

import java.util.List;

import ast.*;
import exceptions.NameException;

public class Disambiguation extends EnvTraversalVisitor{
	
	public void visit(Type node) {
		// do nothing. Types have already been processed
	}
	
	public void visit(MethodInvocation node) throws Exception {
		// do not visit node.id for now
		// if node.id != null. this is of the form Primary.ID(...)
		// TODO: will need to be handled in type checking.
		if (node.id != null){
			// Primary.ID(...)
			node.expr.accept(this);
		} else {
			// Name(...)
			resolveMethodName((Name) node.expr);
		}
		

		if (node.arglist != null) {
			for (Expression expr : node.arglist) {
				expr.accept(this);
			}
		}
	}
	
	public void visit(ArrayAccess node) throws Exception {
		
	}
	
	public void visit(SimpleName node) throws NameException {
		String name = node.toString();
		VariableDeclaration vDecl = curr.lookUpVariable(name);
		if (vDecl != null) {
			node.attachDeclaration(vDecl);
			return; 
		}
		FieldDeclaration fDecl = curr.lookUpField(name);
		if (fDecl == null) {
			throw new NameException("Simple Name cannot be  resolved: " + node.toString());
		} 
		node.attachDeclaration(fDecl);
	}
	
	
	public void visit(QualifiedName node) {
		node.getFullName();
	}
	
	public void resolveMethodName(Name name) {
		
	}
	
	
}
