package environment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ast.*;
import exceptions.AbstractMethodException;
import exceptions.HierarchyException;

public class Hierarchy {
	Set<TypeDeclaration> visited;
	
	public Hierarchy(List<AST> trees) throws Exception {
		buildHierarchy(trees);
		checkHierarchy(trees);
	}
	
	/**
	 * fills the inherit environment for each type declaration
	 * also checks for cycles in hierarchy
	 * @param trees
	 * @throws HierarchyException 
	 */
	public void buildHierarchy(List<AST> trees) throws HierarchyException {
		visited = new HashSet<TypeDeclaration>();
		for (AST tree : trees) {
			if (tree.root.types.size() > 0)
				
				buildInherit(tree.root.types.get(0), new HashSet<TypeDeclaration>());
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
		Set<TypeDeclaration> newAncesters = new HashSet<TypeDeclaration>(ancestors);
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
			inherit(inheritEnv, superEnv, true);
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
			inherit(inheritEnv, superEnv, false);
		}
		
		if (typeDecl.isInterface) {
			if (typeDecl.interfaces.size() == 0 
				&& typeDecl != SymbolTable.getObjectInterfaceRef()) {
				// if interface does not extend any other interfaces
				// implicitly inheirt from object interface
				TypeDeclaration objInterface = SymbolTable.getObjectInterfaceRef();
				if (! visited.contains(objInterface)) {
					visited.add(objInterface);
				}
				SimpleType st = new SimpleType(new SimpleName("objInterface"));
				st.attachDeclaration(objInterface);
				typeDecl.interfaces.add(st);
				inherit(inheritEnv, objInterface.getEnvironment(), true);
			}
		} else {	// is class
			if (typeDecl.superClass == null && typeDecl != SymbolTable.getObjRef()) {
				// if class does not extend any class
				// inherit from object
				TypeDeclaration obj = SymbolTable.getObjRef();
				if (! visited.contains(obj)) {
					visited.add(obj);
				}
				SimpleType st = new SimpleType(new SimpleName("Object"));
				st.attachDeclaration(obj);
				typeDecl.superClass = st;
				inherit(inheritEnv, obj.getEnvironment(), false);		
			}
		}
		
		visited.add(typeDecl);
		
	}
	
	public void inherit(Environment inheritEnv, Environment superEnv, boolean isInterface) throws HierarchyException {
		Environment superInherit = superEnv.getEnclosing();
		
		// fields from super class, overriding might happen
		for (String field : superInherit.fields.keySet()) {
			inheritEnv.addField(field, superInherit.fields.get(field));
		}
		for (String field : superEnv.fields.keySet()) {
			inheritEnv.addField(field, superEnv.fields.get(field));
		}
		
		//methods from superclass, overriding might happen
		for (String method : superInherit.methods.keySet()) {
			if (isInterface && superInherit.methods.get(method).modifiers.contains(Modifier.STATIC)) {
				// static methods from interfaces do not get inherited
				continue;
			}
				
			checkReplace(inheritEnv, superInherit, method);
			MethodDeclaration decl1 = inheritEnv.methods.get(method);
			MethodDeclaration decl2 = superInherit.methods.get(method);
			if (decl1 != null
					&& decl1.isAbstract && decl2.isAbstract
					&& decl1.modifiers.contains(Modifier.PUBLIC) && !decl2.modifiers.contains(Modifier.PUBLIC)) {
				// protected abstract shall not replace public abstract
				continue;
			}
			
			
			inheritEnv.addMethod(method, superInherit.methods.get(method));
		}
		for (String method : superEnv.methods.keySet()) {
			if (isInterface && superEnv.methods.get(method).modifiers.contains(Modifier.STATIC)) {
				// static methods from interfaces do not get inherited
				continue;
			}
			MethodDeclaration decl1 = inheritEnv.methods.get(method);
			MethodDeclaration decl2 = superEnv.methods.get(method);
			if (decl1 != null
					&& decl1.isAbstract && decl2.isAbstract
					&& decl1.modifiers.contains(Modifier.PUBLIC) && !decl2.modifiers.contains(Modifier.PUBLIC)) {
				continue;
			}
			checkReplace(inheritEnv, superEnv, method);
			inheritEnv.addMethod(method, superEnv.methods.get(method));
		}
	}
	
