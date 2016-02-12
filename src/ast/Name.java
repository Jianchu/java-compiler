package ast;

import exceptions.ASTException;
import parser.ParseTree;

/**
 * A name is either a simple name or qualified name.
 * @author zanel
 *
 */
public abstract class Name extends Expression {
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
}
