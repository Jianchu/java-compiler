package ast;

import java.util.List;

public class CompilationUnit extends ASTNode{
	PackageDeclaration pkg;
	List<ImportDeclaration> imports;
	List<TypeDeclaration> types;
}
