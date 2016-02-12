package name;

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
	
	Map<String, ImportDeclaration> imports;
	Map<String, PackageDeclaration> pkg;
}
