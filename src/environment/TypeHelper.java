import ast.TypeDeclaration;

public class TypeHelper {
    // t1 := t2
    public static boolean assignable(Type t1, Type t2) {
        if (t1 instanceof PrimitiveType) {
            if (t2 instanceof PrimitiveType) {
            } else if (t2 instanceof SimpleType) {
            } else if (t2 instanceof ArrayType) {
            }
        } else if (t1 instanceof SimpleType) {
            if (t2 instanceof PrimitiveType) {
            } else if (t2 instanceof SimpleType) {
            } else if (t2 instanceof ArrayType) {
            }
        } else if (t1 instanceof ArrayType) {
            if (t2 instanceof PrimitiveType) {
            } else if (t2 instanceof SimpleType) {
            } else if (t2 instanceof ArrayType) {
            }
        }
        /* tau := tau
         short := byte
         int L= char
         sigma := tau; tau := rho -> sigma := rho
         D <= C -> C := D
         C := null
        */
        /*
        Object := sigma []
        Clonable := sigma []
        java.io.Serializable := [
         D <= C -> C[] := D[]
        */
        return false;
    }
}
