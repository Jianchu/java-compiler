package ast;

import parser.ParseTree;
import scanner.Symbol;

public abstract class Statement extends ASTNode {

    protected boolean checkNodeType(ParseTree node, Symbol symbol) {
        if (node.getTokenType().equals(symbol)) {
            return true;
        }
        return false;
    }

}
