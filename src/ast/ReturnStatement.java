package ast;

import parser.ParseTree;
import scanner.Symbol;

public class ReturnStatement extends Statement {

    private Expression returnExpression = null;

    public ReturnStatement(ParseTree returnNode) {
        ParseTree expressionNode = returnNode.findChild(Symbol.Expression);
        // don't check null intentionally
        this.returnExpression = ASTBuilder.parseExpression(expressionNode);
    }
}
