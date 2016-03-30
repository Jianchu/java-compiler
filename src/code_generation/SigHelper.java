package code_generation;

import ast.ArrayType;
import ast.FieldDeclaration;
import ast.MethodDeclaration;
import ast.PrimitiveType;
import ast.PrimitiveType.Value;
import ast.SimpleName;
import ast.SimpleType;
import ast.Type;
import ast.TypeDeclaration;
import ast.VariableDeclaration;

/**
 * "/" -> "#"
 * "(" and ")" -> "$"
 * "<" and ">" -> "~"
 * "[" -> "@"
 * "," -> "?"
 * @author jianchu
 *
 */

public class SigHelper {

    public static String getTypeSig(Type type) {
        String sigName = null;
        if (type instanceof SimpleType) {
            SimpleType stype = (SimpleType)type;
            sigName = stype.getDeclaration().getFullName();
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
                sigName = "@L" + arrayTypeName;
            } else {
                sigName = "@" + arrayTypeName;
            }
        }
        return sigName;
    }

    private static String getMethodSig(MethodDeclaration md) {
        StringBuilder methodSig = new StringBuilder();
        TypeDeclaration typeNode = (TypeDeclaration) md.getParent();
        methodSig.append(getClassSig(typeNode));
        methodSigHelper(md, methodSig);
        return methodSig.toString();
    }
    
    public static String getMethodSigWithImp(MethodDeclaration md) {
        StringBuilder methodSig = new StringBuilder();
        TypeDeclaration typeNode = (TypeDeclaration) md.getParent();
        methodSig.append(getClassSig(typeNode));
        methodSigHelper(md, methodSig);
        return methodSig.toString() + "implementation";
    }

    public static String getFieldSigWithImp(FieldDeclaration fd) {
        StringBuilder fieldSig = new StringBuilder();
        TypeDeclaration typeNode = (TypeDeclaration) fd.getParent();
        fieldSig.append(getClassSig(typeNode));
        fieldSig.append("#");
        fieldSig.append(fd.id);
        return fieldSig.toString() + "$fieldimplementation";
    }

    public static String getMethodSig(TypeDeclaration td, MethodDeclaration md) {
        StringBuilder methodSig = new StringBuilder();
        methodSig.append(getClassSig(td));
        methodSigHelper(md, methodSig);
        return methodSig.toString();
    }
    
    private static void methodSigHelper(MethodDeclaration md, StringBuilder methodSig) {
        if (md.isConstructor) {
            methodSig.append("#~init~$");
        } else {
            methodSig.append("#" + md.id + "$");
        }
        if (md.parameters != null) {
            boolean first = true;
            for (VariableDeclaration varDec : md.parameters) {
                if (first) {
                    first = false;
                } else {
                    methodSig.append("?");
                }
                methodSig.append(getTypeSig(varDec.type));

            }
        }
        methodSig.append("$");
//        if (md.isConstructor) {
//            methodSig.append("V");
//        } else {
//            // Check void?
//            methodSig.append(getTypeSig(md.returnType));
//        }
    }

    public static String getFieldSig(FieldDeclaration fd) {
        StringBuilder fieldSig = new StringBuilder();
        TypeDeclaration typeNode = (TypeDeclaration) fd.getParent();
        fieldSig.append(getClassSig(typeNode));
        fieldSig.append("#");
        fieldSig.append(fd.id);
        return fieldSig.toString();
    }
    
    public static String getFieldSig(TypeDeclaration td, FieldDeclaration fd) {
        StringBuilder fieldSig = new StringBuilder();
        fieldSig.append(getClassSig(td));
        fieldSig.append("#");
        fieldSig.append(fd.id);
        return fieldSig.toString();
    }

    public static String getClassSig(TypeDeclaration typeDec) {
        String classSig = null;
        String name = typeDec.getFullName();
        SimpleType simpleType = new SimpleType(new SimpleName(name));
        simpleType.attachDeclaration(typeDec);
        classSig = getTypeSig(simpleType);
        return classSig;
    }

    public static String getClassSigWithUgly(TypeDeclaration typeDec) {
        return "ugly#" + getClassSig(typeDec);
    }

    public static String instanceFieldInitSig(Type type) {
        return "instance_field_init$" + getTypeSig(type);
    }
    
    public static String instanceFieldInitSig(TypeDeclaration tDecl) {
        return "instance_field_init$" + getClassSig(tDecl);
    }

    public static String getClssSigWithVTable(TypeDeclaration typeDec) {
        return "VTable#" + getClassSig(typeDec);
    }

}
