package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.ASTException;


/**
 * A block is also a statement for nested blocks.
 */
public class Block extends Statement {
    // a block is just a list of statements
    public List<Statement> statements;

    public Block(ParseTree blockNode) throws ASTException {
        statements = new ArrayList<Statement>();
        visitBlockStatement(blockNode);
    }

    private void visitBlockStatement(ParseTree blockNode) throws ASTException {
        Queue<ParseTree> queue = new LinkedList<ParseTree>();
        queue.add(blockNode);
        while (!queue.isEmpty()) {
            ParseTree currentNode = (ParseTree) queue.remove();
            for (ParseTree child : currentNode.getChildren()) {
                if (checkNodeType(child, Symbol.BlockStatement)) {
                    for (ParseTree blockStatementChild : child.getChildren()) {
                        if (checkNodeType(blockStatementChild,
                                Symbol.LocalVariableDeclarationStatement)) {
                            statements.add(new VariableDeclarationStatement(blockStatementChild));
                        } else if (checkNodeType(blockStatementChild, Symbol.Statement)) {
                            Statement statement = Statement
                                    .parseStatement(blockStatementChild);
                            statements.add(statement);
                        }
                    }
                } else {
                    queue.add(child);
                }

            }
        }
        Collections.reverse(statements);
    }

    // public List<Statement> getBlockStatements() {
    // return this.statements;
    // }
    
	public void accept(Visitor v) throws Exception {
		v.visit(this);
	}
}

