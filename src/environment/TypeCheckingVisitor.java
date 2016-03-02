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
            // Separate the following two because we cannot create type by name.
            // Avoid NPE
            // TODO: Think about if(lhs != null && rhs != null)
            if (lhs != null) {
                if (lhs.getDeclaration().getFullName() == "java.lang.String") {
                    if (rhs != null) {
                        return lhs;
                    } else {
                       throw new TypeCheckingException("Cannot concat string with void");
                   }
                }
            }

            // Avoid NPE
            if (rhs != null) {
                if (rhs.getDeclaration().getFullName() == "java.lang.String") {
                    if (lhs != null) {
                        return rhs;
                    } else {
                        throw new TypeCheckingException("Cannot concat string with void");
                    }
                }
            }
        }
        return null;
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

}
