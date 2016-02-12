package ast;

import exceptions.ASTException;
import parser.ParseTree;

public class QualifiedName extends Name{
	Name qualifier = null;
	String id = null;
	
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
	}
}
