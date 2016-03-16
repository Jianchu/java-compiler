package code_generation;

import ast.ASTNode;
import ast.ArrayType;
import ast.MethodDeclaration;
import ast.PrimitiveType;
import ast.PrimitiveType.Value;
import ast.SimpleName;
import ast.SimpleType;
import ast.Type;
import ast.TypeDeclaration;
import ast.VariableDeclaration;

public class SigHelper {

    public static String getTypeSig(Type type) {
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
            String arrayTypeName = getTypeSig(atype.type);
            if (atype.type instanceof SimpleType) {
                sigName = "[L" + arrayTypeName;
            } else {
                sigName = "[" + arrayTypeName;
            }
        }
        return sigName;
    }

    public static String getMethodSig(MethodDeclaration md) {
        String classSig = null;
        StringBuilder methodSig = new StringBuilder();
        ASTNode typeNode = md.getParent();
        if (typeNode instanceof TypeDeclaration) {
            TypeDeclaration typeDec = (TypeDeclaration) typeNode;
            String name = typeDec.getFullName();
            SimpleType simpleType = new SimpleType(new SimpleName(name));
            classSig = getTypeSig(simpleType);
        }
        methodSig.append(classSig);
        if (md.isConstructor) {
            methodSig.append("/<init>(");
        } else {
            methodSig.append("/m(");
        }
        if (md.parameters != null) {
            for (VariableDeclaration varDec : md.parameters) {
                methodSig.append(getTypeSig(varDec.type));
            }
        }
        methodSig.append(")");
        if (md.isConstructor) {
            methodSig.append("V");
        } else {
            methodSig.append(getTypeSig(md.returnType));
        }
        return methodSig.toString();
    }
}
