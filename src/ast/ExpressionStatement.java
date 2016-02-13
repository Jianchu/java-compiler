package ast;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.ASTException;

public class ExpressionStatement extends Statement{

    public Expression statementExpression;

    public ExpressionStatement(ParseTree expressionStatementNode) throws ASTException {
        ParseTree StatementExpressionNode = expressionStatementNode
                .findChild(Symbol.StatementExpression);
        if (StatementExpressionNode != null) {
            statementExpression = Expression
                    .parseExpression(StatementExpressionNode);
        }
    }

    // public Expression getStatementExpression() {
    // return this.statementExpression;
    // }
	public void accept(Visitor v) throws ASTException {
		v.visit(this);
	}
}
