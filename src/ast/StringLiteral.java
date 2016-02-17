package ast;

import exceptions.ASTException;
import parser.ParseTree;

public class StringLiteral extends Expression{
	public String value;
	public StringLiteral(ParseTree pt) {
		String str = pt.getLexeme();
		value = str.substring(1, str.length()-1);
	}
	
	public void accept(Visitor v) throws Exception {
		v.visit(this);
	}
}