	public void checkHierarchy(List<AST> trees) throws Exception {
		checkPublicFinal(trees);
	}
	
	/**
	 * for all methods that m, m' such that m replaces m',
	 * if m' is public, then m must be public
	 * 
	 * 
	 * @param trees
	 * @throws HierarchyException 
	 * @throws AbstractMethodException 
	 */
	public void checkPublicFinal(List<AST> trees) throws HierarchyException, AbstractMethodException {
		for (AST ast : trees) {
			if (ast.root.types.size() == 0)
				continue;
			TypeDeclaration tDecl = ast.root.types.get(0);
			
			Environment clsEnv = ast.root.types.get(0).getEnvironment();
			Environment inheritEnv = clsEnv.getEnclosing();
			
			// check that type does  not inherit from  obj
			Environment objEnv = SymbolTable.getObjRef().getEnvironment();
			
			if (tDecl != SymbolTable.getObjRef() && tDecl != SymbolTable.getObjectInterfaceRef()) {
				for (String m : clsEnv.methods.keySet()) {
					MethodDeclaration md = objEnv.methods.get(m);
					if (md != null && md.modifiers.contains(Modifier.FINAL)) {
						throw new HierarchyException("cannot override final from Object: " + md.id);
					}
				}
			}
			
			// check that if type has abstract method and is class, then is abstract class
			Set<MethodDeclaration> mContains = new HashSet<MethodDeclaration>();
			for (MethodDeclaration md :clsEnv.methods.values()) {
				mContains.add(md);
			}
			for (String m : inheritEnv.methods.keySet()) {
				if (!clsEnv.methods.containsKey(m)) {
					mContains.add(inheritEnv.methods.get(m));
				}
			}
			for (MethodDeclaration md : mContains) {
				if (md.isAbstract) {
					TypeDeclaration typeDecl = ast.root.types.get(0);
					if (!typeDecl.isInterface && !typeDecl.modifiers.contains(Modifier.ABSTRACT)) {
						throw new AbstractMethodException(typeDecl.id + "." + md.id);
					}
				}				
			}
			
			for (String m : clsEnv.methods.keySet()) {
				if (inheritEnv.methods.containsKey(m)) {
					MethodDeclaration decl1 = clsEnv.methods.get(m);
					MethodDeclaration decl2 = inheritEnv.methods.get(m);
					// decl2 is replaced by decl1
					
					// if the method replaced is public, the new method needs to be public 
					if (decl2.modifiers.contains(Modifier.PUBLIC) &&
							!decl1.modifiers.contains(Modifier.PUBLIC)) {
						throw new HierarchyException("A non-public method replaced public method.");
					}
					
					// the old method can't be final
					if (decl2.modifiers.contains(Modifier.FINAL)) {
						throw new HierarchyException("Final method cannot be override.s");
					}
					
					// old method static <=> new method static
					if (decl2.modifiers.contains(Modifier.STATIC) != decl1.modifiers.contains(Modifier.STATIC)) {
						throw new HierarchyException("Static modifier in replaced method does not match.");
					}
					
					// return types should be the same
					if (!(decl2.returnType == decl1.returnType || decl2.returnType.equals(decl1.returnType))) {
						// both checks are required to deal with simple type and primitive type.
						throw new HierarchyException("Return type of replaced method does not match.");
					}
				}
			}
		}
	}
	
	public void checkReplace(Environment toEnv, Environment fromEnv, String method) throws HierarchyException {
		if (toEnv.methods.containsKey(method)) {
			MethodDeclaration decl2 = toEnv.methods.get(method);
			MethodDeclaration decl1 = fromEnv.methods.get(method);
			// decl2 is replaced by decl1
			
			// applies to abstract method replace too.
			if (!(decl2.returnType == decl1.returnType || decl2.returnType.equals(decl1.returnType))) {
				throw new HierarchyException("Return type of replaced method does not match.");
			}
			
			// only applies to superclass replace
			if (!decl1.isAbstract && decl2.modifiers.contains(Modifier.PUBLIC) &&
					!decl1.modifiers.contains(Modifier.PUBLIC)) {
				throw new HierarchyException("A non-public method replaced public method.");
			}
			// only applies to superclass replace
			// the old method can't be final
			if (decl2.modifiers.contains(Modifier.FINAL)) {
				throw new HierarchyException("Final method cannot be overriden.");
			}
			
		}
	}
}
