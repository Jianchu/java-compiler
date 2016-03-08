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
//				System.out.println(tree.root.types.get(0).getFullName());
				buildInherit(tree.root.types.get(0), new HashSet<TypeDeclaration>());
//				if (tree.root.types.get(0).getFullName().equals("Main")) {
//					for (String m : tree.root.types.get(0).getEnvironment().getEnclosing().methods.keySet()) {
//						
//						System.out.println(m);
//					}
//				}
		}
	}

	private void buildInherit(TypeDeclaration typeDecl, Set<TypeDeclaration> ancestors) throws HierarchyException {
		
		if (visited.contains(typeDecl)) {
			// if already visited, skip.
			// this is possible because of the super class calls
			return;
		}
		
		
		// create a new set of ancestors including self for checking cycles
		Set<TypeDeclaration> newAncesters = new HashSet<TypeDeclaration>(ancestors);
		newAncesters.add(typeDecl);
		Set<TypeDeclaration> superTypes = new HashSet<TypeDeclaration>();
		
		if (typeDecl.isInterface) {
			if (typeDecl.interfaces.size() == 0 
				&& typeDecl != SymbolTable.getObjectInterfaceRef()) {
				// if interface does not extend any other interfaces
				// implicitly inheirt from object interface
				TypeDeclaration objInterface = SymbolTable.getObjectInterfaceRef();
				superTypes.add(objInterface);
				SimpleType st = new SimpleType(new SimpleName("objInterface"));
				st.attachDeclaration(objInterface);
				typeDecl.interfaces.add(st);
			}
		} else {	// is class
			if (typeDecl.superClass == null && typeDecl != SymbolTable.getObjRef()) {
				// if class does not extend any class
				// inherit from object
				TypeDeclaration obj = SymbolTable.getObjRef();
				superTypes.add(obj);
				SimpleType st = new SimpleType(new SimpleName("Object"));
				st.attachDeclaration(obj);
				typeDecl.superClass = st;
			}
		}
		
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
			superTypes.add(itfDecl);
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
			
			superTypes.add(superDecl);
		}
		
		// process superTypes
		inherit(typeDecl, superTypes);
		
		
		visited.add(typeDecl);
		
	}
	
	private void inherit(TypeDeclaration typeDecl, Set<TypeDeclaration> superTypes) throws HierarchyException {
		inheritFields(typeDecl, superTypes);
		inheritMethods(typeDecl, superTypes);
	}
	
	private void inheritFields(TypeDeclaration typeDecl, Set<TypeDeclaration> superTypes) {
		Environment clsEnv = typeDecl.getEnvironment();
		Environment inheritEnv = clsEnv.getEnclosing();
		for (TypeDeclaration sup : superTypes) {
			Environment supEnv = sup.getEnvironment();
			Environment supInheritEnv = sup.getEnvironment();
			for (String f : supEnv.fields.keySet()) {
				if (!clsEnv.fields.containsKey(f)) {
					inheritEnv.addField(f, supEnv.fields.get(f));
				}
			}
			for (String f : supInheritEnv.fields.keySet()){
				if (!clsEnv.fields.containsKey(f)) {
					inheritEnv.addField(f, supInheritEnv.fields.get(f));
				}
			}
			
		}
	}

	private void inheritMethods(TypeDeclaration typeDecl, Set<TypeDeclaration> superTypes) throws HierarchyException {
		for (TypeDeclaration sup : superTypes) {
			Environment supEnv = sup.getEnvironment();
			Environment supInheritEnv = supEnv.getEnclosing();
			inheritEnv(typeDecl, supEnv, superTypes);
			inheritEnv(typeDecl, supInheritEnv, superTypes);
		}
	}
	
	public void inheritEnv(TypeDeclaration cls, Environment supEnv, Set<TypeDeclaration> superTypes) throws HierarchyException {
		Environment clsEnv = cls.getEnvironment();
		Environment inheritEnv = clsEnv.getEnclosing();
		for (String m : supEnv.methods.keySet()) {
//			System.out.println("\t" + m);
			MethodDeclaration mDecl = clsEnv.methods.get(m);
			if (mDecl != null) {
				// m in declare(T), check replace, but do not add to inherit
				checkReplace(mDecl, supEnv.methods.get(m));
			} else {
				// m not in declare(T)
				MethodDeclaration smDecl = supEnv.methods.get(m);
				if (!smDecl.isAbstract) {
					// if not abstract, in inherit
					
					// replace other abstract methods from other supers
					checkInheritReplace(m, smDecl, superTypes);
					
					inheritEnv.addMethod(m, smDecl);
				} else {
					// find all methods like this one
					if (allAbstractAndNoMoreVisible(m, smDecl, superTypes)) {
						if (inheritEnv.methods.keySet().contains(m)) {
							MethodDeclaration existing = inheritEnv.methods.get(m);
							checkReturnType(smDecl, existing);
						}
						inheritEnv.addMethod(m, smDecl);
					}
				}
			}
		}
	}
	
	
	private void checkReturnType(MethodDeclaration newM, MethodDeclaration oldM) throws HierarchyException {
//		System.out.println("\t" + newM.id + newM.returnType + " - " + oldM.returnType);
		if (!(newM.returnType == oldM.returnType || newM.returnType.equals(oldM.returnType))) {
			throw new HierarchyException("different return type: " + newM.returnType + " : " + oldM.returnType);
		}
	}

	private void checkInheritReplace(String m, MethodDeclaration mDecl, Set<TypeDeclaration> superTypes) throws HierarchyException {
		Set<MethodDeclaration> all = getAllMethods(m, superTypes);
		for (MethodDeclaration old : all) {
			if (old != mDecl) {
				checkReplace(mDecl, old);
			}
		}
	}

	/**
	 * check for all abstract. also if there is a version of better visibility.
	 * for example if this method is protected, and there is one that is public. return false.
	 * 
	 * 
	 * @param m
	 * @param mDecl
	 * @param superTypes
	 * @return
	 */
	private boolean allAbstractAndNoMoreVisible(String m, MethodDeclaration mDecl, Set<TypeDeclaration> superTypes) {
		for (TypeDeclaration sup :superTypes) {
			Environment supEnv = sup.getEnvironment();
			MethodDeclaration otherDecl = supEnv.lookUpMethod(m);
			if (otherDecl != null && otherDecl != mDecl) {
				if (!otherDecl.isAbstract) {
					return false;
				} else if (otherDecl.modifiers.contains(Modifier.PUBLIC) 
						&& mDecl.modifiers.contains(Modifier.PROTECTED)) {
					return false;
				}
			}
		}
		return true;
	}

	private void checkReplace(MethodDeclaration mDecl, MethodDeclaration old) throws HierarchyException {
		// TODO Auto-generated method stub
		MethodDeclaration decl1 = mDecl;
		MethodDeclaration decl2 = old;
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

	/**
	 * Get all methods of the same signature
	 * @param m
	 * @param superTypes
	 * @return
	 */
	private Set<MethodDeclaration> getAllMethods(String m, Set<TypeDeclaration> superTypes) {
		Set<MethodDeclaration> all = new HashSet<MethodDeclaration>();
		for (TypeDeclaration sup : superTypes) {
			MethodDeclaration mDecl = sup.getEnvironment().lookUpMethod(m);
			if (mDecl != null) {
				all.add(mDecl);
			}
		}
		return all;
	}
	
	public void checkHierarchy(List<AST> trees) throws Exception {
		for (AST ast : trees) {
			if (ast.root.types.size() == 0)
				continue;
			TypeDeclaration tDecl = ast.root.types.get(0);
			
			Environment clsEnv = ast.root.types.get(0).getEnvironment();
			Environment inheritEnv = clsEnv.getEnclosing();
			
			// check that type does  not inherit from  obj final
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
		}
	}
	
}
