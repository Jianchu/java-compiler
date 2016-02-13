package ast;

import parser.ParseTree;

/**
 * Choose not to parse to int yet due to int range issue.
 * @author zanel
 *
 */
public class IntegerLiteral extends Expression {
	public String value;
	public IntegerLiteral(ParseTree pt) {
		value = pt.getLexeme();
	}
}
