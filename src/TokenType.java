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
	 * White Space
	 */
	SPACE, TAB, NEWLINE,
	
	/*
	 * Comments
	 */
	COMMENT, BLOCK_COMMENT,
	
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
	CHAR, STRING, INTEGER,
	
}
