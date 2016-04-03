package code_generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import joosc.Joosc;
import utility.FileUtility;
import ast.AST;
import ast.BodyDeclaration;
import ast.FieldDeclaration;
import ast.MethodDeclaration;
import ast.Modifier;
import ast.TypeDeclaration;
import ast.Visitor;
import environment.NameHelper;
import exceptions.NameException;

public class OffSet {
	// separate classes from interfaces
	// for interfaces compute global offset used in big ugly table
	// for classes compute offset through inheritance
	static Map<TypeDeclaration, List<String>> ugly = new HashMap<TypeDeclaration, List<String>>();
	static List<String> itfMethods;
	static boolean debug = false;
	
	public static void computeOffSet(List<AST> trees) throws Exception {
                ugly = new HashMap<TypeDeclaration, List<String>>();
		List<TypeDeclaration> clsDecls = new LinkedList<TypeDeclaration>();
		List<TypeDeclaration> itfDecls = new LinkedList<TypeDeclaration>();
		
		// separate into two types
		for (AST ast : trees) {
			if (ast.root.types.size() > 0) {
				TypeDeclaration type = ast.root.types.get(0);
				if (type.isInterface) {
					itfDecls.add(type);
				} else {
					clsDecls.add(type);
				}
			}
		}
		
		classOffSet(clsDecls);	// offset for fields and methods
		interfaceOffSet(itfDecls, clsDecls);	// big ugly table
		
		for (TypeDeclaration cls : clsDecls) {
			// compute local variable offsets, no need for interface because there won't be local variables
			Visitor vov = new VariableOffSetVisitor();
			cls.accept(vov);
		}
		
	}

	/**
	 * computes global offset for interface methods,
	 * and construct big ugly table
	 * @param itfDecls
	 * @param clsDecls
	 */
	private static void interfaceOffSet(List<TypeDeclaration> itfDecls, List<TypeDeclaration> clsDecls) {
		
		itfMethods = new LinkedList<String>();	// a methods index in the list is its offset
		// results in linear search, but O(number of methods) is ok
		
		for (TypeDeclaration itf : itfDecls) {
			
			Map<String, MethodDeclaration> methodNamespace = itf.getEnvironment().methods;
			for (String mName : methodNamespace.keySet()) {
				// get all the method names (mangled), ignore those inherited because they will be found sooner or later
				int offset = itfMethods.indexOf(mName);
				if (offset == -1) {
					// if the name has not been seen and no offset is knows
					itfMethods.add(mName);	// add to list, index is offset
				}
			}
		}
		
		// build up big ugly table
		for (TypeDeclaration cls :clsDecls) {
			List<String> ptrs = new ArrayList<String>(itfMethods.size());
			for (int i = 0; i < itfMethods.size(); i ++) {
				ptrs.add("0"); 	// initialize to string 0
			}
			
			fillUglyColumn(cls.getEnvironment().methods, itfMethods, ptrs);		
			fillUglyColumn(cls.getEnvironment().getEnclosing().methods, itfMethods, ptrs);	// inherited methods
			
			ugly.put(cls, ptrs);	// add column to table, kinda
		}
		
		if (debug) {
			for (TypeDeclaration cls : ugly.keySet()) {
				System.out.printf("%10s",cls.id);
			}
			System.out.println();
			for (int i = 0; i < itfMethods.size(); i++) {
				for (TypeDeclaration cls : ugly.keySet()) {
					System.out.printf("%10s", ugly.get(cls).get(i));
				}
				System.out.println();
			}
		}
	}
	
	private static void fillUglyColumn(Map<String, MethodDeclaration> methodEnv, List<String> itfMethods, List<String> ptrs) {
		for (String mName : methodEnv.keySet()) {
			int offset = itfMethods.indexOf(mName);
			if (offset > -1) {
				// if it is an interface method, don't even care if this class implements the interface
				// add signature label to the entry, to be used later as pointer
				ptrs.set(offset, SigHelper.getMethodSigWithImp(methodEnv.get(mName)));
			}
		}
	}
	
	/**
	 * get the offset of an interface method, undefined for class method
	 * @param mangledMethodName
	 * @return
	 * @throws Exception
	 */
	public static int getInterfaceMethodOffset(String mangledMethodName) throws Exception {
		int offset = itfMethods.indexOf(mangledMethodName);
		if (offset == -1) {
			throw new Exception("No interface of name is found: " + mangledMethodName);
		}
		return offset;
	}
	
	/**
	 * Computes offset of instance fields and methods
	 * @param clsDecls
	 * @throws NameException
	 */
	private static void classOffSet(List<TypeDeclaration> clsDecls) throws NameException {
		Set<TypeDeclaration> visited = new HashSet<TypeDeclaration>();
		for (TypeDeclaration cls : clsDecls) {
			if (!visited.contains(cls)) {
				singleClassOffSet(cls, visited);
			}
		}
	}

	private static void singleClassOffSet(TypeDeclaration cls, Set<TypeDeclaration> visited) throws NameException {
		if (cls.superClass != null) {
			TypeDeclaration superCls = cls.superClass.getDeclaration();
			if (!visited.contains(superCls)) {
				// builds offset for super class
				singleClassOffSet(superCls, visited);
			}
			
			cls.cloneFieldOffSet(superCls.getFieldOffSetList());	// offset is there for inherited method
			cls.cloneMethodOffSet(superCls.getMethodOffSetList());
		}
		
		// compute offset for fields and methods separately
		// no need to process inherited methods, because their offset has been inherited
		for (BodyDeclaration bDecl : cls.members) {
			if (bDecl instanceof FieldDeclaration) {	// field
				FieldDeclaration fDecl = (FieldDeclaration) bDecl;
				if (fDecl.modifiers.contains(Modifier.STATIC)) {	// offset do not apply for static fields
					continue;
				}
				
				// no polymorphism, all fields need new offset
				cls.addFieldOffSet(SigHelper.getFieldSig(fDecl));
				
//				int offset = cls.getFieldOffSetList().indexOf(fDecl.id);
//				if (offset < 0) {
//					// this field has not been declared in super class, append to offset
//					cls.addFieldOffSet(fDecl.id);
//				}	// else the name is already there, do nothing.
				
			} else {	// method
				MethodDeclaration mDecl = (MethodDeclaration) bDecl;
				if (mDecl.isConstructor || mDecl.modifiers.contains(Modifier.STATIC)){	// do not compute offset for constructors
					continue;
				}
				String mangledName = NameHelper.mangle(mDecl);
				int offset = cls.getMethodOffSetList().indexOf(mangledName);	// use mangled name
				if (offset < 0) {
					cls.addMethodOffSet(mangledName);
				}
			}
		}
		
		visited.add(cls);
		
		if (debug) {
			System.out.println(cls.id);
			for (int i = 0; i < cls.getFieldOffSetList().size(); i++) {
				System.out.println("\t" + cls.getFieldOffSetList().get(i) + " " + i);
			}
			for (int i = 0; i < cls.getMethodOffSetList().size(); i++) {
				System.out.println("\t" + cls.getMethodOffSetList().get(i) + " " + i);
			}
		}
		
	}
	
	
	public static void main(String[] args) {
		String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/test/testprogram/offset1/").toArray(paths);
        Joosc.compileSTL(paths);
	}
	

}
