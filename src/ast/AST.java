package ast;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;
import parser.Parser;
import scanner.Scanner;
import scanner.Symbol;
import scanner.Token;

public class AST {
	public CompilationUnit root;
	
	public AST(ParseTree pt) throws ASTException {
		root = new CompilationUnit(pt);
	}

	public static void main(String[] args) {
		try {
			File grammar = new File(System.getProperty("user.dir") + "/data/grammar.lr1");
			File f = new File(System.getProperty("user.dir") + "/test/testprogram/StringLiterals.java");
			Scanner scanner = new Scanner(new FileReader(f));
			List<Token> tokens = scanner.scan();
			Parser par = new Parser(tokens, grammar);
			ParseTree t = par.parse();
			AST ast = new AST(t);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
