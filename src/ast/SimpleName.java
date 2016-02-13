package ast;

import java.util.LinkedList;
import java.util.List;

import parser.ParseTree;


public class SimpleName extends Name {
	public String id;
	
	public SimpleName(ParseTree pt) {
		id = pt.getChildren().get(0).getLexeme();
	}
	
	public SimpleName(String name) {
		id = name;
	}
	
	public List<String> getFullName() {
		List<String> full = new LinkedList<String>();
		full.add(id);
		return full;
	}
}
