package environment;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ast.*;
import exceptions.HierarchyException;

public class Hierarchy {
	Set<TypeDeclaration> visited;
	SymbolTable table;
	Set<TypeDeclaration> onPath;	//for checking cycles
	
	public Hierarchy(List<AST> trees, SymbolTable symTable) throws HierarchyException {
		table = symTable;
		buildHierarchy(trees);
		checkHierarchy(trees);
	}
	
	/**
	 * fills the inherit environment for each type declaration
	 * @param trees
	 * @throws HierarchyException 
	 */
	public void buildHierarchy(List<AST> trees) throws HierarchyException {
		visited = new TreeSet<TypeDeclaration>();
		for (AST tree : trees) {
			if (tree.root.types.size() > 0)
				
				buildInherit(tree.root.types.get(0), new TreeSet<TypeDeclaration>());
		}
	}

	private void buildInherit(TypeDeclaration typeDecl, Set<TypeDeclaration> ancestors) throws HierarchyException {
		
		if (visited.contains(typeDecl)) {
			// if already visited, skip.
			// this is possible because of the super class calls
			return;
		}
		
		Environment inheritEnv = typeDecl.getEnvironment().getEnclosing();
		
		// create a new set of ancestors including self for checking cycles
		Set<TypeDeclaration> newAncesters = new TreeSet<TypeDeclaration>(ancestors);
		newAncesters.add(typeDecl);
		
		// inherit from super interfaces
		for (Type itf : typeDecl.interfaces) {
			TypeDeclaration itfDecl = itf.getDeclaration();
			if (newAncesters.contains(itfDecl)) {
				// if super interface is an ancestor, error
				throw new HierarchyException("cycle detected in class hierarchy.");
			}
			if (! visited.contains(itfDecl)) {
				buildInherit(itfDecl, newAncesters);
			}
			Environment superEnv = itfDecl.getEnvironment();
			inherit(inheritEnv, superEnv);
		}
		
		// parent class
		if (typeDecl.superClass != null) {
			TypeDeclaration superDecl = typeDecl.superClass.getDeclaration();
			if (newAncesters.contains(superDecl)) {
				throw new HierarchyException("cycle detected in class hierarchy.");
			}
			if (! visited.contains(superDecl)) {
				// if the super class has not been processed, do it first
				buildInherit(superDecl, newAncesters);
			}
			
			Environment superEnv = superDecl.getEnvironment();
			inherit(inheritEnv, superEnv);
		}
		
		if (typeDecl.isInterface) {
			if (typeDecl.interfaces.size() == 0 
				&& typeDecl != table.getObjectInterfaceRef()) {
				// if interface does not extend any other interfaces
				// implicitly inheirt from object interface
				TypeDeclaration objInterface = table.getObjectInterfaceRef();
				if (! visited.contains(objInterface)) {
					visited.add(objInterface);
				}
				inherit(inheritEnv, objInterface.getEnvironment());
			}
		} else {	// is class
			if (typeDecl.superClass == null && typeDecl != table.getObjRef()) {
				// if class does not extend any class
				// inherit from object
				TypeDeclaration obj = table.getObjRef();
				if (! visited.contains(obj)) {
					visited.add(obj);
				}
				inherit(inheritEnv, obj.getEnvironment());		
			}
		}
		
		visited.add(typeDecl);
		
	}
	
	public void inherit(Environment inheritEnv, Environment superEnv) {
		Environment superInherit = superEnv.getEnclosing();
		
		// fields from super class, overriding might happen
		for (String field : superInherit.fields.keySet()) {
			inheritEnv.addField(field, superEnv.fields.get(field));
		}
		for (String field : superEnv.fields.keySet()) {
			inheritEnv.addField(field, superEnv.fields.get(field));
		}
		
		//methods from superclass, overriding might happen
		for (String method : superInherit.methods.keySet()) {
			inheritEnv.addMethod(method, superInherit.methods.get(method));
		}
		for (String method : superEnv.methods.keySet()) {
			inheritEnv.addMethod(method, superEnv.methods.get(method));
		}
	}
	
	public void checkHierarchy(List<AST> trees) {
		
	}
}
