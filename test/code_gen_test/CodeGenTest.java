package code_gen_test;

import static org.junit.Assert.*;

import org.junit.Test;

import joosc.Joosc;
import utility.FileUtility;

public class CodeGenTest {

	@Test
	public void testRun() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/test/testprogram/TestCode.java").toArray(paths);
        Joosc.compileSTL(paths);
	}

}
