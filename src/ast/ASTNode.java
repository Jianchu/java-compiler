package ast;

/**
 * All nodes must directly or indirectly extend ASTNode
 * @author zanel
 *
 */
public abstract class ASTNode {
	public int accept(Visitor v) {
		return v.visit(this);
	}
}
