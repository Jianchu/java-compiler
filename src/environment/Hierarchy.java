package environment;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ast.*;

public class Hierarchy {
	Set<TypeDeclaration> visited;
	
	public Hierarchy(List<AST> trees) {
		buildHierarchy(trees);
		checkHierarchy(trees);
	}
	
	public void buildHierarchy(List<AST> trees) {
		visited = new TreeSet<TypeDeclaration>();
		for (AST tree : trees) {
			buildInherit(tree);
		}
	}

	private void buildInherit(AST tree) {
		if (tree.root.types.size() == 0) {
			// if no type declaration in the unit, skip it.
			return;
		}
		TypeDeclaration typeDecl = tree.root.types.get(0); 

		if (visited.contains(typeDecl)) {
			// if already visited, skip.
			// this is possible because of the recursive calls
			return;
		}
		
		
		
		
	}
	
	public void checkHierarchy(List<AST> trees) {
		
	}
}
