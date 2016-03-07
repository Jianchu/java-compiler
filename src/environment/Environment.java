package environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ast.*;
import exceptions.TypeLinkException;

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
	public Map<String, MethodDeclaration> constructors = null;
	
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
			constructors = new HashMap<String, MethodDeclaration>();
		case INHERIT:
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
	
	public FieldDeclaration lookUpField(String field) {
		FieldDeclaration fDecl = null;
		// search local
		if (fields != null) {
			fDecl = fields.get(field);
		}
		if (fDecl != null) {
			return fDecl;
		}
		
		if (enclosing != null) {
			fDecl= enclosing.lookUpField(field);
		}
		
		return fDecl;
	}
	
	public TypeDeclaration lookUpType(String typeName) throws TypeLinkException {
		Environment cuEnv = this;
		while (cuEnv.type != EnvType.COMPILATION_UNIT) {
			cuEnv = cuEnv.getEnclosing();
		}
		
		TypeDeclaration decl = null;
		if (typeName.contains(".")) {
			decl = SymbolTable.getGlobal().get(typeName);
		} else {
			if ((decl = findName(cuEnv.types, typeName)) == null
					&& (decl = findName(cuEnv.singleImports, typeName)) == null
					&& (decl = findName(cuEnv.samePackage, typeName)) == null
					&& (decl = findName(cuEnv.importOnDemands, typeName)) == null) {	
				return null;
			}
		}
		return decl;
	}
	
	public MethodDeclaration lookUpMethod(String methodName) {
		MethodDeclaration mDecl = null;
		// search local
		if (methods != null) {
			mDecl = methods.get(methodName);
		}
		if (mDecl != null)
			return mDecl;
		
		if (enclosing != null) {
			mDecl= enclosing.lookUpMethod(methodName);
		}
		
		return mDecl;
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
	
	public void addConstructor(String name, MethodDeclaration decl) {
		constructors.put(name, decl);
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
		INHERIT,	// contains the fields and methods inherited
		CLASS,	// contains the declared fields and methods
		BLOCK	// block includes method
	}
	
    private TypeDeclaration findName(Map<String, TypeDeclaration> map, String simpleName) throws TypeLinkException {
        Set<String> fullNames = map.keySet();
        boolean simpleNameExists = false;
        TypeDeclaration typeDec = null;
        for (String fullName : fullNames) {
            if (fullName.substring(fullName.lastIndexOf('.') + 1).equals(simpleName)) {
                if (simpleNameExists) {
                    throw new TypeLinkException("The type " + simpleName + " is ambiguous");
                }
                simpleNameExists = true;
                typeDec = map.get(fullName);
            }
        }
        if (typeDec != null) {
        	return typeDec;
        }
        return null;
    }

}
