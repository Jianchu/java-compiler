package environment;

import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;


import ast.*;
import exceptions.ASTException;
import exceptions.NameException;
import parser.ParseTree;
import parser.Parser;
import scanner.Scanner;
import scanner.Token;

/**
 * responsible for constructing symbol table
 * @author zanel
 *
 */
public class TopDeclVisitor extends SemanticsVisitor {
	
	/**
	 * So that the current environment can be shared through different visitors.
	 * @param curr
	 */
	public TopDeclVisitor(SymbolTable syms)  {
		table = syms;
	}
	
	/**
	 * Build class environment
	 * @throws Exception 
	 */
	public void visit(CompilationUnit cu) throws Exception {
		table.openScope(Environment.EnvType.COMPILATION_UNIT);	// the first ever for this file
		Environment curr = table.currentScope();
		
		final Map<String, List<String>> pkgCls = SymbolTable.getAllPackages();
		final Map<String, TypeDeclaration> global = SymbolTable.getGlobal();
		
		//package files
		String pkg = "";
		if (cu.pkg != null) {
			pkg = cu.pkg.name.toString();
		}
		for (String cls : pkgCls.get(pkg)) {
			TypeDeclaration otherDecl = global.get(cls);
			TypeDeclaration thisDecl = null;
			if (cu.types.size() > 0) {
				thisDecl = cu.types.get(0);
			}
			if (thisDecl == null || cls != thisDecl.id) {
				// if the file from same package is not this one, add it.
				curr.addSamePackage(cls, otherDecl);
			}
		}
		
		//imports
		for (ImportDeclaration importDecl : cu.imports) {
			List<String> name = importDecl.name.getFullName();
			String nameStr = importDecl.name.toString();
			if (name.get(name.size() -1).equals("*")) {
				// import on demand
				List<String> qualifier = name.subList(0, name.size() - 1);
				String qualifierStr = String.join(".", name);
				List<String> clsList = pkgCls.get(qualifierStr);
				if (clsList == null) {
					// check that the package exist;
					throw new NameException("Import package not recoginzed: " + nameStr);
				}
				
				for (String cls : clsList) {
					curr.addImportOnDemand(cls, global.get(cls));
				}
				
			} else {
				// single import 
				TypeDeclaration decl = global.get(nameStr);
				if (decl == null) {
					throw new NameException("Import class name not recoginzed: " + nameStr);
				}
				curr.addSingleImport(nameStr, decl);
			}
		}
		
		// class or interface declaration
		for (TypeDeclaration typeDecl : cu.types) {
			
			// the for loop is redundant since there can only be one declaration.
			// but I've decided to do it in a more general way, when i made the types a list...
			typeDecl.accept(this);
		}
		
		table.closeScope();
	}
	
	
	public void visit(TypeDeclaration typeDecl) throws Exception {
		// TODO: super class or interfaces needs an environment
		
		// create environments for methods and fields
		table.openScope(Environment.EnvType.CLASS);
		
		for (BodyDeclaration bDecl : typeDecl.members) {
			bDecl.accept(this);
		}
		
		table.closeScope();
	}
	
	public void visit(FieldDeclaration fDecl) throws ASTException {
		System.out.println("chimi-fucking-changa not done yet");
	}
	
    public static void main(String[] args) throws Exception {
        File grammar = new File(System.getProperty("user.dir")
                + "/data/grammar.lr1");
        File f = new File(System.getProperty("user.dir")+ "/test/testprogram/EnvironmentTest.java");
        
        Scanner scanner = new Scanner(new FileReader(f));
        List<Token> tokens = scanner.scan();
        Parser par = new Parser(tokens, grammar);
        ParseTree t = par.parse();
        // Weeder wee = new Weeder(t, "StringLiterals");
        // wee.weed();
        AST ast = new AST(t);
        List<AST> allTrees = new LinkedList<AST>();
        allTrees.add(ast);
        SymbolTable.buildGlobal(allTrees);
        SymbolTable table = new SymbolTable();
        Visitor v = new SemanticsVisitor();
        ast.root.accept(v);
    }
	
	
}
