package ast;

import java.util.List;


/**
 * A block is also a statement for nested blocks.
 * @author zanel
 *
 */
public class Block extends Statement {
	// a block is just a list of statements
	List<Statement> statements;
}
