package ast;

import environment.Environment;
import exceptions.ASTException;

/**
 * All nodes must directly or indirectly extend ASTNode
 * @author zanel
 *
 */
public abstract class ASTNode {

    ASTNode parentNode = null;
    Environment env = null;
    String code = "";

    /**
     * Not implemented by default.
     * 
     * @param v
     * @return
     * @throws ASTException
     */
    public abstract void accept(Visitor v) throws Exception;

    public void setParent(ASTNode parent) {
        parentNode = parent;
    }

    public ASTNode getParent() {
        return parentNode;
    }

    public void attachEnvironment(Environment env) {
        this.env = env;
    }

    public Environment getEnvironment() {
        return env;
    }

    public void attachCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}
