package environment;

import java.util.List;

import ast.*;

public class Disambiguation extends TraversalVisitor{
	
	public void visit(Type node) {
		// do nothing. Types have already been processed
	}
	
	public void visit(MethodInvocation node) throws Exception {
	}
	
	public void visit(ArrayAccess node) throws Exception {
		node.array.accept(this);
		node.index.accept(this);
	}
	

	
}
