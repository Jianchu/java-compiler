package environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ast.*;

public class Environment {
	Environment enclosing;
	
	/* 
	 * put variable and field declaration both in one map.
	 * first of all because they never exist at the same time
	 * secondly it makes look up of variable name easier.
	 */
	Map<String, ASTNode> variables;
	
	/*
	 * imports and files from the same package 
	 * are listed in the global class environment, under types variable.
	 */
	Map<String, TypeDeclaration> types;
	Map<String, MethodDeclaration> methods;
	
	public Environment(Environment outer) {
		enclosing = outer;
		variables = new HashMap<String, ASTNode>();
		types = new HashMap<String, TypeDeclaration>();
		methods = new HashMap<String, MethodDeclaration>();
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
	
	public Environment getEnclosing() {
		return enclosing;
	}
	
	public enum Type {
		// might be incomplete
		GLOBAL,
		CLASS,
		METHOD,
		BLOCK
	}
	

}
