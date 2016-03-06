package environment;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ast.ArrayAccess;
import ast.ArrayCreationExpression;
import ast.ArrayType;
import ast.AssignmentExpression;
import ast.BooleanLiteral;
import ast.CastExpression;
import ast.CharacterLiteral;
import ast.ClassInstanceCreationExpression;
import ast.CompilationUnit;
import ast.Expression;
import ast.FieldAccess;
import ast.InfixExpression;
import ast.InfixExpression.Operator;
import ast.InstanceofExpression;
import ast.IntegerLiteral;
import ast.MethodInvocation;
import ast.Name;
import ast.NullLiteral;
import ast.PrefixExpression;
import ast.PrimitiveType;
import ast.PrimitiveType.Value;
import ast.QualifiedName;
import ast.SimpleName;
import ast.SimpleType;
import ast.StringLiteral;
import ast.ThisExpression;
import ast.Type;
import ast.TypeDeclaration;
import ast.VariableDeclaration;
import ast.VariableDeclarationExpression;
import exceptions.TypeCheckingException;

public class TypeCheckingVisitor extends TraversalVisitor {
    private final Map<String, TypeDeclaration> global = SymbolTable.getGlobal();
    private final TypeHelper helper = new TypeHelper();
    private String currentTypeName;
    // maybe need to add or delete some methods...

    @Override
    public void visit(CompilationUnit node) throws Exception {
        for (TypeDeclaration typeDecl : node.types) {
            this.currentTypeName = typeDecl.getFullName();
            typeDecl.accept(this);
        }
    }

    @Override
    public void visit(ArrayAccess node) throws Exception {
        if (node.array != null) {
            node.array.accept(this);
        }
        if (node.index != null) {
            node.index.accept(this);
        }
        Type arrayType = node.array.getType();
        Type indexType = node.index.getType();
        if (arrayType instanceof ArrayType) {
            Set<Value> values = new HashSet<Value>();
            values.add(Value.BOOLEAN);
            if (CheckSinglePrimitive(indexType, values, null)) {
                node.attachType(((ArrayType) arrayType).type);
            } else {
                throw new TypeCheckingException("Index cannot be boolean.");
            }
        } else {
            throw new TypeCheckingException("Access a non-array type by array access.");
        }
    }

    @Override
    public void visit(ArrayCreationExpression node) throws Exception {
        if (node.type != null) {
            node.type.accept(this);
        } else {
            throw new TypeCheckingException("No null array.");
        }
        if (node.expr != null) {
            node.expr.accept(this);
        }
        Type indexType = node.expr.getType();
        
        Set<Value> values = new HashSet<Value>();
        values.add(Value.BOOLEAN);
        if (CheckSinglePrimitive(indexType, values, null)) {
            node.attachType(arrayTypeBuilder(node.type));
        } else {
            throw new TypeCheckingException("Index cannot be boolean.");
        }
    }

    /**
     * TODO:
     * lhs: QualifiedName/SimpleName/ArrayAccess/FieldAccess
     * expr: QualifiedName/SimpleName/ArrayAccess/MethodInvocation/FieldAccess
     * 
     */
    @Override
    public void visit(AssignmentExpression node) throws Exception {
        if (node.lhs != null) {
            node.lhs.accept(this);
        }
        Type lhsType = node.lhs.getType();

        if (node.expr != null) {
            node.expr.accept(this);
        }
        Type exprType = node.expr.getType();

        if (node.lhs instanceof ArrayAccess) {
            ArrayAccess arrayAccess = (ArrayAccess) node.lhs;
            Type typeOfArray = arrayAccess.getType();
            if (helper.assignable(typeOfArray, exprType)) {
                node.attachType(typeOfArray);
            } else {
                throw new TypeCheckingException("Invalid assignment: incomparable types");
            }
            
        } else {
            if (helper.assignable(lhsType, exprType)) {
                node.attachType(lhsType);
            } else {
                throw new TypeCheckingException("Invalid assignment: incomparable types");
            }
        }
    }

    @Override
    public void visit(BooleanLiteral node) throws Exception {
        node.attachType(new PrimitiveType(Value.BOOLEAN));
    }

    @Override
    public void visit(CastExpression node) throws Exception {
        Type castToType = null;

        if (node.type != null) {
            node.type.accept(this);
            castToType = node.type;
        }
        if (node.expr != null) {
            node.expr.accept(this);
            castToType = node.expr.getType();
        }
        if (node.unary != null) {
            node.unary.accept(this);
        }
        Type unaryType = node.unary.getType();
        
        // break cast into three cases:
        if (checkPrimitive(castToType, unaryType, false)) {
            node.attachType(castToType);
        } else if (checkPrimitive(castToType, unaryType, true)) {
            node.attachType(new PrimitiveType(Value.BOOLEAN));
        } else if (helper.assignable(castToType, unaryType) || helper.assignable(unaryType, castToType)) {
            node.attachType(simpleTypeBuilder((SimpleType) castToType));
        }
    }

