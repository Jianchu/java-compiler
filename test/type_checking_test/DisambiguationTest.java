package type_checking_test;

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
	public void testAST() throws Exception {
		String path = System.getProperty("user.dir") + "/java/lang/String.java";
//		String path = System.getProperty("user.dir") + "/test/testprogram/CastExpr.java";
		
		ASTPrinterVisitor.print(path);
	}
	
	@Test
	public void test() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_5_AmbiguousName_FieldVsType_Initializer.java").toArray(paths);
        Joosc.compileSTL(paths);	
	}

}
