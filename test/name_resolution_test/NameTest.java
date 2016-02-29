package name_resolution_test;

import joosc.Joosc;

import org.junit.Test;

import utility.FileUtility;

public class NameTest {
    @Test
    public void testRun() {
        String[] paths = {System.getProperty("user.dir") + "/assignment_testcases/a1/J1_01.java"};
        Joosc.compileSTL(paths);
    }
    
    @Test
    public void testCheck1() {
        String[] paths = new String[0];
//      paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a1/").toArray(paths);
        Joosc.compileSTL(paths);
    }

    // duplicate type. foo.Bar is declared twice.
    @Test
    public void testRun1() {
        String[] paths = new String[0];
        paths =  FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/Je_2_DuplicateType/").toArray(paths);
        Joosc.compileSTL(paths);
    }
    
    // TODO: The problems in testCheck2 and testCheck3 cause many crashes!
    
    // stack trace:
    //exceptions.NameException: Import class name not recoginzed: java.awt
    //at environment.TopDeclVisitor.visit(TopDeclVisitor.java:92)
    //
    // This should be importOnDemands, why go in to single import branch?
    // same problem in J1_3_PackageClashWithType_Linked_Mutated
    // same problem in J1_3_ImportOnDemand_DefaultImportInPresenceOfOtherImport
    // same problem in J1_3_ImportOnDemand_ProgramDefinedPackage

    @Test
    public void testCheck2() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J1_3_OnDemandImport_NonAmbiguous_SamePackage").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assert (result == 0) : "Should be a valid case";
    }
    
    //null pointer exception at ast.Type.parseInterfaceTypeList(Type.java:56)
    //problem in AST?
    // same problem in J1_singleTypeImport
    @Test
    public void testCheck3() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J2_4_InterfaceExtends_MultipleWays").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assert (result == 0) : "Should be a valid case";
    }
    
    // See https://www.student.cs.uwaterloo.ca/~cs444/a2.html ,the sixth
    // requirement of type linking.
    // package name same as the type name, should this be checked in visit
    // CompilationUnit when it reads package name?
    // same problem in Je_3_PackageNameIsClassName_Prefix
    // same problem in Je_4_Resolve_DefaultPackage
    // same problem in Je_3_PackageNameIsClassName
    
    @Test
    public void testCheck4() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/Je_3_Resolve_SamePackageAndClassName.java").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assert (result == 42) : "Should be an invalid case";
    }
    
    // See Main.java
    // The problem is in Object interface?
    @Test
    public void testCheck5() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/Je_14_Interface_DeclaresToString_ThrowsConflict").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assert (result == 42) : "Should be an invalid case";
    }
        
    //See main.java
    // no A class or interface must not have (declare or inherit) two methods
    // with the same name and parameter types but different return types
    @Test
    public void testCheck6() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/Je_4_Override_DifferentReturnTypes_AbstractFromSuperclassAndInterface").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assert (result == 42) : "Should be an invalid case";
    }
    
    /**
     * Null pointer exception in Hierarchy.checkPublicFinal() 
     * stack trace:
     * at environment.Hierarchy.checkPublicFinal(Hierarchy.java:164) 
     * at environment.Hierarchy.checkHierarchy(Hierarchy.java:124)
     * at environment.Hierarchy.<init>(Hierarchy.java:15)
     **/
    @Test
    public void testCheck7() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J2_Ifaceimplicitabstract").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assert (result == 0) : "Should be a valid case";
    }
    
    /**
     * Check that all import-on-demand declarations refer to existing packages.
     */
    @Test
    public void testCheck8() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/Je_3_ImportOnDemand_ClassNameAsPackage").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assert (result == 42) : "Should be an invalid case";
    }
}
