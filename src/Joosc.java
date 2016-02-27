import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import parser.ParseTree;
import parser.Parser;
import scanner.Scanner;
import scanner.Token;
import weeder.Weeder;
import ast.AST;
import environment.SymbolTable;

public class Joosc {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Joosc <filename>");
            System.exit(42);
        }
	
        Scanner scanner = null;
        List<Token> tokens = null;
        File grammar;
        try {
            grammar = new File(System.getProperty("user.dir") + "/data/grammar.lr1");
		    List<AST> trees = new LinkedList<AST>();
		    for (String arg : args) {
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
		    
        } catch (Exception e) {
            System.err.println(e);
            System.exit(42);
        }
        System.exit(0);

    }
    
    /**
     * write a version of main method that adds the standard library to arguments for testing.
     * @param args
     */
    // May be use File[] as param?
    public static void mainSTL(File[] args) {
        File javaLib = new File(System.getProperty("user.dir") + "/java/");
        List<File> sourceFiles = new ArrayList<File>(getLibFiles(javaLib));
        sourceFiles.addAll(Arrays.asList(args));
        Scanner scanner = null;
        List<Token> tokens = null;
        try {
            File grammar = new File(System.getProperty("user.dir") + "/data/grammar.lr1");
            List<AST> trees = new LinkedList<AST>();
            for (File sourcefile : sourceFiles) {
                System.out.println(sourcefile);
                scanner = new Scanner(new FileReader(sourcefile));
                tokens = scanner.scan();
                Parser parser = new Parser(tokens, grammar);
                ParseTree parseTree = parser.parse();
                Weeder weeder = new Weeder(parseTree, sourcefile.getName().substring(0, sourcefile.getName().lastIndexOf('.')));
                weeder.weed();
                AST ast = new AST(parseTree);
                trees.add(ast);
            }
            SymbolTable.buildEnvs(trees);
            
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private static List<File> getLibFiles(File javaLib) {
        List<File> libFiles = new ArrayList<File>();
        File[] javaLibFiles = javaLib.listFiles();
        for (File javaLibFile : javaLibFiles) {
            if (javaLibFile.isDirectory()) {
                libFiles.addAll(getLibFiles(javaLibFile));
            } else {
                libFiles.add(javaLibFile);
            }
        }
        return libFiles;
    }
}
