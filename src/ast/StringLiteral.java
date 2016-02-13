package ast;

import parser.ParseTree;

public class StringLiteral extends Expression{
	public String value;
	public StringLiteral(ParseTree pt) {
		String str = pt.getLexeme();
		value = str.substring(1, str.length()-1);
	}
}
