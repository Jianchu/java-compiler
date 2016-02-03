package ast;

import parser.ParseTree;
import scanner.Symbol;

public class ExpressionStatement extends Statement{

    private Expression statementExpression;

    public ExpressionStatement(ParseTree expressionStatementNode) {
        ParseTree StatementExpressionNode = ASTBuilder.findChild(
                expressionStatementNode,
                Symbol.StatementExpression);
        if (StatementExpressionNode != null) {
            statementExpression = ASTBuilder
                    .parseExpression(StatementExpressionNode);
        }
    }

    public Expression getStatementExpression() {
        return this.statementExpression;
    }

}
