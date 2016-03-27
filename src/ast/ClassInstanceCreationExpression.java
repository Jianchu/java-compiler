package ast;

import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;

public class ClassInstanceCreationExpression extends Expression {
    public Type type;
    public List<Expression> arglist;    

    private MethodDeclaration constructor;
    
    public ClassInstanceCreationExpression(ParseTree pt) throws ASTException {
        List<ParseTree> subtrees = pt.getChildren();
        type = Type.parseType(subtrees.get(1));
        arglist = null;
        if (subtrees.size() > 4) {
            arglist = Expression.parseArglist(subtrees.get(3));
        }
    }
    
	public void accept(Visitor v) throws Exception {
		v.visit(this);
	}
	
	public void addConstructor(MethodDeclaration constructor) {
		this.constructor = constructor;
	}
	
	public MethodDeclaration getConstructor(MethodDeclaration constructor) throws Exception {
		if (this.constructor == null) {
			throw new Exception("Constructor not found.");
		}
		return constructor;
	}
	
}
