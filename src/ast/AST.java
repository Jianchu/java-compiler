package ast;

public class AST {

	
	/*
	 * Literals
	 */
	private static class ValueLiteral extends ASTNode {
		public String value;
		public ValueLiteral(String val) {
			value = val;
		}
	}
	
	public static class IntegerLiteral extends ValueLiteral {
		public IntegerLiteral(String val) {
			super(val);
		}
	}
	
	public static class StringLiteral extends ValueLiteral {
		public StringLiteral(String val) { super(val); }
	}
	
	public static class BooleanLiteral extends ASTNode {
		public boolean value;
		public BooleanLiteral(boolean tf) {
			value = tf;
		}
	}
	
	public static class NullLiteral extends ASTNode{
	}
	
	
	
	public static void main(String[] args) {
		IntegerLiteral il = new AST.IntegerLiteral("12");
		
	}
	
}
