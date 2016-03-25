package code_generation;

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
		
		
		
	}

	private static void classOffSet(List<TypeDeclaration> clsDecls) {
		
	}
	
	
}
