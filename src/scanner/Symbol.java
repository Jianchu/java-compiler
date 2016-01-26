package scanner;
/**
 * Enum Type for Token types.
 * @author zanel
 *
 */
public enum Symbol {
	/*
	 * Identifiers
	 */
	ID,

	/*
	 * White Space -- DEPRECATED
	 * SPACE, TAB, NEWLINE,
	 */

	/*
	 * Comments -- DEPRECATED
	 * COMMENT, BLOCK_COMMENT,
	 */


	/*
	 * Operators 
	 */
	// =, ==
	ASSIGN, EQUAL,
	// >, >>, >>>, >=, >>=, >>>= (unsigned right shift assignment)
	RANGLE, DBRANGLE, TPRANGLE, GEQ, RSHIFT_EQ, URSHIFT_EQ,
	// <, <<, <=, <<=
	LANGLE, DBLANGLE, LEQ, LSHIFT_EQ,
	// ~
	BIT_COMP,
	// !, !=
	NOT, NEQ,
	// ?, :
	QUESTION, COLON, 
	// &, &&, &=
	BITAND, AND, AND_EQ,
	// |, ||, |=
	BITOR, LOR, OR_EQ,
	// ^, ^=
	EXOR, EXOR_EQ,
	// +, ++, -, --, +=, -=
	PLUS, INCREMENT, MINUS, DECREMENT, PLUS_EQ, MINUS_EQ,
	// *, *=, /, /=, 
	STAR, STAR_EQ, SLASH, SLASH_EQ,
	// %, %=
	MOD, MOD_EQ,

	/*
	 * Separators
	 * ( ) { } [ ] ; , .
	 */
	LPAREN, RPAREN, LBRACE, RBRACE, LBRACKET, RBRACKET, SEMICOLON, COMMA, DOT,

	/*
	 * Literals
	 */
	CHARACTER, STRING, DECIMAL, NULL, //Null is also in keywords
	//Boolean Literals stored by value.
	TRUE, FALSE,
	
	
	/*
	 * Keywords
	 * 
	 * Some of these do not need to be implemented, but based on what 
	 * Ondrej said in class they still need to be recognized as keyword
	 * for this to be valid Java. In parser we should take care to stop
	 * unimplemented keyword.
	 * 
	 */
	ABSTRACT,
	BOOLEAN,
	BREAK,
	BYTE,
	CASE,
	CATCH,
	CHAR,
	CLASS,
	CONST,
	CONTINUE,
	DEFAULT,
	DO,
	DOUBLE,
	ELSE,
	EXTENDS,
	FINAL,
	FINALLY,
	FLOAT,
	FOR,
	GOTO,
	IF,
	IMPLEMENTS,
	IMPORT,
	INSTANCEOF,
	INT,
	INTERFACE,
	LONG,
	NATIVE,
	NEW,
	PACKAGE,
	PRIVATE,
	PROTECTED,
	PUBLIC,
	RETURN,
	SHORT,
	STATIC,
	STRICTFP,
	SUPER,
	SWITCH,
	SYNCHRONIZED,
	THIS,
	THROW,
	THROWS,
	TRANSIENT,
	TRY,
	VOID,
	VOLATILE,
	WHILE,
	
	/*
	 * Non-terminals
	 */
	AbstractMethodDeclaration,
	AdditiveExpression,
	AndExpression,
	ArgumentList,
	ArrayAccess,
	ArrayCreationExpression,
	ArrayType,
	Assignment,
	AssignmentExpression,
	AssignmentOperator,
	Block,
	BlockStatement,
	BlockStatements,
	BooleanLiteral,
	CastExpression,
	ClassBody,
	ClassBodyDeclaration,
	ClassBodyDeclarations,
	ClassDeclaration,
	ClassInstanceCreationExpression,
	ClassMemberDeclaration,
	ClassOrInterfaceType,
	ClassType,
	CompilationUnit,
	ConditionalAndExpression,
	ConditionalExpression,
	ConditionalOrExpression,
	ConstructorBody,
	ConstructorDeclaration,
	ConstructorDeclarator,
	DimExpr,
	Dims,
	EmptyStatement,
	EqualityExpression,
	ExclusiveOrExpression,
	Expression,
	ExpressionStatement,
	ExtendsInterfaces,
	FieldAccess,
	FieldDeclaration,
	ForInit,
	ForStatement,
	ForStatementNoShortIf,
	ForUpdate,
	FormalParameter,
	FormalParameterList,
	IfThenElseStatement,
	IfThenElseStatementNoShortIf,
	IfThenStatement,
	ImportDeclaration,
	ImportDeclarations,
	InclusiveOrExpression,
	IntegerLiteral,
	IntegralType,
	InterfaceBody,
	InterfaceDeclaration,
	InterfaceMemberDeclaration,
	InterfaceMemberDeclarations,
	InterfaceType,
	InterfaceTypeList,
	Interfaces,
	LeftHandSide,
	Literal,
	LocalVariableDeclaration,
	LocalVariableDeclarationStatement,
	MethodBody,
	MethodDeclaration,
	MethodDeclarator,
	MethodHeader,
	MethodInvocation,
	Modifier,
	Modifiers,
	MultiplicativeExpression,
	Name,
	NullLiteral,
	NumericType,
	PackageDeclaration,
	PostfixExpression,
	Primary,
	PrimaryNoNewArray,
	PrimitiveType,
	QualifiedName,
	ReferenceType,
	RelationalExpression,
	ReturnStatement,
	S,
	ShiftExpression,
	SimpleName,
	SingleTypeImportDeclaration,
	Statement,
	StatementExpression,
	StatementExpressionList,
	StatementNoShortIf,
	StatementWithoutTrailingSubstatement,
	Super,
	Type,
	TypeDeclaration,
	TypeDeclarations,
	TypeImportOnDemandDeclaration,
	UnaryExpression,
	UnaryExpressionNotPlusMinus,
	VariableDeclarator,
	VariableDeclaratorId,
	VariableInitializer,
	VariableInitializers,
	WhileStatement,
	WhileStatementNoShortIf
}
