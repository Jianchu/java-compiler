package code_generation;

import ast.ArrayType;
import ast.PrimitiveType;
import ast.PrimitiveType.Value;
import ast.SimpleType;
import ast.Type;

public class SigHelper {

    public static String GetTypeSig(Type type) {
        String sigName = null;
        if (type instanceof SimpleType) {
            SimpleType stype = (SimpleType)type;
            sigName = stype.getDeclaration().getFullName().replace('.', '/');
        } else if (type instanceof PrimitiveType) {
            PrimitiveType ptype = (PrimitiveType)type;
            if (ptype.value.equals(Value.BOOLEAN)) {
                sigName = "Z";
            } else {
                sigName = ptype.toString().substring(0, 1).toUpperCase();
            }
        } else if (type instanceof ArrayType) {
            ArrayType atype = (ArrayType) type;
            String arrayTypeName = GetTypeSig(atype.type);
            if (atype.type instanceof SimpleType) {
                sigName = "[L" + arrayTypeName;
            } else {
                sigName = "[" + arrayTypeName;
            }
        }
        return sigName;
    }
}
