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
    
    //exceptions.NameException: Import class name not recoginzed: java.awt
    //at environment.TopDeclVisitor.visit(TopDeclVisitor.java:92)
    //This is importOnDemands way go in to single import branch?
    @Test
    public void testCheck2() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J1_3_OnDemandImport_NonAmbiguous_SamePackage").toArray(paths);
        Joosc.compileSTL(paths);
    }
    
        
}
