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
public class QualifiedName extends Name{
	private Name qualifier = null;
	private String id = null;
	
	// the list could be length 0
	private List<String> fullName = new LinkedList<String>();
	
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
	public final List<String> getFullName() {
		return new LinkedList<String>(fullName);
	}
	
	public Name getQualifier() {
		return qualifier;
	}
	
	@Override
	public String toString() {
		return String.join(".", fullName);
	}
	
	public void accept(Visitor v) throws Exception {
		v.visit(this);
	}
}
