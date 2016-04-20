package ast;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.ASTException;

/**
 * No modifiers for local variables?
 * repeat code from field declaration... 
 * 
 * takes in both LocalVariableDeclaraton and FormalParameter
 * @author zanel
 *
 */
public class VariableDeclaration extends ASTNode {
    public Type type = null;
    public String id = null;
    public Expression initializer = null;
//    private int offset;

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
                    id = ASTHelper.parseID(child.getFirstChild());
                    break;
                default:
                    break;
                }
            }
        }
    }

    private void parseVariableDeclarator(ParseTree pt) throws ASTException {
        for (ParseTree child : pt.getChildren()) {
            switch(child.getTokenType()) {
            case VariableDeclaratorId:
                id = child.getFirstChild().getLexeme();
                break;
            case VariableInitializer:
                initializer = Expression.parseExpression(child.getFirstChild());
                break;
            default:
                break;
            }
        }
    }

    public void accept(Visitor v) throws Exception {
        v.visit(this);
    }

//    public void setOffSet(int offset) {
//        this.offset = offset;
//    }
//    
//    public int getOffSet() {        
//        return offset;
//    }
}
