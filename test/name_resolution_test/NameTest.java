package name_resolution_test;

import org.junit.Test;
import joosc.Joosc;

public class NameTest {
	@Test
	public void test() {
		String[] paths = {System.getProperty("user.dir") + "/assignment_testcases/a1/J1_01.java"};
		Joosc.compileSTL(paths);
	}
	
	@Test
	public void testModifier() {
		String[] paths = {System.getProperty("user.dir") + "/test/testprogram/PublicFinal.java"};
		Joosc.compileSTL(paths);
	}
}
