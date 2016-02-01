package ast;

import parser.ParseTree;
import scanner.Symbol;

public class AST {
	
	
	public AST(ParseTree pt) {
		
	}
	
	private ASTNode buildTree(ParseTree pt) {
		
		ASTBuilder.parseCompilationUnit(pt);
		
		return null;
	}
	
	

	public static void main(String[] args) {
		
	}
	
}
