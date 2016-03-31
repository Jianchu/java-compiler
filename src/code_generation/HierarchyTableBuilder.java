package code_generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import utility.StringUtility;
import ast.AST;
import ast.ArrayType;
import ast.Name;
import ast.PrimitiveType;
import ast.PrimitiveType.Value;
import ast.SimpleName;
import ast.SimpleType;
import ast.Type;
import ast.TypeDeclaration;
import environment.TypeHelper;

public class HierarchyTableBuilder {
    
    static Map<Type, List<String>> hierarchyTable = new HashMap<Type, List<String>>();
    static Map<Type, Integer> offSets = new HashMap<Type, Integer>();
    static Integer offSetCounter = new Integer(0);
    static List<String> Alltypes = new ArrayList<String>();

    public static void build(List<AST> trees) throws Exception {
        List<TypeDeclaration> typeDecs = new LinkedList<TypeDeclaration>();
        for (AST ast : trees) {
            if (ast.root.types.size() > 0) {
                TypeDeclaration type = ast.root.types.get(0);
                typeDecs.add(type);
            }
        }
        setOffSet(typeDecs);
        setHierarchy();
        // printOffSets();
    }
    
    private static void sort(List<Type> types) {
        for (int i = 0; i < offSetCounter; i++) {
            for (Map.Entry<Type, Integer> entry : offSets.entrySet()) {
                if (entry.getValue() == i) {
                    types.add(entry.getKey());
                }
            }
        }
    }

    private static void setHierarchy() {
        List<Type> types = new ArrayList<Type>();
        sort(types);
        for (Type typeInTop : types) {
            List<String> column = new ArrayList<String>();
            for (Type typeInLeft : types) {
                boolean isSubType = TypeHelper.assignable(typeInLeft, typeInTop);
                if (isSubType) {
                    //System.out.println("super: " + typeInLeft + ", sub: " + typeInTop);
                    column.add("0xffffffff");
                } else {
                    column.add("0x0");
                }
            }
            Alltypes.add(typeInTop.toString());
            hierarchyTable.put(typeInTop, column);
        }
//        for (Type type : hierarchyTable.keySet()) {
//            System.out.println(type);
//            for (int i = 0; i < hierarchyTable.get(type).size(); i++) {
//                System.out.println(Alltypes.get(i) + " " + hierarchyTable.get(type).get(i));
//            }
//        }
    }

    private static void setOffSet(List<TypeDeclaration> typeDecs) {

        setPrimitiveOffSet();

        for (TypeDeclaration typeDec : typeDecs) {
            SimpleType simpleType = simpleTypeBuilder(typeDec);
            offSets.put(simpleType, offSetCounter);
            offSetCounter++;
            ArrayType arrayType = arrayTypeBuilder(simpleType);
            offSets.put(arrayType, offSetCounter);
            offSetCounter++;
        }
    }

    private static void setPrimitiveOffSet() {
        // primitive type:
        primitiveOffSetHelper(Value.BOOLEAN);
        primitiveOffSetHelper(Value.BYTE);
        primitiveOffSetHelper(Value.CHAR);
        primitiveOffSetHelper(Value.INT);
        primitiveOffSetHelper(Value.SHORT);
    }

    private static void primitiveOffSetHelper(Value value) {
        PrimitiveType primitiveType = new PrimitiveType(value);
//        offSets.put(primitiveType, offSetCounter);
//        offSetCounter++;

        ArrayType primitiveArrayType = arrayTypeBuilder(primitiveType);
        offSets.put(primitiveArrayType, offSetCounter);
        offSetCounter++;
    }

    private static SimpleType simpleTypeBuilder(TypeDeclaration typeDec) {
        Name name = new SimpleName(typeDec.getFullName());
        SimpleType type = new SimpleType(name);
        type.attachDeclaration(typeDec);
        return type;
    }

    private static ArrayType arrayTypeBuilder(Type type) {
        ArrayType arrayType = null;
        if (type instanceof SimpleType) {
            SimpleType stype = (SimpleType) type;
            arrayType = new ArrayType(stype);
        } else if (type instanceof PrimitiveType) {
            PrimitiveType ptype = (PrimitiveType) type;
            arrayType = new ArrayType(ptype);
        }
        return arrayType;
    }

    public static int getTypeOffSet(String typeName) {
        return Alltypes.indexOf(typeName);
    }

    public static int getTypeOffSet(Type type) {
        return Alltypes.indexOf(type.toString());
    }

    public static String getHierarchyTable() {

        return buildText();
    }

    private static String buildText() {
        StringBuilder hierarchyText = new StringBuilder();
        StringUtility.appendLine(hierarchyText, "section .data");
        for (Type type : hierarchyTable.keySet()) {
            String typeSig = SigHelper.getClassSigWithHierarchy(type);
            StringUtility.appendLine(hierarchyText, "global " + typeSig);
            StringUtility.appendIndLn(hierarchyText, typeSig + ":");
            for (int i = 0; i < hierarchyTable.get(type).size(); i++) {
                StringUtility.appendLine(hierarchyText, "dd " + hierarchyTable.get(type).get(i), 2);
            }
        }
        return hierarchyText.toString();
    }

    private static void printOffSets() {
        for (String type : Alltypes) {
            System.out.println(Alltypes.indexOf(type) + " " + type);
        }
    }
}
