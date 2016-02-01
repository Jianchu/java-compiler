package ast;

import java.util.List;

public class Block extends ASTNode {
	// a block is just a list of statements
	List<Statement> statements;
}
