package environment;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;


import ast.*;
import exceptions.NameException;

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
	 * @throws NameException 
	 */
	public void visit(CompilationUnit cu) throws NameException {
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
		
		
	}
	
	/**
	 * for finding files in same package. 
	 * DO NOT USE. package info has been saved to globalPackages
	 * @param pkg
	 * @return
	 */
	private Set<String> findSamePackage(String pkg) {
		Set<String> pkgFiles = new TreeSet<String>();
		for (String f : SymbolTable.getGlobal().keySet()) {
			if (f.startsWith(pkg) && !f.substring(pkg.length() + 1).contains(".")) {
				// look for classes that 
				// start with package name and has only a simple name after package name
				// because no nested classes
				pkgFiles.add(f);
			}
		}
		return pkgFiles;
	}
	
}
