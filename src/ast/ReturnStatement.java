package ast;

import parser.ParseTree;
import scanner.Symbol;

public class ReturnStatement extends Statement {

    private Expression returnExpression;

    public ReturnStatement(ParseTree returnNode) {
        ParseTree expressionNode = ASTBuilder.findChild(returnNode, Symbol.Expression);
        // don't check null intentionally
        this.returnExpression = ASTBuilder.parseExpression(expressionNode);
    }
}
