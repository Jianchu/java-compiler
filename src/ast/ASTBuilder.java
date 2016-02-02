package ast;

import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;

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
}
