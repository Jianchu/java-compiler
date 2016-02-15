package environment;

import java.util.Stack;

import ast.*;

/**
 * responsible for constructing symbol table
 * @author zanel
 *
 */
public class TopDeclVisitor extends SemanticsVisitor {
	
	/**
	 * So that the current environment can be shared through different visitors.
	 * @param curr
	 */
	public TopDeclVisitor(SymbolTable syms)  {
		table = syms;
	}
	
	/**
	 * Build class environment
	 */
	public void visit(CompilationUnit cu) {
		
	}
}
