package ast;

import java.util.List;

import parser.ParseTree;

public class ASTBuilder {
	public static CompilationUnit parseCompilationUnit(ParseTree pt) {
		
		CompilationUnit cu = new CompilationUnit();
		List<ParseTree> subTrees = pt.getChildren();
		
		for (ParseTree t : subTrees) {
			switch (t.getTokenType()) {
			case ImportDeclaration:
				break;
			case PackageDeclaration:
				break;
			case TypeDeclaration:
				TypeDeclaration td = parseTypeDeclaration(pt);
//				cu.addTypeDeclaraiton();
				break;
			}
		}
		
		return null;
	}
	
	public static TypeDeclaration parseTypeDeclaration(ParseTree pt) {
		
		Block body = (Block) parseStatement(pt);
		return null;
	}
	
	public static Statement parseStatement(ParseTree pt) {
		
		return null;
	}
	
	public static Expression parseExpression(ParseTree pt) {
//		parseExpression(pt);
		return null;
	}
}
