package environment;

import java.util.Map;

import ast.ArrayAccess;
import ast.ArrayCreationExpression;
import ast.AssignmentExpression;
import ast.BooleanLiteral;
import ast.CastExpression;
import ast.CharacterLiteral;
import ast.ClassInstanceCreationExpression;
import ast.FieldAccess;
import ast.InfixExpression;
import ast.InfixExpression.Operator;
import ast.InstanceofExpression;
import ast.IntegerLiteral;
import ast.MethodInvocation;
import ast.NullLiteral;
import ast.PrefixExpression;
import ast.PrimitiveType;
import ast.PrimitiveType.Value;
import ast.SimpleName;
import ast.SimpleType;
import ast.StringLiteral;
import ast.ThisExpression;
import ast.Type;
import ast.TypeDeclaration;
import ast.VariableDeclarationExpression;
import exceptions.TypeCheckingException;

public class TypeCheckingVisitor extends TraversalVisitor {
    private final Map<String, TypeDeclaration> global = SymbolTable.getGlobal();

    // maybe need to add or delete some methods...

    @Override
    public void visit(ArrayAccess node) throws Exception {
    }

    @Override
    public void visit(ArrayCreationExpression node) throws Exception {
    }

    @Override
    public void visit(AssignmentExpression node) throws Exception {
    }

    @Override
    public void visit(BooleanLiteral node) throws Exception {
    }

    @Override
    public void visit(CastExpression node) throws Exception {
    }

    @Override
    public void visit(CharacterLiteral node) throws Exception {
    }

    @Override
    public void visit(ClassInstanceCreationExpression node) throws Exception {
    }

    @Override
    public void visit(FieldAccess node) throws Exception {
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
            // need := here...
            break;
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
                    return simpletypeBuilder("java.lang.String");
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

    @Override
    public void visit(InstanceofExpression node) throws Exception {
    }

    @Override
    public void visit(IntegerLiteral node) throws Exception {
    }

    @Override
    public void visit(MethodInvocation node) throws Exception {
    }

    @Override
    public void visit(NullLiteral node) throws Exception {
    }

    @Override
    public void visit(PrefixExpression node) throws Exception {
    }

    @Override
    public void visit(StringLiteral node) throws Exception {
    }

    @Override
    public void visit(ThisExpression node) throws Exception {
    }

    @Override
    public void visit(VariableDeclarationExpression node) throws Exception {
    }

    private SimpleType simpletypeBuilder(String typeName) {
        SimpleName name = new SimpleName(typeName);
        SimpleType type = new SimpleType(name);
        TypeDeclaration typeDec = global.get(typeName);
        type.attachDeclaration(typeDec);
        return type;
    }

}
