package ast;

import parser.ParseTree;

/**
 * A name is either a simple name or qualified name.
 * @author zanel
 *
 */
public abstract class Name extends ASTNode {
	public static Name parseName(ParseTree pt) {
		return null;
	}
}
