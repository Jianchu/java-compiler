package ast;

import exceptions.ASTException;
import parser.ParseTree;
import scanner.Symbol;

/**
 * No modifiers for local variables?
 * repeat code from field declaration... 
 * 
 * takes in both LocalVariableDeclarator and FormalParameter
 * @author zanel
 *
 */
public class VariableDeclaration extends ASTNode {
	Type type = null;
	String id = null;
	Expression initializer = null;
	
	public VariableDeclaration(ParseTree pt) throws ASTException {		
		if (pt.getTokenType() == Symbol.LocalVariableDeclaration) {	
			for (ParseTree child : pt.getChildren()) {
				switch (child.getTokenType()) {
				case Type:
					type = Type.parseType(child);
					break;
				case VariableDeclarator:
					parseVariableDeclarator(child);
					break;
				default:
					break;
				}
			}
		} else if (pt.getTokenType() == Symbol.FormalParameter) {
			for (ParseTree child : pt.getChildren()) {
				switch (child.getTokenType()) {
				case Type:
					type = Type.parseType(child);
					break;
				case VariableDeclaratorId:
					id = ASTBuilder.parseID(child);
					break;
				default:
					break;
				}
			}
		}
	}
	
	private void parseVariableDeclarator(ParseTree pt) {
		for (ParseTree child : pt.getChildren()) {
			switch(child.getTokenType()) {
			case VariableDeclaratorId:
				id = child.getFirstChild().getFirstChild().getLexeme();
				break;
			case VariableInitializer:
				initializer = ASTBuilder.parseExpression(child.getFirstChild());
				break;
			default:
				break;
			}
		}
	}
}
