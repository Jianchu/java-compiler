package environment;

import java.util.List;

import ast.*;
import exceptions.NameException;
import exceptions.TypeLinkException;

/**
 * Not handled at this stage:
 * 	Type: already did at TopDeclVisitor
 * 	Method Invocation: require type info
 * 	
 * @author zanel
 *
 */
public class Disambiguation extends EnvTraversalVisitor{
	
	public void visit(TypeDeclaration node) throws Exception {
		System.out.println(node.getFullName());
		super.visit(node);
	}
	
	@Override
	public void visit(SimpleType node) {
		// do nothing. Types have already been processed
	}
	
	@Override
	public void visit(FieldAccess node) throws Exception {
		// do not visit node.id on purpose
		if (node.expr != null)
			node.expr.accept(this);
	}
	
	@Override
	public void visit(MethodInvocation node) throws Exception {
		// do not visit node.id purpose
		// if node.id != null. this is of the form Primary.ID(...)
		// TODO: will need to be handled in type checking.
		if (node.id != null){
			// Primary.ID(...)
			node.expr.accept(this);
		} else {
			// Name(...)
			// cannot be resolved at this stage because type information of parameters are needed
		}
		

		if (node.arglist != null) {
			for (Expression expr : node.arglist) {
				expr.accept(this);
			}
		}
	}
	
	public void visit(SimpleName node) throws NameException {
		String name = node.toString();
//		System.out.println("\t" + name);
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
	
	/**
	 * TODO: check for no decl in 
	 * A1.A2.A3....
	 * @throws Exception 
	 */
	public void visit(QualifiedName node) throws Exception {
		List<String> fn = node.getFullName();
		ASTNode a1Decl = curr.lookUpVariable(fn.get(0));
		if (a1Decl != null) {
			// A1 is variable declaration, the rest are instance field;
			TypeDeclaration prefixDecl = ((VariableDeclaration) a1Decl).type.getDeclaration();
			prefixDecl = searchField(node, prefixDecl);
			node.attachDeclaration(prefixDecl);		// the final prefix is just the full name
			return;
		}
		
		a1Decl = curr.lookUpField(fn.get(0));
		if (a1Decl != null) {
			// A1 is a field, the rest are instance fields
			TypeDeclaration prefixDecl = ((FieldDeclaration) a1Decl).type.getDeclaration();
			prefixDecl = searchField(node, prefixDecl);
			node.attachDeclaration(prefixDecl);		// the final prefix is just the full name
			return;
		}
		
		for (int i = 1; i < fn.size(); i++) {
			String prefix = String.join(".", fn.subList(0, i));

			TypeDeclaration prefixDecl = curr.lookUpType(prefix);
			if (prefixDecl != null) {
				// prefix A1...Ai is type
				// A(i+1) is field
				FieldDeclaration fDecl = prefixDecl.getEnvironment().lookUpField(fn.get(i));
				if (fDecl == null)
					throw new NameException("Static field not found.");
				
				int j = i + 1;
				while (j < fn.size()) {
					TypeDeclaration fType = fDecl.type.getDeclaration();
					fDecl = fType.getEnvironment().lookUpField(fn.get(j));
				}
				
				node.attachDeclaration(fDecl);
				return;
			}
		}
		
		throw new NameException("Qualified Name not recognized: " + node.toString());
	}
	
	/**
	 * for looking up instance fields
	 * @param fn
	 * @param decl
	 * @return
	 * @throws NameException 
	 */
	private TypeDeclaration searchField(QualifiedName name, TypeDeclaration prefixDecl) throws NameException {		
		List<String> fn = name.getFullName();
		for (int i = 1; i < fn.size(); i++) {	
			if (prefixDecl == null) {
				// array.length
				if (fn.get(i).equals("length") && fn.size() == i+1) {
					name.getQualifier().attachDeclaration(prefixDecl);
					name.isArrayLength = true;
					return null;
				} else {
					throw new NameException("Field Prefix not recognized: "+ String.join(".", fn.subList(0, i+1)));
				}
			}
			
			// normal
			FieldDeclaration fDecl = prefixDecl.getEnvironment().lookUpField(fn.get(i));
			if (fDecl == null) {
				throw new NameException("Field Prefix not recognized: "+ String.join(".", fn.subList(0, i+1)));
			}
			prefixDecl = fDecl.type.getDeclaration();
		}
		return prefixDecl;
	}

	
	public static void disambiguate(List<AST> trees) throws Exception {
		for (AST t : trees) {
			Visitor v = new Disambiguation();
			t.root.accept(v);
		}
	}
	
	
}
