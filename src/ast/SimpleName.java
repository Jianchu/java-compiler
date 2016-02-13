package ast;

import parser.ParseTree;


public class SimpleName extends Name {
	public String id;
	
	public SimpleName(ParseTree pt) {
		id = pt.getChildren().get(0).getLexeme();
	}
	
	public SimpleName(String name) {
		id = name;
	}
}
