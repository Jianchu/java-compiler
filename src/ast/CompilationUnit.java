package ast;

import java.util.LinkedList;
import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;

public class CompilationUnit extends ASTNode{
	public PackageDeclaration pkg = null;
	public List<ImportDeclaration> imports = new LinkedList<ImportDeclaration>();
	public List<TypeDeclaration> types = new LinkedList<TypeDeclaration>();
	
	public CompilationUnit(ParseTree pt) throws ASTException {
		List<ParseTree> subTrees = pt.getChildren();
		
		for (ParseTree child : subTrees) {
			switch (child.getTokenType()) {
			case PackageDeclaration:
				pkg = new PackageDeclaration(child);
				break;
			case ImportDeclarations:
				ImportDeclaration nextImport = new ImportDeclaration(child);
				// populate the list with next
				imports.add(nextImport);
				while (nextImport.hasNext()) {
					nextImport = nextImport.next();
					imports.add(nextImport);
				}
				break;
			case TypeDeclarations:
				TypeDeclaration nextType = new TypeDeclaration(child);
				types.add(nextType);
				while (nextType.hasNext()) {
					nextType = nextType.next();
					types.add(nextType);
				}
				break;
			default:
				throw new ASTException("Unexpected node type.");	
			}
		}
	}
}
