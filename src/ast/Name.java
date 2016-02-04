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
		for (ParseTree child : pt.getChildren()) {
			switch (child.getTokenType()) {
			case SimpleName:
				return new SimpleName(child);
			case QualifiedName:
				return new QualifiedName(child);
			}
		}
		throw new ASTException();
	}
}
