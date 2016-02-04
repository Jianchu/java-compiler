package ast;

import java.util.ArrayList;
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
    // maybe we should use Object rather than statement
    private List<Statement> statements;

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
                        if (checkNodeType(child, Symbol.LocalVariableDeclaration)) {
                            // do some for localvariabledeclaration
                            
                        } else if (checkNodeType(child, Symbol.Statement)) {
                            Statement statement = ASTBuilder.parseStatement(child);
                            statements.add(statement);
                        }
                    }
                } else {
                    queue.add(child);
                }

            }
        }
    }

    public List<Statement> getBlockStatements() {
        return this.statements;
    }
}

