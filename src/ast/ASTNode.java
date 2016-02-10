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
	public int accept(Visitor v) throws ASTException {
		throw new ASTException("method not implemented");
	}
	
	public void setParent(ASTNode parent) {
		parentNode = parent;
	}
	
	public ASTNode getParent() {
		return parentNode;
	}
}
