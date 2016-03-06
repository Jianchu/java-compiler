package joosc;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import parser.ParseTree;
import parser.Parser;
import scanner.Scanner;
import scanner.Token;
import weeder.Weeder;
import ast.AST;
import ast.Visitor;
import environment.Disambiguation;
import environment.Hierarchy;
import environment.SymbolTable;

public class Joosc {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Joosc <filename>");
            System.exit(42);
        }

        System.exit(compile(args));
    }
    
    public static int compile(String[] args) {
        String objInterfacePath = System.getProperty("user.dir") + "/data/ObjInterface.java";
        List<String> augArgs = new LinkedList<String>();
        for (String arg : args) {
        	augArgs.add(arg);
        }
        augArgs.add(objInterfacePath);
        
        Scanner scanner = null;
        List<Token> tokens = null;
        
        File grammar;
        try {
            grammar = new File(System.getProperty("user.dir") + "/data/grammar.lr1");
		    List<AST> trees = new LinkedList<AST>();
		    for (String arg : augArgs) {
//		    	System.out.println(arg);
				File input = new File(arg);
				scanner = new Scanner(new FileReader(input));
				tokens = scanner.scan();
				Parser parser = new Parser(tokens, grammar);
				ParseTree parseTree = parser.parse();
				Weeder weeder = new Weeder(parseTree, input.getName().substring(0, input.getName().lastIndexOf('.')));
				weeder.weed();
				AST ast = new AST(parseTree);
				trees.add(ast);			
		    }
		    SymbolTable.buildEnvs(trees);
		    new Hierarchy(trees);
		    Disambiguation.disambiguate(trees);
		    
        } catch (Exception e) {
        	e.printStackTrace();
        	return 42;
        }
        return 0;
    }
    
    /**
     * write a version of main method that adds the standard library to arguments for testing.
     * @param args
     */

    public static int compileSTL(String[] args) {
        File javaLib = new File(System.getProperty("user.dir") + "/java/");
        List<String> sourceFiles = new ArrayList<String>(getLibFiles(javaLib));
        for (String arg : args) {
            sourceFiles.add(arg);
        }
        String[] sourceFilesInArray = new String[sourceFiles.size()];
        sourceFilesInArray = sourceFiles.toArray(sourceFilesInArray);
        return compile(sourceFilesInArray);
    }

    private static List<String> getLibFiles(File javaLib) {
        List<String> libFiles = new ArrayList<String>();
        File[] javaLibFiles = javaLib.listFiles();
        for (File javaLibFile : javaLibFiles) {
            if (javaLibFile.isDirectory()) {
                libFiles.addAll(getLibFiles(javaLibFile));
            } else {
                libFiles.add(javaLibFile.getAbsolutePath());
            }
        }
        return libFiles;
    }
}
