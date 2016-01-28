package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import scanner.Scanner;
import scanner.Token;

public class TestRestrictions {
    /*
     * checked in scanner:
     *     All characters in the input program must be in the range of 7-bit ASCII (0 to 127).
     * checked in parser (grammar):
     *     No multidimensional array types or array creation expressions are allowed.
     *     A method or constructor must not contain explicit this() or super() calls.
     *     The type void may only be used as the return type of a method.
     *     An interface cannot contain fields or constructors. 
     *     A formal parameter of a method must not have an initializer.
     *     An interface method cannot have a body.
     * in issue:
     *     A class/interface must be declared in a .java file with the same base name as the class/interface.
     */
    File grammar;
	@Rule public ExpectedException thrown= ExpectedException.none();

    //
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
        runParser(f);
    }

    //A method has a body if and only if it is neither abstract nor native.
    @Test
    public void testMethodBodyNeitherAbstractNorNative() throws Exception {
        File f = new File(
                System.getProperty("user.dir")
                        + "/test/testprogram/restrictions/MethodBodyNeitherAbstractNorNative.txt");
        runParser(f);

    }

    //An abstract method cannot be static or final.
    @Test
    public void testAbstractMethodCannotBeStaticOrFinal() throws Exception {
        File f = new File(
                System.getProperty("user.dir")
                        + "/test/testprogram/restrictions/AbstractMethodCannotBeStaticOrFinal.txt");
        runParser(f);

    }

    // A static method cannot be final.
    @Test
    public void testStaticMethodCannotBeFinal() throws Exception {
        File f = new File(
                System.getProperty("user.dir")
                        + "/test/testprogram/restrictions/StaticMethodCannotBeFinal.txt");
        runParser(f);

    }

    //A native method must be static.
    @Test
    public void testNativeMethodMustBeStatic() throws Exception {
        File f = new File(
                System.getProperty("user.dir")
                + "/test/testprogram/restrictions/NativeMethodMustBeStatic.txt");
        runParser(f);

    }
    


    // An interface method cannot be static, final, or native.
    @Test
    public void testNoStaticFinalNativeForInterfaceMethod() throws Exception {
        File f = new File(
                System.getProperty("user.dir")
                        + "/test/testprogram/restrictions/NoStaticFinalNativeForInterfaceMethod.txt");
        runParser(f);

    }



    //No field can be final.
    @Test
    public void testNoFieldCanBeFinal() throws Exception {
        File f = new File(System.getProperty("user.dir")
                + "/test/testprogram/restrictions/NoFieldCanBeFinal.txt");
        runParser(f);
    }
    
    //Every class must contain at least one explicit constructor.
    @Test
    public void testClassMustHaveExplicitConstructor() throws Exception {
        File f = new File(System.getProperty("user.dir")
                        + "/test/testprogram/restrictions/ClassMustHaveExplicitConstructor.txt");
        runParser(f);
    }
    
    private void runParser(File program) throws Exception {
        thrown.expect(Exception.class);
    	Scanner scanner = new Scanner(new FileReader(program));
        List<Token> tokens = scanner.scan();
        Parser par = new Parser(tokens, grammar);
        ParseTree t = par.parse();
        t.pprint();
    }

}
