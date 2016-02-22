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

	public Map<String, VariableDeclaration> variables = null;
	public Map<String, FieldDeclaration> fields = null;
	
	/*
	 * imports and files from the same package 
	 * are listed in the global class environment, under types variable.
	 */
	public Map<String, TypeDeclaration> types = null;
	public Map<String, MethodDeclaration> methods = null;
	
	// only for compilation unit type scope
	public Map<String, TypeDeclaration> singleImports = null;
	public Map<String, TypeDeclaration> importOnDemands = null;
	public Map<String, TypeDeclaration> samePackage = null;
	
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
			fields = new HashMap<String, FieldDeclaration>();
			methods = new HashMap<String, MethodDeclaration>();
			break;
			
		case BLOCK:
			variables = new HashMap<String, VariableDeclaration>();
			break;
			
		}
	}
	
	public VariableDeclaration lookUpVariable(String varName) {
		if (variables == null)
			return null;
		
		VariableDeclaration result = variables.get(varName);
		if (result != null)
			return result;
		
		if (enclosing != null) {
			result = enclosing.lookUpVariable(varName);
		}
		return result;
	}
	
	public void addField(String name, FieldDeclaration decl) {
		fields.put(name, decl);
	}
	
	
	public void addVariable(String name, VariableDeclaration declaration) {
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
		INTERFACE,
		SUPER,
		CLASS,
		BLOCK	// block includes method
	}
	

}
