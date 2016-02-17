package environment;

import java.util.Stack;

import ast.*;

/**
 * See section 8.5.3 of Crafting a Compiler
 * semantic checking. uses both TopDeclVisitor and TypeVisitor
 * @author zanel
 *
 */
public class SemanticsVisitor implements Visitor {
	SymbolTable table;
	
	public SemanticsVisitor() {
		table = new SymbolTable();
	}
	
	public void visit(CompilationUnit node) throws Exception {
		TopDeclVisitor declVisitor = new TopDeclVisitor(table);
		node.accept(declVisitor);
	}

	
	/*
	 * Declaration
	 */
	public void visit(FieldDeclaration node) throws Exception {}
	public void visit(ImportDeclaration node) throws Exception {}
	public void visit(MethodDeclaration node) throws Exception {}
	public void visit(PackageDeclaration node) throws Exception {}
	public void visit(TypeDeclaration node) throws Exception {}

	
	/*
	 * Statement
	 */
	public void visit(Block node) throws Exception {}
	public void visit(ExpressionStatement node) throws Exception {}
	public void visit(ForStatement node) throws Exception {}
	public void visit(IfStatement node) throws Exception {}
	public void visit(ReturnStatement node) throws Exception {}
	public void visit(VariableDeclaration node) throws Exception {}
	public void visit(VariableDeclarationStatement node) throws Exception {}
	public void visit(WhileStatement node) throws Exception {}
	
	/*
	 * Type
	 */
	public void visit(ArrayType node) throws Exception {}
	public void visit(PrimitiveType node) throws Exception {}
	public void visit(SimpleType node) throws Exception {}
	
	/*
	 * Expression
	 */
	public void visit(ArrayAccess node)  throws Exception {}
	public void visit(ArrayCreationExpression node) throws Exception {}
	public void visit(AssignmentExpression node) throws Exception {}
	public void visit(BooleanLiteral node) throws Exception {}
	public void visit(CastExpression node) throws Exception {}
	public void visit(CharacterLiteral node) throws Exception {}
	public void visit(ClassInstanceCreationExpression node) throws Exception {}
	public void visit(FieldAccess node) throws Exception {}
	public void visit(InfixExpression node) throws Exception {}
	public void visit(InstanceofExpression node) throws Exception {}
	public void visit(IntegerLiteral node) throws Exception {}
	public void visit(MethodInvocation node) throws Exception {}
	public void visit(NullLiteral node) throws Exception {}
	public void visit(PrefixExpression node) throws Exception {}
	public void visit(StringLiteral node) throws Exception {}
	public void visit(ThisExpression node) throws Exception {}
	public void visit(VariableDeclarationExpression node) throws Exception {}

	/*
	 * Name
	 */
	public void visit(SimpleName node) throws Exception {}
	public void visit(QualifiedName node) throws Exception {}
	public void visit(Modifier node) throws Exception {}
}
