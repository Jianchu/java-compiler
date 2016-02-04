package ast;

import java.util.LinkedList;
import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;
import scanner.Symbol;

public class ASTBuilder {
	public static CompilationUnit parseCompilationUnit(ParseTree pt) throws ASTException {
		
		CompilationUnit cu = new CompilationUnit(pt);
		return cu;
	}
	//parameter should be Statement or StatementNoShortIf
	public static Statement parseStatement(ParseTree pt) throws ASTException {
		Statement st = Statement.getStatement(pt);
		return st;
	}
	
	public static Expression parseExpression(ParseTree pt) {
//		parseExpression(pt);
		return null;
	}
	
	public static String parseID(ParseTree pt) throws ASTException {
		if (pt.getTokenType() != Symbol.ID) 
			throw new ASTException();
		return pt.getLexeme();
		
	}
	
	/**
	 * Deprecated: please use ParseTree.findChild();
	 * 
	 * Helper method for finding a child of specific type.
	 * Uses linear search. 
	 * Please only use when you know for certain from certain a node would exist, and only appear once.
	 * e.g. finding Name in "SingleTypeImportDeclaration IMPORT Name SEMICOLON"
	 * @param pt
	 * @return a child node of the type

		public static ParseTree findChild(ParseTree pt, Symbol sym) {
		for (ParseTree child : pt.getChildren()) {
			if (child.getTokenType() == sym) {
				return child;
			}
		}
		return null;
	}
		 */
	
	public static <E extends Next<E>> List<E> getList(E elem) {
		List<E> result = new LinkedList<E>();
		result.add(elem);
		while (elem.hasNext()) {
			elem = elem.next();
			result.add(elem);
		}
		return result;
	}
}
