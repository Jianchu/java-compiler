package parser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import scanner.Scanner;
import scanner.Token;

public class ParserTest {
	File grammar;
	
	@Before
	public void setUp() {
		grammar = new File(System.getProperty("user.dir") + "/data/gen/grammar.lr1");
	}
	
	@Test
	public void testSmallGrammar() throws Exception {
		Scanner scanner = new Scanner(new StringReader("i = 1"));
		List<Token> tokens = scanner.scan();
		File parseIn = new File(System.getProperty("user.dir") + "/data/test.lr1");
		Parser parser = new Parser(tokens, parseIn);
		ParseTree t = parser.parse();
		t.pprint();
	}
	
	@Test
	public void testGrammar() throws Exception {
		File f = new File(System.getProperty("user.dir") + "/test/testprogram/TestAll.java");
		Scanner scanner = new Scanner(new FileReader(f));
		List<Token> tokens = scanner.scan();
		Parser par = new Parser(tokens, grammar);
		ParseTree t = par.parse();
		t.pprint();
	}

}
