package ast;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.ASTException;

public abstract class Statement extends ASTNode {

    public static Statement visitStatement(ParseTree statementNode)
            throws ASTException {
        ParseTree realStatement = statementNode.getChildren().get(0);
        switch (realStatement.getTokenType()) {
        case IfThenStatement:
        case IfThenElseStatement:
        case IfThenElseStatementNoShortIf:
            return new IfStatement(realStatement);
        case WhileStatement:
        case WhileStatementNoShortIf:
            return new WhileStatement(realStatement);
        case ForStatementNoShortIf:
        case ForStatement:
            return new ForStatement(realStatement);
        case StatementWithoutTrailingSubstatement:
            // return null for empty statement and "return;"
            return getStatementNoTrailing(realStatement);
        }
        throw new ASTException();
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
