package parser;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import scanner.Scanner;
import scanner.Token;

public class TestRestrictions {
    /*
     * checked in scanner:
     *     All characters in the input program must be in the range of 7-bit ASCII (0 to 127).
     * checked in parser:
     *     No multidimensional array types or array creation expressions are allowed.
     *     A method or constructor must not contain explicit this() or super() calls.
     *     The type void may only be used as the return type of a method. 
     * in issue:
     *     A class/interface must be declared in a .java file with the same base name as the class/interface.
     */


    File grammar;

    @Before
    public void setUp() {
        grammar = new File(System.getProperty("user.dir")
                + "/data/gen/grammar.lr1");
    }

    //A class cannot be both abstract and final.
    @Test
    public void testClassCannotBeAbstractAndFinal() throws Exception {
        File f = new File(
                System.getProperty("user.dir")
                        + "/test/testprogram/restrictions/ClassCannotBeAbstractAndFinal.txt");
        Scanner scanner = new Scanner(new FileReader(f));
        List<Token> tokens = scanner.scan();
        Parser par = new Parser(tokens, grammar);
        ParseTree t = par.parse();
        t.pprint();
    }

    //A method has a body if and only if it is neither abstract nor native.
    @Test
    public void testMethodBodyNeitherAbstractNorNative() throws Exception {
        File f = new File(
                System.getProperty("user.dir")
                        + "/test/testprogram/restrictions/MethodBodyNeitherAbstractNorNative.txt");
        Scanner scanner = new Scanner(new FileReader(f));
        List<Token> tokens = scanner.scan();
        Parser par = new Parser(tokens, grammar);
        ParseTree t = par.parse();
        t.pprint();
    }

    //An abstract method cannot be static or final.
    @Test
    public void testAbstractMethodCannotBeStaticOrFinal() throws Exception {
        File f = new File(
                System.getProperty("user.dir")
                        + "/test/testprogram/restrictions/AbstractMethodCannotBeStaticOrFinal.txt");
        Scanner scanner = new Scanner(new FileReader(f));
        List<Token> tokens = scanner.scan();
        Parser par = new Parser(tokens, grammar);
        ParseTree t = par.parse();
        t.pprint();
    }

    // A static method cannot be final.
    @Test
    public void testStaticMethodCannotBeFinal() throws Exception {
        File f = new File(
                System.getProperty("user.dir")
                        + "/test/testprogram/restrictions/StaticMethodCannotBeFinal.txt");
        Scanner scanner = new Scanner(new FileReader(f));
        List<Token> tokens = scanner.scan();
        Parser par = new Parser(tokens, grammar);
        ParseTree t = par.parse();
        t.pprint();
    }

    //A native method must be static.
    @Test
    public void testNativeMethodMustBeStatic() throws Exception {
        File f = new File(
                System.getProperty("user.dir")
                + "/test/testprogram/restrictions/NativeMethodMustBeStatic.txt");
        Scanner scanner = new Scanner(new FileReader(f));
        List<Token> tokens = scanner.scan();
        Parser par = new Parser(tokens, grammar);
        ParseTree t = par.parse();
        t.pprint();
    }
    
    // A formal parameter of a method must not have an initializer.
    @Test
    public void testParametersMustNotHaveInitializer() throws Exception {
        File f = new File(
                System.getProperty("user.dir")
                        + "/test/testprogram/restrictions/ParametersMustNotHaveInitializer.txt");
        Scanner scanner = new Scanner(new FileReader(f));
        List<Token> tokens = scanner.scan();
        Parser par = new Parser(tokens, grammar);
        ParseTree t = par.parse();
        t.pprint();
    }

    //An interface cannot contain fields or constructors.
    @Test
    public void testNoFieldsConstructorsForInterface() throws Exception {
        File f = new File(
                System.getProperty("user.dir")
                        + "/test/testprogram/restrictions/NoFieldsConstructorsForInterface.txt");
        Scanner scanner = new Scanner(new FileReader(f));
        List<Token> tokens = scanner.scan();
        Parser par = new Parser(tokens, grammar);
        ParseTree t = par.parse();
        t.pprint();
    }

    // An interface method cannot be static, final, or native.
    @Test
    public void testNoStaticFinalNativeForInterfaceMethod() throws Exception {
        File f = new File(
                System.getProperty("user.dir")
                        + "/test/testprogram/restrictions/NoStaticFinalNativeForInterfaceMethod.txt");
        Scanner scanner = new Scanner(new FileReader(f));
        List<Token> tokens = scanner.scan();
        Parser par = new Parser(tokens, grammar);
        ParseTree t = par.parse();
        t.pprint();
    }

    //An interface method cannot have a body.
    @Test
    public void testNoStaticNoBodyForInterfaceMethod() throws Exception {
        File f = new File(System.getProperty("user.dir")
                + "/test/testprogram/restrictions/NoBodyForInterfaceMethod.txt");
        Scanner scanner = new Scanner(new FileReader(f));
        List<Token> tokens = scanner.scan();
        Parser par = new Parser(tokens, grammar);
        ParseTree t = par.parse();
        t.pprint();
    }

    //No field can be final.
    @Test
    public void testNoFieldCanBeFinal() throws Exception {
        File f = new File(System.getProperty("user.dir")
                + "/test/testprogram/restrictions/NoFieldCanBeFinal.txt");
        Scanner scanner = new Scanner(new FileReader(f));
        List<Token> tokens = scanner.scan();
        Parser par = new Parser(tokens, grammar);
        ParseTree t = par.parse();
        t.pprint();
    }
    
    //Every class must contain at least one explicit constructor.
    @Test
    public void testClassMustHaveExplicitConstructor() throws Exception {
        File f = new File(System.getProperty("user.dir")
                        + "/test/testprogram/restrictions/ClassMustHaveExplicitConstructor.txt");
        Scanner scanner = new Scanner(new FileReader(f));
        List<Token> tokens = scanner.scan();
        Parser par = new Parser(tokens, grammar);
        ParseTree t = par.parse();
        t.pprint();
    }

}
