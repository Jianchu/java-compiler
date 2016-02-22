package environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ast.*;

public class Environment {
	Environment enclosing;
	EnvType type;
	/* 
	 * put variable and field declaration both in one map.
	 * first of all because they never exist at the same time
	 * secondly it makes look up of variable name easier.
	 */
	Map<String, ASTNode> variables = null;
	
	/*
	 * imports and files from the same package 
	 * are listed in the global class environment, under types variable.
	 */
	Map<String, TypeDeclaration> types = null;
	Map<String, MethodDeclaration> methods = null;
	
	// only for compilation unit type scope
	Map<String, TypeDeclaration> singleImports = null;
	Map<String, TypeDeclaration> importOnDemands = null;
	Map<String, TypeDeclaration> samePackage = null;
	
	
	public Environment(Environment outer, EnvType scopeType) {
		enclosing = outer;
		type = scopeType;
		switch (scopeType) {
		case COMPILATION_UNIT:
			singleImports = new HashMap<String, TypeDeclaration>();
			importOnDemands = new HashMap<String, TypeDeclaration>();
			samePackage = new HashMap<String, TypeDeclaration>();
			types = new HashMap<String, TypeDeclaration>();
			break;
			
		case CLASS:
			variables = new HashMap<String, ASTNode>();
			methods = new HashMap<String, MethodDeclaration>();
			break;
			
		case BLOCK:
			variables = new HashMap<String, ASTNode>();
			break;
			
		}
	}
	
	public ASTNode lookUpVariable(String varName) {
		ASTNode result = variables.get(varName);
		if (result != null)
			return result;
		
		if (enclosing != null) {
			result = enclosing.lookUpVariable(varName);
		}
		return result;
	}
	
	public void addVariable(String name, ASTNode declaration) {
		variables.put(name, declaration);
	}
	
	public void addMethod(String name, MethodDeclaration decl) {
		methods.put(name, decl);
	}
	
	public void addType(String name, TypeDeclaration decl) {
		types.put(name, decl);
	}
	
	public void addSingleImport(String name, TypeDeclaration decl) {
		singleImports.put(name, decl);
	}
	
	public void addImportOnDemand(String name, TypeDeclaration decl) {
		importOnDemands.put(name, decl);
	}
	
	public void addSamePackage(String name, TypeDeclaration decl) {
		samePackage.put(name, decl);
	}
	
	public Environment getEnclosing() {
		return enclosing;
	}
	
	public enum EnvType {
		// might be incomplete
		COMPILATION_UNIT,
		CLASS,
		BLOCK	// block includes method
	}
	

}
