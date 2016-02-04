package ast;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.ASTException;

public abstract class Statement extends ASTNode {


    protected boolean checkNodeType(ParseTree node, Symbol symbol) {
        if (node.getTokenType().equals(symbol)) {
            return true;
        }
        return false;
    }

    protected static Statement getStatement(ParseTree statementNode) throws ASTException {
        Statement statement;
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
        // should return null?
        return null;
    }

    private static Statement getStatementNoTrailing(ParseTree statementNoTrailingNode) throws ASTException {
        Statement statement;
        ParseTree realStatement = statementNoTrailingNode.getChildren().get(0);
        switch(realStatement.getTokenType()) {
        case Block:
            statement = new Block(realStatement);
            break;
        case EmptyStatement:
            // return null?
            break;
        case ExpressionStatement:
            // call expression?
            break;
        case ReturnStatement:
            statement = new ReturnStatement(realStatement);
            break;
        }
        // should return null?
        return null;
    }
}
