package ast;

public interface Visitor {
//	public int visit(ASTNode astNode);
	
	public boolean visit(ArrayAccess node);
	public boolean visit(ArrayCreationExpression node);
	public boolean visit(ArrayType node);
	public boolean visit(AssignmentExpression node);
	public boolean visit(Block node);
	public boolean visit(BodyDeclaration node);
	public boolean visit(BooleanLiteral node);
	public boolean visit(CastExpression node);
	public boolean visit(CharacterLiteral node);
	public boolean visit(ClassInstanceCreationExpression node);
	public boolean visit(CompilationUnit node);
	public boolean visit(ExpressionStatement node);
	public boolean visit(FieldAccessExpression node);
	public boolean visit(FieldAccess node);
	public boolean visit(ForStatement node);
	public boolean visit(IfStatement node);
	public boolean visit(ImportDeclaration node);
	public boolean visit(InfixExpression node);
	public boolean visit(InstanceofExpression node);
	public boolean visit(IntegerLiteral node);
	public boolean visit(MethodDeclaration node);
	public boolean visit(MethodInvocation node);
	public boolean visit(Modifier node);
	public boolean visit(NullLiteral node);
	public boolean visit(PackageDeclaration node);
	public boolean visit(PrefixExpression node);
	public boolean visit(PrimitiveType node);
	public boolean visit(QualifiedName node);
	// qualified type class not used
	public boolean visit(ReturnStatement node);
	public boolean visit(SimpleName node);
	public boolean visit(SimpleType node);
	public boolean visit(StringLiteral node);
	public boolean visit(ThisExpression node);
	public boolean visit(TypeDeclaration node);
	public boolean visit(VariableDeclaration node);
	public boolean visit(VariableDeclarationStatement node);
	public boolean visit(WhileStatement node);
}
