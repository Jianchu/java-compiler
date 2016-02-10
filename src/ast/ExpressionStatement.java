package ast;

import exceptions.ASTException;
import parser.ParseTree;
import scanner.Symbol;

public class ExpressionStatement extends Statement{

    private Expression statementExpression;

    public ExpressionStatement(ParseTree expressionStatementNode) throws ASTException {
        ParseTree StatementExpressionNode = expressionStatementNode
                .findChild(Symbol.StatementExpression);
        if (StatementExpressionNode != null) {
            statementExpression = Expression
                    .parseExpression(StatementExpressionNode);
        }
    }

    public Expression getStatementExpression() {
        return this.statementExpression;
    }

}
