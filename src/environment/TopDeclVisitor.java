package environment;

import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Iterator;
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
		
		//Done: check for ambiguous names
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
				// names in samePackage is simple.
				curr.addSamePackage(cls, otherDecl);
			}
		}
		
		//imports
		// check for single type import collision. e.g java.util.List, java.awt.List
		Set<String> singleName = new TreeSet<String>();
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
				String simName = name.get(name.size() - 1);
				if (singleName.contains(simName)) {
					throw new NameException("single import name collides.");
				} else {
					singleName.add(simName);
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
		
		cu.attachEnvironment(table.currentScope());
		table.closeScope();
	}
	
	
	public void visit(TypeDeclaration typeDecl) throws Exception {
		table.currentScope().addType(typeDecl.id, typeDecl);
		
		// covers simple checks 1,2,3,4
		checkInterfaces(typeDecl);
		checkSuperClass(typeDecl);
		
		// environment for inherited fields and methods,
		// will filled later in Hierarchy class.
		table.openScope(Environment.EnvType.INHERIT);	
		
		// create environments for methods and fields
		table.openScope(Environment.EnvType.CLASS);
		
		for (BodyDeclaration bDecl : typeDecl.members) {
			bDecl.accept(this);
		}
		
		// attach the scope of fields and methods to TypeDeclaration.
		typeDecl.attachEnvironment(table.currentScope());
		table.closeScope();	// class

	}
	
	public void visit(FieldDeclaration fDecl) throws Exception {
		if (table.currentScope().fields.containsKey(fDecl.id)) {
			throw new NameException("field name repeated.");
		}
		table.currentScope().addField(fDecl.id, fDecl);
	}
	
	public void visit(MethodDeclaration mDecl) throws Exception {
		// link param types, needs to do this first so that name mangler can use the info
		for (VariableDeclaration vd : mDecl.parameters) {
			if (vd.type instanceof SimpleType) {
				Visitor tv = new TypeVisitor(table);
				vd.type.accept(tv);
			}
		}
		
		// link return type
		Type returnType = mDecl.returnType;
		if (returnType != null && ! (returnType instanceof PrimitiveType)) {
			Visitor tv = new TypeVisitor(table);
			returnType.accept(tv);
		}
		
		String mangledName = NameHelper.mangle(mDecl);
		if (table.currentScope().methods.containsKey(mangledName)) {
			throw new NameException("method signature repeated.");
		}
		
		table.currentScope().addMethod(NameHelper.mangle(mDecl), mDecl);
		table.openScope(Environment.EnvType.BLOCK);
		// extra scope for method parameters
		for (VariableDeclaration vd : mDecl.parameters) {
			vd.accept(this);
		}
		if (!mDecl.isAbstract) {
			mDecl.body.accept(this);
		}
		table.closeScope();
	}
	
	public void visit(Block block) throws Exception {
		table.openScope(Environment.EnvType.BLOCK);
		if (block.statements.size() > 0) {
			Statement first = block.statements.get(0);
			first.accept(this);
		}
		table.closeScope();
	}
	
	public void visit(VariableDeclarationStatement vds) throws Exception {
		table.openScope(Environment.EnvType.BLOCK);
		vds.varDeclar.accept(this);
		if (vds.hasNext()) {
			vds.next().accept(this);
		}
		table.closeScope();
	}
	
	public void visit(VariableDeclaration vd) throws Exception {
		// Done: type linking for types
		Visitor tv = new TypeVisitor(table);
		if (vd.type instanceof SimpleType && vd.type.getDeclaration() == null) {
			// if is simple type but has not been linked, type linking
			vd.type.accept(tv);
		}
		
		// check that a variable of the same name does not already exist
		if (table.currentScope().lookUpVariable(vd.id) != null) {
			throw new NameException("repeated variable name");
		}
		
		table.currentScope().addVariable(vd.id, vd);
	}
	
	private void checkSuperClass(TypeDeclaration typeDecl) throws Exception {
		if (typeDecl.superClass != null) {
			Type parent = typeDecl.superClass;
			if (parent instanceof PrimitiveType)
				throw new NameException("cannot extend primitive type.");
			
			Visitor tv = new TypeVisitor(table);
			parent.accept(tv);	// find declaration using type visitor
			
			TypeDeclaration parentDecl = parent.getDeclaration();
			if (parentDecl.id.equals(typeDecl.id)) {
				throw new NameException("class cannot extend itself.");
			} 
			if (parentDecl.isInterface){
				// simple check no.1: must be class
				throw new NameException("must extend a class.");
			}
			if (parentDecl.modifiers.contains(Modifier.FINAL)) {
				// simple check no.4: cannot extend final class
				throw new NameException("cannot extend final class");
			}
		}
	}
	
	private void checkInterfaces(TypeDeclaration typeDecl) throws Exception {
		Set<TypeDeclaration> seen = new HashSet<TypeDeclaration>();
		for (Type itf : typeDecl.interfaces) {
			if (itf instanceof PrimitiveType)
				throw new NameException("not an interface");
			
			Visitor tv = new TypeVisitor(table);
			itf.accept(tv);
			
			TypeDeclaration itfDecl = itf.getDeclaration();
			// simple check no.2
			if (!itfDecl.isInterface)
				throw new NameException("must implement an interface");
			
			// simple check no.3
			if (seen.contains(itfDecl)) 
				throw new NameException("cannot implement same interface twice");
		}
	}
	
    public static void main(String[] args) throws Exception {
        File grammar = new File(System.getProperty("user.dir")
                + "/data/grammar.lr1");
        File f = new File(System.getProperty("user.dir")+ "/test/testprogram/EnvironmentTestCase.java");
        
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
