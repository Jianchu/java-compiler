package ast;

import exceptions.ASTException;

/**
 * All nodes must directly or indirectly extend ASTNode
 * @author zanel
 *
 */
public abstract class ASTNode {
	
	ASTNode parentNode = null;
	
	/**
	 * Not implemented by default.
	 * @param v
	 * @return
	 * @throws ASTException
	 */
	public abstract void accept(Visitor v) throws ASTException;
	
	public void setParent(ASTNode parent) {
		parentNode = parent;
	}
	
	public ASTNode getParent() {
		return parentNode;
	}
}
