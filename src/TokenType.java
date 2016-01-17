/**
 * Enum Type for Token types.
 * @author zanel
 *
 */
public enum TokenType {
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
	// <, <=
	LANGLE, LEQ,
	// !, !=
	NOT, NEQ,
	// ?, :
	QUESTION, COLON, 
	// &, NOTE: && is missing for now
	BITAND, 
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
	CHARACTER, STRING, INTEGER, NULL, //Null is also in keywords

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
	WHILE
}
