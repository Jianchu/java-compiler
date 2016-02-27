import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import ast.AST;
import environment.SymbolTable;
import parser.ParseTree;
import parser.Parser;
import scanner.Scanner;
import scanner.Token;
import weeder.Weeder;

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
    
}
