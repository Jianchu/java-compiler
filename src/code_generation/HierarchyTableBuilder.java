package code_generation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ast.AST;
import ast.ArrayType;
import ast.Name;
import ast.PrimitiveType;
import ast.PrimitiveType.Value;
import ast.SimpleName;
import ast.SimpleType;
import ast.Type;
import ast.TypeDeclaration;
import environment.SymbolTable;

public class HierarchyTableBuilder {
    
    static Map<Type, List<String>> hierarchyTable = new HashMap<Type, List<String>>();
    static Map<Type, Integer> offSets = new HashMap<Type, Integer>();
    static Integer offSetCounter = new Integer(0);

    public static void build(List<AST> trees) throws Exception {
        List<TypeDeclaration> typeDecs = new LinkedList<TypeDeclaration>();
        for (AST ast : trees) {
            if (ast.root.types.size() > 0) {
                TypeDeclaration type = ast.root.types.get(0);
                typeDecs.add(type);
            }
        }
        setOffSet(typeDecs);
        printOffSets();
        Name name = new SimpleName("java.lang.String");
        SimpleType type = new SimpleType(name);
        TypeDeclaration typeDec = SymbolTable.getGlobal().get(name);
        type.attachDeclaration(typeDec);
        System.out.println(offSets.keySet().contains(type));
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
        // null type:
        offSets.put(null, offSetCounter);
        offSetCounter++;

        // primitive type:
        primitiveOffSetHelper(Value.BOOLEAN);
        primitiveOffSetHelper(Value.BYTE);
        primitiveOffSetHelper(Value.CHAR);
        primitiveOffSetHelper(Value.INT);
        primitiveOffSetHelper(Value.SHORT);
    }

    private static void primitiveOffSetHelper(Value value) {
        PrimitiveType primitiveType = new PrimitiveType(value);
        offSets.put(primitiveType, offSetCounter);
        offSetCounter++;

        ArrayType booleanArrayType = arrayTypeBuilder(primitiveType);
        offSets.put(booleanArrayType, offSetCounter);
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

    private static void setHierarchy(List<TypeDeclaration> typeDecs) {

    }

    public static void printOffSets() {
        for (int i = 0; i < offSetCounter; i++) {
            for (Map.Entry<Type, Integer> entry : offSets.entrySet()) {
                if (entry.getValue() == i) {
                    System.out.println(entry.getKey() + " " + entry.getValue());
                }
            }
        }
    }
}
