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

    protected static Statement getStatement(ParseTree statementNode) {
        Statement statement;
        ParseTree realStatement = statementNode.getChildren().get(0);
        switch (realStatement.getTokenType()) {
        case IfThenStatement:
        case IfThenElseStatement:
        case IfThenElseStatementNoShortIf:
            break;

        case WhileStatement:
        case WhileStatementNoShortIf:
            break;
        case ForStatementNoShortIf:
        case ForStatement:
            break;
        case StatementWithoutTrailingSubstatement:

        }
        return null;
    }

    private static Statement getStatementNoTrailing(ParseTree statementNoTrailingNode) {
        Statement statement;
        ParseTree realStatement = statementNoTrailingNode.getChildren().get(0);
        switch(realStatement.getTokenType()) {
        case Block:
            break;
        case EmptyStatement:
            break;
        case ExpressionStatement:
            break;
        case ReturnStatement:
            break;
        }
        return null;
    }
}
