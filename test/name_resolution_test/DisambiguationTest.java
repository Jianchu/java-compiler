package name_resolution_test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ast.ASTPrinterVisitor;
import joosc.Joosc;
import utility.FileUtility;

public class DisambiguationTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void printAST() throws Exception {
		ASTPrinterVisitor.print(System.getProperty("user.dir") + "/java/lang/Integer.java");
	}
	
	@Test
	public void test() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J2_interface_omitted_abstract").toArray(paths);
        Joosc.compileSTL(paths);
	}

}
