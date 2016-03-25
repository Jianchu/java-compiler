package code_generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ast.*;

public class OffSet {
	// separate classes from interfaces
	// for interfaces compute global offset used in big ugly table
	// for classes compute offset through inheritance
	Map<TypeDeclaration, List<String>> ugly = new HashMap<TypeDeclaration, List<String>>();
	
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
		
		List<String> itfMethods = new LinkedList<String>();	// a methods index in the list is its offset
		// results in linear search, but O(number of methods) is ok
		
		for (TypeDeclaration itf : itfDecls) {
			
			Map<String, MethodDeclaration> methodNamespace = itf.getEnvironment().methods;
			for (String mName : methodNamespace.keySet()) {
				// get all the method names (mangled), ignore those inherited because they will be found sooner or later
				int offset = itfMethods.indexOf(mName);
				if (offset == -1) {
					// if the name has not been seen and no offset is knows
					itfMethods.add(mName);
					methodNamespace.get(mName).setOffSet(itfMethods.size() - 1); 	//set offset in the declaration
				} else {
					// if the name has been seen, use the offsets
					methodNamespace.get(mName).setOffSet(offset);
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
		}
		
	}

	private static void classOffSet(List<TypeDeclaration> clsDecls) {
		
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
}
