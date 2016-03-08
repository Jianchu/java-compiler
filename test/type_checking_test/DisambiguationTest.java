package type_checking_test;

import static org.junit.Assert.assertEquals;
import joosc.Joosc;

import org.junit.Before;
import org.junit.Test;

import utility.FileUtility;

public class DisambiguationTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAST() throws Exception {
		String path = System.getProperty("user.dir") + "/java/lang/String.java";
//		String path = System.getProperty("user.dir") + "/test/testprogram/CastExpr.java";
		
        // ASTPrinterVisitor.print(path);
	}
	
	@Test
	public void test() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_5_AmbiguousName_FieldVsType_Initializer.java").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
	}

	/**
	 * Method unrecognized: System.out.println
	 * Failed for same error:
	 * J1_namelinking3.java
         * J1_implicitstringconcatenation.java
	 */
        @Test
        public void test1() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_implicitstringconcatenation.java").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
        }
        
        /**
         * nullpointer in field access
         */
        @Test
        public void test2() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_6_AssignmentInArrayLength.java").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
        }
        
        /**
         * exceptions.NameException: forward reference
         * Failed for same error:
         * J1_fieldOwnInit2.java
         * J1_fieldOwnInit1.java
         * J1_fieldinit2.java
         * J1_fieldinit_forward_ref.java
         * J1_fieldInOwnInit.java
         * J1_fieldinit_forward_ref2.java
         * J1_5_ForwardReference_ExplicitThis_InAssignment.java
         */
        @Test
        public void test3() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_fieldOwnInit1.java").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
        }
        
        /**
         * Simple Name cannot be  resolved
         * 
         */
        @Test
        public void test4() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_6_ProtectedAccess_InstanceField_This").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
        }
        
        /**
         * Static field not found
         * J2_6_ProtectedAccess_StaticField_Sub
         */
        @Test
        public void test5() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J2_6_ProtectedAccess_StaticField_This").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
        }
        
        /**
         * Field prefix not recognized
         * J1_6_ProtectedAccess_InstanceField_ThisVar
         */
        @Test
        public void test6() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_6_ProtectedAccess_InstanceField_SubVar").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
        }
}
