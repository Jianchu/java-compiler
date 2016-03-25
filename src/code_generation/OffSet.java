package code_generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ast.*;

public class OffSet {
	// separate classes from interfaces
	// for interfaces compute global offset used in big ugly table
	// for classes compute offset through inheritance
	static Map<TypeDeclaration, List<String>> ugly = new HashMap<TypeDeclaration, List<String>>();
	static List<String> itfMethods;
	
	public static void computeOffSet(List<AST> trees) {
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
		
		classOffSet(clsDecls);
		interfaceOffSet(itfDecls, clsDecls);
		
	}

	/*
	 * computes global offset for interface methods,
	 * and construct big ugly table
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
		
	}
	
	private static void fillUglyColumn(Map<String, MethodDeclaration> methodEnv, List<String> itfMethods, List<String> ptrs) {
		for (String mName : methodEnv.keySet()) {
			int offset = itfMethods.indexOf(mName);
			if (offset > -1) {
				// if it is an interface method, don't even care if this class implements the interface
				// add signature label to the entry, to be used later as pointer
				ptrs.set(offset, SigHelper.getMethodSig(methodEnv.get(mName)));
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
	
	private static void classOffSet(List<TypeDeclaration> clsDecls) {
		Set<TypeDeclaration> visited = new HashSet<TypeDeclaration>();
		for (TypeDeclaration cls : clsDecls) {
			if (!visited.contains(cls)) {
				singleClassOffSet(cls, visited);
			}
		}
	}

	private static void singleClassOffSet(TypeDeclaration cls, Set<TypeDeclaration> visited) {
		TypeDeclaration superCls = cls.superClass.getDeclaration();
		if (!visited.contains(superCls)) {
			// builds offset for super class
			singleClassOffSet(superCls, visited);
		}
		
		// compute offset for fields and methods separately
		
	}
	
	
	

}