    @Override
    public void visit(CharacterLiteral node) throws Exception {
        node.attachType(new PrimitiveType(Value.CHAR));
    }

    /**
     * TODO:
     * get the constructors of node.type
     * check whether node.arglist matches the parameters of one of the constructors
     * the type of node is node.type
     */
    @Override
    public void visit(ClassInstanceCreationExpression node) throws Exception {
        if (node.type != null) {
            node.type.accept(this);
        }
        if (node.arglist != null) {
            for (Expression expr : node.arglist) {
                expr.accept(this);
            }
        }
    }

    /**
     * TODO: 
     * node.expr is the qualifier
     * node.id is field's name
     * the type of A.B.C.f is type of C or C.
     */
    
    @Override
    public void visit(FieldAccess node) throws Exception {
        if (node.expr != null) {
            node.expr.accept(this);
        }
        
        
    }

    @Override
    public void visit(InfixExpression node) throws Exception {
        if (node.lhs != null) {
            node.lhs.accept(this);
        }
        Type lhsType = node.lhs.getType();

        if (node.rhs != null) {
            node.rhs.accept(this);
        }
        Type rhsType = node.lhs.getType();

        Operator op = node.op;
        
        Type type = typeCheckInfixExp(lhsType, rhsType, op);
        node.attachType(type);
    }

    @Override
    public void visit(InstanceofExpression node) throws Exception {
        if (node.expr != null) {
            node.expr.accept(this);
        }
        if (node.type != null) {
            node.type.accept(this);
        }

        Type exprType = node.expr.getType();

        if (helper.assignable(exprType, node.type)
                || helper.assignable(node.type, exprType)) {
            node.attachType(new PrimitiveType(Value.BOOLEAN));
        } else {
            throw new TypeCheckingException("Uncomparable types in instanceof");
        }
    }

    @Override
    public void visit(IntegerLiteral node) throws Exception {
        node.attachType(new PrimitiveType(Value.INT));
    }

    /**
     * TODO:
     * node.expr: A.B.C.m
     * get declaration of methods in type C (or the type of C) who have name m. 
     * check whether node.arglist matches the parameters of one of the methods
     * the type of node is the return type of m.
     */
    @Override
    public void visit(MethodInvocation node) throws Exception {
        if (node.expr != null) {
            node.expr.accept(this);
        }
        if (node.arglist != null) {
            for (Expression expr : node.arglist) {
                expr.accept(this);
            }
        }

    }

    @Override
    public void visit(NullLiteral node) throws Exception {
        node.attachType(null);
    }

    @Override
    public void visit(PrefixExpression node) throws Exception {
        if (node.expr != null) {
            node.expr.accept(this);
        }
        Type expr = node.expr.getType();

        ast.PrefixExpression.Operator op = node.op;

        Type type = typeCheckPrefixExp(expr, op);
        node.attachType(type);
    }

    @Override
    public void visit(StringLiteral node) throws Exception {
        node.attachType(simpleTypeBuilder("java.lang.String"));
    }

    @Override
    public void visit(ThisExpression node) throws Exception {
        node.attachType(simpleTypeBuilder(this.currentTypeName));
    }

    @Override
    public void visit(VariableDeclarationExpression node) throws Exception {
        if (node.variableDeclaration != null) {
            node.variableDeclaration.accept(this);
        }

        Type initializerType = node.variableDeclaration.initializer.getType();
        if (helper.assignable(node.variableDeclaration.type, initializerType)) {
            node.attachType(node.variableDeclaration.type);
        }
    }
    
    @Override
    public void visit(SimpleName node) throws Exception {
        attachTypeOfName(node);
    }

    @Override
    public void visit(QualifiedName node) throws Exception {
        attachTypeOfName(node);
    }

    private void attachTypeOfName(Name node) throws TypeCheckingException {
        VariableDeclaration vDecl = (VariableDeclaration) node.getDeclaration();
        Type type = vDecl.type;
        if (type != null) {
            node.attachType(type);
        } else {
            throw new TypeCheckingException("Type not found for name: " + node.toString());
        }
    }

