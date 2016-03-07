package name_resolution_test;

import static org.junit.Assert.assertEquals;
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
    public void testTopDecl() {
        String[] paths = new String[0];
      paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J1_4_AbstractMethod_InheritAbstractFromObject").toArray(paths);
        Joosc.compileSTL(paths);
    }
    
    @Test
    public void testCheck1() {
        String[] paths = new String[0];
      paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J1_singleTypeImport").toArray(paths);
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
        assertEquals(0, result);
    }
    
    //null pointer exception at ast.Type.parseInterfaceTypeList(Type.java:56)
    //problem in AST?
    // same problem in J1_singleTypeImport
    @Test
    public void testCheck3() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J2_4_InterfaceExtends_MultipleWays").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
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
        assertEquals(42, result);
    }
    
    // See Main.java
    // The problem is in Object interface?
    @Test
    public void testCheck5() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/Je_14_Interface_DeclaresToString_ThrowsConflict").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(42, result);
    }
        
    //See main.java
    // no A class or interface must not have (declare or inherit) two methods
    // with the same name and parameter types but different return types
    @Test
    public void testCheck6() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/Je_4_Override_DifferentReturnTypes_AbstractFromSuperclassAndInterface").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(42, result);
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
        assertEquals(0, result);
    }
    
    /**
     * Check that all import-on-demand declarations refer to existing packages.
     */
    @Test
    public void testCheck8() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/Je_3_ImportOnDemand_ClassNameAsPackage").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(42, result);
    }
    
    
    @Test
    // Import package not recoginzed: foo
    // at environment.TopDeclVisitor.visit(TopDeclVisitor.java:81)
    // at ast.CompilationUnit.accept(CompilationUnit.java:56)
    public void testCheck9() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J1_3_PackageExists_AsPrefix_Internal").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    @Test
    /**
     * Method has same sig as a constructor is valid
     */
    public void testCheck10() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J1_4_DuplicateMethodDeclare_MethodNameEqualsConstructorName.java").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    
    /**
     * Null pointer
     * 
     */
    //null pinter in at environment.TopDeclVisitor.checkInterfaces(TopDeclVisitor.java:309)
    // same: J1_classimplementsserializable2
    // J1_4_InheritedFields_SameField_TwoWays
    @Test
    public void testCheck11() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J2_4_ImplementsInterface_TwiceByName").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    //null pointer in at environment.TopDeclVisitor.checkSuperClass(TopDeclVisitor.java:284)
    // same: J1_classextendsobject2.java
    @Test
    public void testCheck12() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J1_4_ClassExtendsClass_SameName").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    //null pointer in at environment.NameHelper.mangle(NameHelper.java:28)
    @Test
    public void testCheck13() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J1_4_InterfaceMethod_FromObject").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    //null pointer in at environment.Hierarchy.checkPublicFinal(Hierarchy.java:164)
    @Test
    public void testCheck14() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J2_Ifaceimplicitabstract").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    /**
     * exceptions.NameException: package name conflicts with type name. at
     * environment.SymbolTable.checkPkgNames(SymbolTable.java:133) at
     * environment.SymbolTable.buildGlobal(SymbolTable.java:78)
     * same:J1_3_PackageDecl_SamePackageAndClassName
     * J1_4_PackageNameIsClassName_DefaultPackage J1_name
     * J1_3_SingleTypeImport_ClashWithPackageName
     * J1_6_ProtectedAccess_StaticMethod_This
     */
    @Test
    public void testCheck15() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J1_3_InfixResolvesToType").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    /**
     * exceptions.NameException: single import name collides.
       at environment.TopDeclVisitor.visit(TopDeclVisitor.java:96)
     * same :J1_singleTypeImportSameTypeMultipleTimes
     */
    @Test
    public void testCheck16() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J1_3_SingleTypeImport_MultipleImportsOfSameType").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    /**
     * exceptions.NameException: method signature repeated.
        at environment.TopDeclVisitor.visit(TopDeclVisitor.java:180)
        same : J1_constructorWithSameNameAsMethod.java
     */
    @Test
    public void testCheck17() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J1_4_DuplicateMethodDeclare_MethodNameEqualsConstructorName.java").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    /**
     * exceptions.NameException: Import package not recoginzed: foo
        at environment.TopDeclVisitor.visit(TopDeclVisitor.java:81)
        at ast.CompilationUnit.accept(CompilationUnit.java:56)
     */
    @Test
    public void testCheck18() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/J1_3_PackageExists_AsPrefix_Internal").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    /**
     * single import or package name clash with type 
     * same in:
     * Je_3_SingleTypeImport_ClashWithClass
     * Je_3_SingleTypeImport_ClashWithClass_InPackage
     * Je_3_PackageNameIsClassName Je_3_PackageNameIsClassName_External
     * Je_3_PackageNameIsClassName_Prefix
     * Je_3_PackageNameIsClassName_ExternalPrefix
     */
    @Test
    public void testCheck19() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/Je_3_SingleTypeImport_ClashWithInterface").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(42, result);
    }
    
    /**
     * A protected method must not override a public method 
     * same in:
     * Je_4_ProtectedOverride_Exception_Clone
     * Je_4_ProtectedOverride_TwoVersionsFromSuperclass
     */
    @Test
    public void testCheck20() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/Je_4_ProtectedOverride_FromSuperclassAndInterface").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(42, result);
    }
    
    /**
     * An interface must not be mentioned more than once in the same implements clause of a class 
     * same in:
     * Je_4_ImplementTwice_SimpleName
     */
    @Test
    public void testCheck21() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/Je_4_ImplementTwice_QualifiedName").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(42, result);
    }
    
    /**
     * Duplicate Type
     */
    @Test
    public void testCheck22() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/Je_2_DuplicateType").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(42, result);
    }
    
    /**
     * why visit(ClassInstanceCreationExpression node) is not called?
     */
    @Test
    public void testCheck23() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/Je_3_ImportOnDemand_ClashWithImplicitImport").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(42, result);
    }
    
    /**
     * getClass should be a final method?
     */
    @Test
    public void testCheck24() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a2/Je_4_Interface_FinalMethodFromObject").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(42, result);
    }
    
    /***************************** Failed positive test cases from a3 ********************************************/
    @Test
    public void testCheck25() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_typecheck_return.java").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    @Test
    public void testCheck26() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_6_ProtectedAccess_InstanceField_This").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    @Test
    public void testCheck27() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J2_6_ProtectedAccess_StaticField_This").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    @Test
    public void testCheck28() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_sideeffects_obj3.java").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    @Test
    public void testCheck29() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_6_ProtectedAccess_InstanceField_SubVar").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    @Test
    public void testCheck30() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J2_6_ProtectedAccess_StaticField_Sub").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    @Test
    public void testCheck31() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_supermethod_override11").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    @Test
    public void testCheck32() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J1_5_ForwardReference_EqualInfix.java").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }
    
    @Test
    public void testCheck33() {
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/assignment_testcases/a3/J2_6_ProtectedAccess_StaticField_Sub").toArray(paths);
        int result = Joosc.compileSTL(paths);
        assertEquals(0, result);
    }

}
