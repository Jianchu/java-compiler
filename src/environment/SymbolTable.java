package environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import ast.AST;
import ast.TypeDeclaration;
import exceptions.ASTException;

/**
 * created so that it is easier to maintain environment stack through different visitors
 * and if necessary to store links to all environments
 * @author zanel
 *
 */
public class SymbolTable {
	Environment curr;
	public static HashMap<List<String>, TypeDeclaration> global = null;
	
	public SymbolTable() {
		curr = null;
	}
	
	public void openScope() {
		curr = new Environment(curr);
	}
	
	public void closeScope() {
		curr = curr.enclosing;
	}
	
	public Environment currentScope() {
		return curr;
	}
	
	/**
	 * The global environment contains all the classes with fully qualified names
	 * @param trees
	 */
	public static void buildGlobal(List<AST> trees) {
		for (AST ast : trees) {
			List<String> fullName = new ArrayList<String>();
			
			if (ast.root.pkg != null)
				fullName.addAll(ast.root.pkg.name.getFullName());
			if (ast.root.types.size() != 0)
				fullName.add(ast.root.types.get(0).id);
			
			if (fullName.size() > 0)
				global.put(fullName, ast.root.types.get(0));
		}
	}
	
	/**
	 * example of how buildGlobal and SemanticsVisitor will be used.
	 * @param trees
	 * @throws ASTException 
	 */
	public static void buildEnvs(List<AST> trees) throws ASTException {
		buildGlobal(trees);
		SemanticsVisitor sv = new SemanticsVisitor();
		for (AST tree : trees) {
			tree.root.accept(sv);
		}
	}
}
