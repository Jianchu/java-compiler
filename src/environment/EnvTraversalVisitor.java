package environment;

import ast.*;

public class EnvTraversalVisitor extends TraversalVisitor{
	Environment curr;
	
	@Override
	public void visit(TypeDeclaration node) throws Exception {
		curr = node.getEnvironment();
		super.visit(node);
	}
	
	@Override
	public void visit(MethodDeclaration node) throws Exception {
		curr = node.getEnvironment();
		super.visit(node);
	}
	
	@Override
	public void visit(Block node) throws Exception {
		curr = node.getEnvironment();
		super.visit(node);
	}
	
	@Override
	public void visit(VariableDeclarationStatement node) throws Exception {
		curr = node.getEnvironment();
		super.visit(node);
	}
	
	@Override
	public void visit(ForStatement node) throws Exception {
		curr = node.getEnvironment();
		super.visit(node);
	}
}
