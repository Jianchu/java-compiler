// JOOS1:PARSER_WEEDER,PARSER_EXCEPTION,TYPE_CHECKING,INVALID_INSTANCEOF
// JOOS2:PARSER_WEEDER,PARSER_EXCEPTION,TYPE_CHECKING,INVALID_INSTANCEOF
// JAVAC:UNKNOWN
// 
/**
 * Typecheck:
 * - Type clause of instanceof must be a reference type
 */
public class Je_6_InstanceOf_Primitive_2 {

    public Je_6_InstanceOf_Primitive_2() {}

    public static int test() { 
	return 123; 
    }

    public boolean foo(Object o) {
	return o instanceof int;
    }
}
