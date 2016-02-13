package ast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;
import parser.Parser;
import scanner.Scanner;
import scanner.Token;

public class ASTPrinterVisitor implements Visitor{
	public int indent;
	public final int DISTANCE = 5;
	
	public ASTPrinterVisitor() {
		indent = 0;
	}
	
	private void printIndent(String s) {
		for (int i = 0; i < indent; i++) {
			System.out.print(" ");
		}
		System.out.println(s);
	}
	
	public void visit(CompilationUnit node) throws ASTException {
		printIndent(node.getClass().getSimpleName());
		
		indent += DISTANCE;
		node.pkg.accept(this);
		
		for (ImportDeclaration im : node.imports) {
				im.accept(this);
		}
		
		indent -= DISTANCE;
	}
	
	public void visit(PackageDeclaration node) {
		printIndent(node.getClass().getSimpleName());
	}
	
	public void visit(ImportDeclaration node) {
		printIndent(node.getClass().getSimpleName());
	}
	
	
	public void visit(ArrayAccess node) {
		
	}
	public void visit(ArrayCreationExpression node) {
		
	}
	public void visit(ArrayType node){
		
	}
	public void visit(AssignmentExpression node) {
		
	}
	public void visit(Block node){
		
	}
	public void visit(BodyDeclaration node) {
		
	}
	public void visit(BooleanLiteral node) {
		
	}
	public void visit(CastExpression node) {
		
	}
	public void visit(CharacterLiteral node) {
		
	}
	public void visit(ClassInstanceCreationExpression node) {
		
	}

	public void visit(ExpressionStatement node) {
		
	}
	public void visit(FieldAccessExpression node) {
		
	}
	public void visit(FieldAccess node) {
		
	}
	public void visit(ForStatement node) {
		
	}
	public void visit(IfStatement node) {
		
	}

	public void visit(InfixExpression node) {
		
	}
	public void visit(InstanceofExpression node) {
		
	}
	public void visit(IntegerLiteral node) {
		
	}
	public void visit(MethodDeclaration node) {
		
	}
	public void visit(MethodInvocation node){
		
	}
	public void visit(Modifier node) {
		
	}
	public void visit(NullLiteral node) {
		
	}

	public void visit(PrefixExpression node) {
		
	}
	public void visit(PrimitiveType node) {
		
	}
	public void visit(QualifiedName node) {
		
	}
	// qualified type class not used
	public void visit(ReturnStatement node) {
		
	}
	public void visit(SimpleName node) {
		
	}
	public void visit(SimpleType node) {
		
	}
	public void visit(StringLiteral node) {
		
	}
	public void visit(ThisExpression node) {
		
	}
	public void visit(TypeDeclaration node) {
		
	}
	public void visit(VariableDeclaration node) {
		
	}
	public void visit(VariableDeclarationStatement node) {
		
	}
	public void visit(WhileStatement node) {
		
	}

	
	public static void main(String[] args) throws Exception {
		File grammar = new File(System.getProperty("user.dir") + "/data/grammar.lr1");
		File f = new File(System.getProperty("user.dir") + "/test/testprogram/StringLiterals.java");
		Scanner scanner = new Scanner(new FileReader(f));
		List<Token> tokens = scanner.scan();
		Parser par = new Parser(tokens, grammar);
		ParseTree t = par.parse();
		AST ast = new AST(t);
		Visitor v = new ASTPrinterVisitor();
		ast.root.accept(v);
	}
}
