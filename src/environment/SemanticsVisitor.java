package environment;

import ast.*;

import exceptions.ASTException;
/**
 * See section 8.5.3 of Crafting a Compiler
 * semantic checking. uses both TopDeclVisitor and TypeVisitor
 * @author zanel
 *
 */
public class SemanticsVisitor implements Visitor {
	public void visit(CompilationUnit node) throws ASTException {}

	
	/*
	 * Declaration
	 */
	public void visit(FieldDeclaration node) throws ASTException {}
	public void visit(ImportDeclaration node) throws ASTException {}
	public void visit(MethodDeclaration node) throws ASTException {}
	public void visit(PackageDeclaration node) throws ASTException {}
	public void visit(TypeDeclaration node) throws ASTException {}

	
	/*
	 * Statement
	 */
	public void visit(Block node) throws ASTException {}
	public void visit(ExpressionStatement node) throws ASTException {}
	public void visit(ForStatement node) throws ASTException {}
	public void visit(IfStatement node) throws ASTException {}
	public void visit(ReturnStatement node) throws ASTException {}
	public void visit(VariableDeclaration node) throws ASTException {}
	public void visit(VariableDeclarationStatement node) throws ASTException {}
	public void visit(WhileStatement node) throws ASTException {}
	
	/*
	 * Type
	 */
	public void visit(ArrayType node) throws ASTException {}
	public void visit(PrimitiveType node) throws ASTException {}
	public void visit(SimpleType node) throws ASTException {}
	
	/*
	 * Expression
	 */
	public void visit(ArrayAccess node)  throws ASTException {}
	public void visit(ArrayCreationExpression node) throws ASTException {}
	public void visit(AssignmentExpression node) throws ASTException {}
	public void visit(BooleanLiteral node) throws ASTException {}
	public void visit(CastExpression node) throws ASTException {}
	public void visit(CharacterLiteral node) throws ASTException {}
	public void visit(ClassInstanceCreationExpression node) throws ASTException {}
	public void visit(FieldAccess node) throws ASTException {}
	public void visit(InfixExpression node) throws ASTException {}
	public void visit(InstanceofExpression node) throws ASTException {}
	public void visit(IntegerLiteral node) throws ASTException {}
	public void visit(MethodInvocation node) throws ASTException {}
	public void visit(NullLiteral node) throws ASTException {}
	public void visit(PrefixExpression node) throws ASTException {}
	public void visit(StringLiteral node) throws ASTException {}
	public void visit(ThisExpression node) throws ASTException {}
	public void visit(VariableDeclarationExpression node) throws ASTException {}

	/*
	 * Name
	 */
	public void visit(SimpleName node) throws ASTException {}
	public void visit(QualifiedName node) throws ASTException {}
	public void visit(Modifier node) throws ASTException {}
}
