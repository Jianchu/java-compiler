package ast;

import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;
import scanner.Symbol;

public class ASTBuilder {
	public static CompilationUnit parseCompilationUnit(ParseTree pt) throws ASTException {
		
		CompilationUnit cu = new CompilationUnit();
		List<ParseTree> subTrees = pt.getChildren();
		
		for (ParseTree t : subTrees) {
			switch (t.getTokenType()) {
			case ImportDeclaration:
				break;
			case PackageDeclaration:
				break;
			case TypeDeclaration:
				TypeDeclaration td = new TypeDeclaration(t);
				break;
			default:
				throw new ASTException("Unexpected node type.");	
			}
		}
		
		return null;
	}
	
	public static Statement parseStatement(ParseTree pt) {
		
		return null;
	}
	
	public static Expression parseExpression(ParseTree pt) {
//		parseExpression(pt);
		return null;
	}
	
	/**
	 * Helper method for finding a child of specific type.
	 * Uses linear search. 
	 * Please only use when you know for certain from certain a node would exist, and only appear once.
	 * e.g. finding Name in "SingleTypeImportDeclaration IMPORT Name SEMICOLON"
	 * @param pt
	 * @return a child node of the type
	 */
	public static ParseTree findChild(ParseTree pt, Symbol sym) {
		for (ParseTree child : pt.getChildren()) {
			if (child.getTokenType() == sym) {
				return child;
			}
		}
		return null;
	}
}
