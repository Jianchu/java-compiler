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
        Joosc.compileSTL(paths);
    }
    
    // See https://www.student.cs.uwaterloo.ca/~cs444/a2.html ,the sixth
    // requirement of type linking.
    // package name same as the type name, should this be checked in visit
    // CompilationUnit when it reads package name?
    @Test
    public void testCheck3() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/Je_3_Resolve_SamePackageAndClassName.java").toArray(paths);
        Joosc.compileSTL(paths);
    }

    //null pointer exception at ast.Type.parseInterfaceTypeList(Type.java:56)
    //problem in AST?
    // same problem in J1_singleTypeImport
    @Test
    public void testCheck4() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J2_4_InterfaceExtends_MultipleWays").toArray(paths);
        Joosc.compileSTL(paths);
    }
        
}
