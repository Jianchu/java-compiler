package code_generation;

import ast.MethodDeclaration;
import ast.Modifier;
import ast.VariableDeclaration;
import environment.TraversalVisitor;

public class VariableOffSetVisitor extends TraversalVisitor{
    
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
        
        // compute offset for arguments
        int paramMaxOffSet = -node.parameters.size();
        for (int i = 0; i < node.parameters.size(); i++) {
        	// parameter offsets are negative numbers
        	node.addVarOffSet(node.parameters.get(i), paramMaxOffSet - i); 
        }
        
        if (node.body != null) {
            node.body.accept(this);
        }
    }
}
