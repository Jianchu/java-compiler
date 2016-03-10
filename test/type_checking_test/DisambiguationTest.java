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
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_6_ProtectedAccess_InstanceMethod_ThisVar").toArray(paths);
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
         * 
         * Je_5_ForwardReference_MethodCall.java
         * Je_5_ForwardReference_InAssignment.java
         */
        @Test
        public void test3() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_5_ForwardReference_ExplicitThis_InAssignment.java").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
        }
        
        @Test
        public void testStaticNonstatic() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/Je_6_NonStaticAccessToStatic_Field.java").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(42, result);
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
        
        @Test
        public void test7() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_typecheck_instanceof3.java").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
        } 
        
        @Test
        public void test8() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/Je_5_AmbiguousName_FieldVsType_Initializer.java").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(42, result);
        }  
        
        @Test
        public void test9() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/Je_6_ConstructorPresent_Super_NoDefault").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(42, result);
        }   
        
        @Test
        public void test10() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_interfaceassignable").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
        }   
        
        @Test
        public void test11() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_InterfaceObject").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
        }   
        
        @Test
        public void test12() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/Je_16_ClosestMatch_Array.java").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(42, result);
        }   
    /**
     * Je_6_ProtectedAccess_InstanceField_SuperVar
     * Je_6_ProtectedAccess_Method_OutsidePackage_NotInSubclass
     * Je_6_ProtectedAccess_InstanceField_NoRelation_Internal
     * Je_6_ProtectedAccess_Method_OutsidePackage_NotBySubclass
     * Je_6_ProtectedAccess_InstanceField_SubDeclare_SubVar
     * Je_6_ProtectedAccess_StaticMethod_Sub_DeclaredInSub
     * Je_6_ProtectedAccess_InstanceMethod_SuperVar
     * Je_6_ProtectedAccess_ClassCreation_Sub
     * Je_16_ProtectedAccess_StaticField_Sub_DeclaredInSub
     * Je_6_ProtectedAccess_WriteField_OutsidePackage_NotInSubclass
     * Je_6_ProtectedAccess_ReadField_OutsidePackage_NotBySubclass
     * Je_6_ProtectedAccess_ClassCreation_Super
     * Je_6_ProtectedAccess_ReadField_OutsidePackage_NotInSubclass
     * Je_6_ProtectedAccess_InstanceMethod_SubDeclare_SubVar
     * Je_6_ProtectedAccess_TwoSubtypes
     * Je_6_ProtectedAccess_WriteField_OutsidePackage_NotBySubclass
     * Je_6_ProtectedAccess_SuperConstructor_NewExp
     * Je_6_ProtectedAccess_External Je_6_ProtectedAccess_Constructor
     * Je_6_ProtectedAccess_InstanceField_NoRelation_External
     * 
     * Je_6_StaticThis_NonStaticField_ImplicitThis.java
     * Je_16_StaticThis_StaticFieldInitializer.java
     * Je_6_NonStaticAccessToStatic_Method.java
     * Je_6_StaticAccessToNontatic_Field.java
     * Je_6_ConstructorPresent_Super_NoDefault
     */
}
