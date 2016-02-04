package ast;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.ASTException;

public abstract class Statement extends ASTNode {

    private static Statement statement = null;
    private static Expression statementExpression = null;

    public static Statement visitStatement(ParseTree statementNode)
            throws ASTException {
        ParseTree realStatement = statementNode.getChildren().get(0);
        switch (realStatement.getTokenType()) {
        case IfThenStatement:
        case IfThenElseStatement:
        case IfThenElseStatementNoShortIf:
            statement = new IfStatement(realStatement);
            break;
        case WhileStatement:
        case WhileStatementNoShortIf:
            statement = new WhileStatement(realStatement);
            break;
        case ForStatementNoShortIf:
        case ForStatement:
            statement = new ForStatement(realStatement);
            break;
        case StatementWithoutTrailingSubstatement:
            statement = getStatementNoTrailing(realStatement);
        }
        return statement;
        // return null for empty statement and "return;"
    }

    private static Statement getStatementNoTrailing(ParseTree statementNoTrailingNode) throws ASTException {
        Statement statement = null;
        ParseTree realStatement = statementNoTrailingNode.getChildren().get(0);
        switch(realStatement.getTokenType()) {
        case Block:
            statement = new Block(realStatement);
            break;
        case EmptyStatement:
            // return null?
            break;
        case ExpressionStatement:
            statement = new ExpressionStatement(realStatement);
            // call expression?
            break;
        case ReturnStatement:
            statement = new ReturnStatement(realStatement);
            break;
        }
        // return null for empty statement and "return;"
        return statement;
    }

    protected boolean checkNodeType(ParseTree node, Symbol symbol) {
        if (node.getTokenType().equals(symbol)) {
            return true;
        }
        return false;
    }
}
