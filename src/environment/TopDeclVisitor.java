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
import exceptions.AbstractMethodException;
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
public class TopDeclVisitor extends TraversalVisitor {
	SymbolTable table;

	/**
	 * So that the current environment can be shared through different visitors.
	 * @param curr
	 */
	public TopDeclVisitor()  {
		table = new SymbolTable();;
	}
	
	/**
	 * Build class environment
	 * @throws Exception 
	 */
	public void visit(CompilationUnit cu) throws Exception {
		table.openScope(Environment.EnvType.COMPILATION_UNIT);	// the first ever for this file
		Environment curr = table.currentScope();
		
		final Map<String, List<String>> pkgCls = SymbolTable.getAllPackages();
		final Map<String, TypeDeclaration> globalEnv = SymbolTable.getGlobal();
		
		//Done: check for ambiguous names
		//package files
		String pkg = "";
		if (cu.pkg != null) {
			checkPkgName(cu.pkg.name);
			pkg = cu.pkg.name.toString();
		}
		for (String cls : pkgCls.get(pkg)) {
			TypeDeclaration otherDecl = globalEnv.get(cls);
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
		Set<String> seen = new TreeSet<String>();
		for (ImportDeclaration importDecl : cu.imports) {
			List<String> name = importDecl.name.getFullName();
			String nameStr = importDecl.name.toString();
			if (importDecl.onDemand){
				// import on demand
				importOnDemand(name);
				
			} else {
				// single import 
				TypeDeclaration decl = globalEnv.get(nameStr);
				if (decl == null) {
					throw new NameException("Import class name not recoginzed: " + nameStr);
				}
				String simName = name.get(name.size() - 1);
				if (singleName.contains(simName) &&
						! curr.singleImports.containsKey(nameStr)) {
					// if class name the same, but fully qualified name different.
					throw new NameException("single import name collides.");
				} else if (cu.types.size() > 0 && simName.equals(cu.types.get(0).id) && !nameStr.equals(cu.types.get(0).getFullName())) {
					throw new NameException("single import name collides with type name.");
				}
				else {
					singleName.add(simName);
				}
				
				curr.addSingleImport(nameStr, decl);
			}
		}
		
		// import java.lang automatically
		List<String> lang = new LinkedList<String>();
		lang.add("java"); lang.add("lang");
		importOnDemand(lang);
//		final String lang = "java.lang";
//		if (pkgCls.get(lang) != null) {
//			for (String cls : pkgCls.get(lang)) {
//				String fn =  cls;
//				if (curr.singleImports.get(fn) == null)
//					curr.addSingleImport(fn, globalEnv.get(fn));
//			}
//		} else {
//			// TODO: maybe throw exception.
//			System.err.println("could not find java.lang");
//		}
		
		
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
		// will be filled later in Hierarchy class.
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
//		System.out.println(mDecl.id);
		// link param types, needs to do this first so that name mangler can use the info
		for (VariableDeclaration vd : mDecl.parameters) {
			Visitor tv = new TypeVisitor(table);
			vd.type.accept(tv);
		}
		
		// link return type
		Type returnType = mDecl.returnType;
		if (returnType != null) {
			Visitor tv = new TypeVisitor(table);
			returnType.accept(tv);
		}
		
		String mangledName = NameHelper.mangle(mDecl);
		if (!mDecl.isConstructor) {
			if (table.currentScope().methods.containsKey(mangledName)) {
				throw new NameException("method signature repeated.");
			}
		
			table.currentScope().addMethod(NameHelper.mangle(mDecl), mDecl);
		} else {
			if (table.currentScope().constructors.containsKey(mangledName)) {
				throw new NameException("method signature repeated.");
			}
			table.currentScope().addConstructor(NameHelper.mangle(mDecl), mDecl);
		}
		
		table.openScope(Environment.EnvType.BLOCK);
		// extra scope for method parameters
		for (VariableDeclaration vd : mDecl.parameters) {
			vd.accept(this);
		}
		if (!mDecl.isAbstract) {
			if (mDecl.body != null)
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
	
	public void visit(VariableDeclarationExpression vde) throws Exception {
		table.openScope(Environment.EnvType.BLOCK);
		vde.variableDeclaration.accept(this);
		table.closeScope();
	}
	
	public void visit(VariableDeclaration vd) throws Exception {
		// Done: type linking for types
		Visitor tv = new TypeVisitor(table);
		if (vd.type instanceof SimpleType || vd.type instanceof ArrayType) {
			// if is simple type but has not been linked, type linking
			vd.type.accept(tv);
		}
		
		// check that a variable of the same name does not already exist
		if (table.currentScope().lookUpVariable(vd.id) != null) {
			throw new NameException("repeated variable name");
		}
		
		table.currentScope().addVariable(vd.id, vd);
	}
	
	/*
	 * Other Statement
	 */
	public void visit(ExpressionStatement node) throws Exception {
		node.statementExpression.accept(this);
		visitNextStatement(node);
	}
	public void visit(ForStatement node) throws Exception {
		
		if (node.forInit != null)
			node.forInit.accept(this);
		if (node.forBody != null)
			node.forBody.accept(this);
		visitNextStatement(node);
	}
	public void visit(IfStatement node) throws Exception {
		node.ifStatement.accept(this);
		if (node.elseStatement != null)
			node.elseStatement.accept(this);
		visitNextStatement(node);
	}
	public void visit(ReturnStatement node) throws Exception {
		node.returnExpression.accept(this);
		visitNextStatement(node);
	}
	public void visit(WhileStatement node) throws Exception {
		node.whileStatement.accept(this);
		visitNextStatement(node);
	}
	
	private void visitNextStatement(Statement node) throws Exception {
		if (node.hasNext()) {
			node.next().accept(this);
		}
	}
	
	/*
	 * Expressions
	 */
	public void visit(ClassInstanceCreationExpression node) throws Exception {
		Visitor tv = new TypeVisitor(table);
		node.type.accept(tv);
	}
	
	public void visit(MethodInvocation node) throws Exception {
		node.expr.accept(this);
	}
	
	public void visit(CastExpression node) throws Exception {
		Visitor tv = new TypeVisitor(table);
		// TODO: Deal with type linking for cast expression somewhere else
//		node.type.accept(tv);
	}
	
	public void visit(ArrayCreationExpression node) throws Exception {
		Visitor tv = new TypeVisitor(table);
		node.type.accept(tv);
	}
	
	public void visit(InstanceofExpression node) throws Exception {
		Visitor tv = new TypeVisitor(table);
		node.type.accept(tv);
	}
	
	
	/*
	 * Helper
	 */
	
	private void checkSuperClass(TypeDeclaration typeDecl) throws Exception {
		if (typeDecl.superClass != null) {
			Type parent = typeDecl.superClass;
			if (parent instanceof PrimitiveType)
				throw new NameException("cannot extend primitive type.");
			
			Visitor tv = new TypeVisitor(table);
			parent.accept(tv);	// find declaration using type visitor
			
			TypeDeclaration parentDecl = parent.getDeclaration();
			if (parentDecl == typeDecl) {
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
			if (!itfDecl.isInterface) {
				throw new NameException("must implement an interface");
			}
			// simple check no.3
			if (seen.contains(itfDecl)) 
				throw new NameException("cannot implement same interface twice");
			
			seen.add(itfDecl);
		}
	}
	
	private void importOnDemand(List<String> name) throws NameException {
		String qualifierStr = String.join(".", name);
		boolean found = false;
		List<String> clsList = new LinkedList<String>(); 
		for (String pkgName : table.getAllPackages().keySet()) {
			if (pkgName.startsWith(qualifierStr) 
					&& (pkgName.length() == qualifierStr.length() 
							|| pkgName.charAt(qualifierStr.length()) == '.')){
				found = true;
				clsList.addAll(table.getAllPackages().get(pkgName));
			}
		}
		
		if (!found)
			throw new NameException("Import package name not recognized: " + qualifierStr);
		
		for (String cls : clsList) {
			table.currentScope().addImportOnDemand(cls, table.getGlobal().get(cls));
		}
	}
	
	private void checkPkgName(Name pkg) throws NameException {
		String pkgStr = pkg.toString();
		for (String clsStr : table.getGlobal().keySet()) {
//			System.out.println(pkgStr + " : " + clsStr);
			if (clsStr.contains(".") && pkgStr.startsWith(clsStr) 
					&& (pkgStr.length() == clsStr.length() 
						|| pkgStr.charAt(clsStr.length()) == '.')) {
				throw new NameException("package name conflicts with class name");
			}
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
        Visitor v = new TopDeclVisitor();
        ast.root.accept(v);
    }
	
	
}