    private Type typeCheckInfixExp(Type lhs, Type rhs, Operator op) throws TypeCheckingException {
        switch (op) {
        case PLUS:
            // Type checking for String concatenation.
            SimpleType type1 = checkeStringConcat(lhs, rhs);
            SimpleType type2 = checkeStringConcat(rhs, lhs);
            if (type1 != null) {
                return type1;
            } else if (type2 != null) {
                return type2;
            }
            if (checkPrimitive(lhs, rhs, false)) {
                return new PrimitiveType(Value.INT);
            } else {
                throw new TypeCheckingException("Invalid operation: + have to be used for PrimitiveType except boolean");
            }
        case AND:
        case LOR:
        case BITOR:
        case BITAND:
            if (checkPrimitive(lhs, rhs, true)) {
                return new PrimitiveType(Value.BOOLEAN);
            } else {
                throw new TypeCheckingException("Invalid comparison: & && | || have to be used for boolean");
            }
        case LANGLE:
        case RANGLE:
        case GEQ:
        case LEQ:
            if (checkPrimitive(lhs, rhs, false)) {
                return new PrimitiveType(Value.BOOLEAN);
            } else {
                throw new TypeCheckingException("Invalid comparison: < << > >> have to be used for PrimitiveType except boolean");
            }
        case NEQ:
        case EQUAL:
            if (helper.assignable(lhs, rhs) || helper.assignable(rhs, lhs)) {
                return new PrimitiveType(Value.BOOLEAN);
            } else {
                throw new TypeCheckingException("Invalid comparison: = == have to be used for comparable types");
            }
        case MINUS:
        case STAR:
        case SLASH:
        case MOD:
            if (checkPrimitive(lhs, rhs, false)) {
                return new PrimitiveType(Value.INT);
            } else {
                throw new TypeCheckingException("Invalid operation: - * / % have to be used for PrimitiveType except boolean");
            }
        }
        return null;
    }
    
    private SimpleType checkeStringConcat(Type type1, Type type2)
            throws TypeCheckingException {
        if (type1 instanceof SimpleType) {
            if (type1.getDeclaration().getFullName() == "java.lang.String") {
                if (!(type2 instanceof Void)) {
                    return simpleTypeBuilder((SimpleType) type1);
                } else {
                    throw new TypeCheckingException("Cannot concat string with void");
                }
            }
        }
        return null;
    }

    private boolean checkPrimitive(Type type1, Type type2, boolean isBoolean) {
        if ((type1 instanceof PrimitiveType) && (type2 instanceof PrimitiveType)) {
            PrimitiveType ptype1 = (PrimitiveType) type1;
            PrimitiveType ptype2 = (PrimitiveType) type2;
            if (isBoolean) {
                if (ptype1.value.equals(Value.BOOLEAN) && ptype2.value.equals(Value.BOOLEAN)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (!ptype1.value.equals(Value.BOOLEAN) && !ptype2.value.equals(Value.BOOLEAN)) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    private Type typeCheckPrefixExp(Type expr, ast.PrefixExpression.Operator op) throws TypeCheckingException {
        Set<Value> values;
        switch (op) {
        // TODO: Check whether the type of -byte and -short is int.
        case MINUS:
            values = new HashSet<Value>();
            values.add(Value.CHAR);
            values.add(Value.BOOLEAN);
            if (CheckSinglePrimitive(expr, values, null)) {
                return new PrimitiveType(Value.INT);
            }
            break;
        case NOT:
            values = new HashSet<Value>();
            values.add(Value.BOOLEAN);
            if (CheckSinglePrimitive(expr, null, values)) {
                return new PrimitiveType(Value.BOOLEAN);
            }
            break;
        }
        throw new TypeCheckingException("Invalid prefix expression");
    }

    private boolean CheckSinglePrimitive(Type type, Set<Value> excludes, Set<Value> includes) {
        Set<Value> allTypes = new HashSet<Value>();
        allTypes.add(Value.BOOLEAN);
        allTypes.add(Value.INT);
        allTypes.add(Value.CHAR);
        allTypes.add(Value.BYTE);
        allTypes.add(Value.SHORT);
        if (excludes != null) {
            allTypes.removeAll(excludes);
        } else if (includes != null) {
            allTypes.retainAll(includes);
        }
        if (type instanceof PrimitiveType) {
            PrimitiveType ptype = (PrimitiveType) type;
            if (allTypes.contains(ptype.value)) {
                return true;
            }
        }
        return false;
    }

    private SimpleType simpleTypeBuilder(SimpleType simpleType) {
        Name name = simpleType.name;
        SimpleType type = new SimpleType(name);
        type.attachDeclaration(simpleType.getDeclaration());
        return type;
    }

    private ArrayType arrayTypeBuilder(Type type) throws TypeCheckingException {
        ArrayType arrayType = null;
        if (type instanceof SimpleType) {
            SimpleType stype = (SimpleType) type;
            Name name = stype.name;
            arrayType = new ArrayType(name);
        } else if (type instanceof PrimitiveType) {
            PrimitiveType ptype = (PrimitiveType) type;
            SimpleName name = new SimpleName(ptype.value.toString());
            arrayType = new ArrayType(name);
        } else {
            throw new TypeCheckingException("ArrayBuilder error.");
        }
        return arrayType;
    }

    // Keep this for String Literal and this for now...
    private SimpleType simpleTypeBuilder(String typeName) {
        SimpleName name = new SimpleName(typeName);
        SimpleType type = new SimpleType(name);
        TypeDeclaration typeDec = global.get(typeName);
        type.attachDeclaration(typeDec);
        return type;
    }
}
