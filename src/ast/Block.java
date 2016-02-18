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
    private Statement last;
    
    public Block(ParseTree blockNode) throws ASTException {
        statements = new ArrayList<Statement>();
        parseBlockStatement(blockNode);
    }

    private void parseBlockStatement(ParseTree pt) throws ASTException {
    	Statement s; // for adding next
		
    	// write search with recursion
    	for (ParseTree child : pt.getChildren()) {
    		switch(child.getTokenType()) {
    		case BlockStatements:
    			parseBlockStatement(child);
    			break;
    		case BlockStatement:
    			parseBlockStatement(child);
    			break;
    		case LocalVariableDeclarationStatement:
    			s = new VariableDeclarationStatement(child);
    			// next is for use in TopDeclVisitor
    			if (last != null)
    				last.setNext(s);
    			last = s;
    			statements.add(s);
    			break;
    		case Statement:
    			s = Statement.parseStatement(child);
    			if (last != null)
    				last.setNext(s);
    			last = s;
    			
    			statements.add(Statement.parseStatement(child));
    			break;
    		}
    	}
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

