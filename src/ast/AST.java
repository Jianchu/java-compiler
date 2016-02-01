package ast;

import parser.ParseTree;
import scanner.Symbol;

public class AST {
	
	
	public AST(ParseTree pt) {
		
	}
	
	private ASTNode buildTree(ParseTree pt) {
		switch (pt.getTokenType()) {
		case CompilationUnit:
		default:
			break;
		}
		pt.getChildren();
		return null;
	}
	
	

	public static void main(String[] args) {
		
	}
	
}
