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
	public boolean isArrayLength = false;
	
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
	
	private QualifiedName(Name quali, String id) {
		this.qualifier = quali;
		this.id = id;
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
	
	public List<Name> getPrefixList() {
		List<Name> l = new LinkedList<Name>();
		getPrefixList(this, l);
		return l;
	}
	
	private void getPrefixList(QualifiedName n, List<Name> l) {
		if (n.qualifier != null) {
			if (n.qualifier instanceof SimpleName) {
				l.add(n.qualifier);
				l.add(n);
			} else {
				getPrefixList((QualifiedName) n.qualifier, l);
				l.add(n);
			}
		} else {
			l.add(n);
		}
	}
	
	public static void main(String[] args) {
		Name qn = new QualifiedName(new SimpleName("a"), "b");
		QualifiedName qqn = new QualifiedName(qn, "c");
		System.out.println(qn);
		System.out.println(qqn.toString());
		for (Name n : qqn.getPrefixList()) {
			System.out.println(n);
		}
	}
}
