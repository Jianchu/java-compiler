package static_analysis;

import java.util.List;

import ast.*;
import environment.TraversalVisitor;
import exceptions.VariableException;

public class VariableAnalysis extends TraversalVisitor{
	VariableDeclaration currVd = null;
	
	
	@Override
    public void visit(MethodDeclaration node) throws Exception {
        for (Modifier mo : node.modifiers) {
            mo.accept(this);
        }
        if (!node.isConstructor) {
            // check for void
            if (node.returnType != null) {
                node.returnType.accept(this);
            }
        }
        // do not visit parameter declarations because they do not need to be initialized
        if (node.body != null) {
            node.body.accept(this);
        }
    }
	
	@Override
	public void visit(VariableDeclaration node) throws Exception {
		node.type.accept(this);	// useless but why not
		if (node.initializer == null)
			throw new VariableException("Variable not initialized: " + node.id);
		
		currVd = node;
		node.initializer.accept(this);
		currVd = null;
	}
	
	@Override
	public void visit(SimpleName node) throws Exception{
		if (currVd != null && node.getDeclaration() == currVd) {
			throw new VariableException("Variable occurred in its own initializer: " + node);
		}
	}
	
	@Override
	public void visit(QualifiedName node) throws Exception {
		for (Name n : node.getPrefixList()) {
			if (currVd != null && n.getDeclaration() == currVd) {
				throw new VariableException("Variable occured in its own initializer: " + n);
			}
		}
	}
	
	public static void check(List<AST> trees) throws Exception {
		for (AST ast : trees) {
			Visitor vav = new VariableAnalysis();
			ast.root.accept(vav);
		}
		
	}
}
