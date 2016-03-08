package environment;

import ast.Block;
import ast.ForStatement;
import ast.MethodDeclaration;
import ast.TypeDeclaration;
import ast.VariableDeclarationStatement;

public class EnvTraversalVisitor extends TraversalVisitor{
	Environment curr = null;
	Environment last = null;
	
	@Override
	public void visit(TypeDeclaration node) throws Exception {
        // System.out.println(node.getFullName());
		curr = node.getEnvironment();
		super.visit(node);
	}
	
	@Override
	public void visit(MethodDeclaration node) throws Exception {
		last = curr;
		curr = node.getEnvironment();
		super.visit(node);
		curr = last;
	}
	
	@Override
	public void visit(Block node) throws Exception {
		last = curr;
		curr = node.getEnvironment();
		super.visit(node);
		curr = last;
	}
	
	@Override
	public void visit(VariableDeclarationStatement node) throws Exception {
		last = curr;
		curr = node.getEnvironment();
		super.visit(node);
		curr = last;
	}
	
	@Override
	public void visit(ForStatement node) throws Exception {
		last = curr;
		curr = node.getEnvironment();
		super.visit(node);
		curr = last;
	}
}
