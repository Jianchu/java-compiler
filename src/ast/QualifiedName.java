package ast;

import exceptions.ASTException;
import parser.ParseTree;

public class QualifiedName extends Name{
	Name qualifier;
	String id;
	
	public QualifiedName(ParseTree pt) throws ASTException {
		for (ParseTree child : pt.getChildren()) {
			switch (child.getTokenType()) {
			case Name:
				qualifier = Name.parseName(child);
			case ID:
				id = ASTBuilder.parseID(child);
			}
		}
	}
}
