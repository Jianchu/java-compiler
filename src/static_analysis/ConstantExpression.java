package static_analysis;

import ast.*;

public class ConstantExpression {

    static int NOT_CONSTANT = 0;
    static int CONSTANT_TRUE = 1;
    static int CONSTANT_FALSE = 2;
    //see section 15.28 of JLS

    // v.getDeclaration
    // vd.modifiers
    // vd.initializer
    public static int isConstant(Expression expr) {
        if (isConstExpr(expr)) {
            BooleanLiteral val = (BooleanLiteral) evalConstExpr(expr);
            if (val.value) {
                return CONSTANT_TRUE;
            }
            return CONSTANT_FALSE;
        }
        return NOT_CONSTANT;
    }

    private static boolean isConstExpr(Expression expr) {
        if (expr instanceof BooleanLiteral ||
            expr instanceof CharacterLiteral ||
            expr instanceof IntegerLiteral ||
            expr instanceof NullLiteral ||
            expr instanceof StringLiteral) {
            return true;
        }
        if (expr instanceof CastExpression) {
            CastExpression castExpr = (CastExpression) expr;
            if (castExpr.type instanceof PrimitiveType ||
                castExpr.type.toString().equals("java.lang.String")) {
                return isConstExpr(castExpr.unary);
            }
        }
        if (expr instanceof PrefixExpression) {
            PrefixExpression preExpr = (PrefixExpression) expr;
            return isConstExpr(preExpr.expr);
        }
        if (expr instanceof InfixExpression) {
            InfixExpression inExpr = (InfixExpression) expr;
            return isConstExpr(inExpr.lhs) && isConstExpr(inExpr.rhs);
        }
        if (expr instanceof SimpleName) {
            SimpleName sName = (SimpleName) expr;
            ASTNode sNameDecl = sName.getDeclaration();
            if (sNameDecl instanceof VariableDeclaration) {
                VariableDeclaration vDecl = (VariableDeclaration) sNameDecl;
                return vDecl.modifiers.contains(Modifier.FINAL) && isConstExpr(vDecl.initializer);
            } else if (sNameDecl instanceof FieldDeclaration) {
                FieldDeclaration fDecl = (FieldDeclaration) sNameDecl;
                return fDecl.modifiers.contains(Modifier.FINAL) && isConstExpr(fDecl.initializer);
            }
        }
        if (expr instanceof QualifiedName) {
            QualifiedName qName = (QualifiedName) expr;
            if (! qName.getQualifier() instanceof TypeDeclaration) {
                return false;
            }
            ASTNode qNameDecl = qName.getDeclaration();
            if (qNameDecl instanceof FieldDeclaration) {
                FieldDeclaration fDecl = (FieldDeclaration) qNameDecl;
                return fDecl.modifiers.contains(Modifier.FINAL) && isConstExpr(fDecl.initializer);
            }
        }
        return false;
    }
}
