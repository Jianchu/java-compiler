package ast;

import exceptions.ASTException;
import parser.ParseTree;
import scanner.Symbol;

public class AST {
	
	
	public AST(ParseTree pt) {
		
	}
	
	private ASTNode buildTree(ParseTree pt) throws ASTException {
		
		ASTBuilder.parseCompilationUnit(pt);
		
		return null;
	}
	
	

	public static void main(String[] args) {
		
	}
	
}
