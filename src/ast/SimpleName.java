package ast;

import java.util.LinkedList;
import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;

/**
 * getters are provided for this class. 
 * do not attempt to make the fields public.
 * @author zanel
 *
 */
public class SimpleName extends Name {
	private String id;
	
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
	
	@Override
	public String toString() {
		return id;
	}
	
	public void accept(Visitor v) throws Exception {
		v.visit(this);
	}
}
