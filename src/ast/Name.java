package ast;

import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;

/**
 * A name is either a simple name or qualified name.
 * @author zanel
 *
 */
public abstract class Name extends Expression {
	ASTNode decl;	// use ASTNode because VariableDecalration is not a bodyDeclaration
	
	public static Name parseName(ParseTree pt) throws ASTException {
		switch(pt.getTokenType()) {
		case Name:
			return parseName(pt.getFirstChild());
		case SimpleName:
			return new SimpleName(pt);
		case QualifiedName:
			return new QualifiedName(pt);
		}
		throw new ASTException("unexpected: " + pt.getTokenType());
	}
	
	public abstract List<String> getFullName();
	
	public void attachDeclaration(ASTNode node) {
		decl = node;
	}
	
	public ASTNode getDeclaration() {
		return decl;
	}
	
	
}
