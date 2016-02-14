package environment;

import java.util.Map;

import ast.ASTNode;
import ast.FieldDeclaration;
import ast.ImportDeclaration;
import ast.MethodDeclaration;
import ast.PackageDeclaration;
import ast.TypeDeclaration;
import ast.VariableDeclaration;

public class Environment {
	Environment enclosing;
	Map<String, FieldDeclaration> fields;
	Map<String, VariableDeclaration> variables;
	Map<String, TypeDeclaration> types;
	Map<String, MethodDeclaration> methods;
	
	// imports and files from the same package are listed in the global class environment.
	
}
