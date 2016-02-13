package ast;

import parser.ParseTree;

public class CharacterLiteral extends Expression{
	public String value;
	public CharacterLiteral(ParseTree pt) {
		String charStr = pt.getLexeme();
		value = charStr.substring(1, charStr.length()-1);
	}
}
