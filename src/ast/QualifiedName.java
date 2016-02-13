package ast;

import java.util.LinkedList;
import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;

public class QualifiedName extends Name{
	public Name qualifier = null;
	public String id = null;
	
	// the list could be length 0
	public List<String> fullName = new LinkedList<String>();
	
	public QualifiedName(ParseTree pt) throws ASTException {
		for (ParseTree child : pt.getChildren()) {
			switch (child.getTokenType()) {
			case Name:
				qualifier = Name.parseName(child);
				break;
			case ID:
				id = ASTHelper.parseID(child);
				break;
			}
		}
		
		// build the full name list so that we don't have to parse the name again.
		if (id != null) {
			fullName.add(id);
			if (qualifier != null) {
				fullName.addAll(0, qualifier.getFullName());
			}
		}
	}
	
	/**
	 * 
	 */
	public List<String> getFullName() {
		return fullName;
	}
	public void accept(Visitor v) throws ASTException {
		v.visit(this);
	}
}
