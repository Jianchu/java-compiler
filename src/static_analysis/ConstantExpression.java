package static_analysis;

import ast.*;

public class ConstantExpression {

    static int NOT_CONSTANT = 0;
    static int CONSTANT_TRUE = 1;
    static int CONSTANT_FALSE = 2;
    //see section 15.28 of JLS

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
            if (sNameDecl instanceof FieldDeclaration) {
                FieldDeclaration fDecl = (FieldDeclaration) sNameDecl;
                return fDecl.modifiers.contains(Modifier.FINAL) && isConstExpr(fDecl.initializer);
            }
            return false;
        }
        if (expr instanceof QualifiedName) {
            QualifiedName qName = (QualifiedName) expr;
            if (!(qName.getQualifier().getDeclaration() instanceof TypeDeclaration)) {
                return false;
            }
            ASTNode qNameDecl = qName.getDeclaration();
            if (qNameDecl instanceof FieldDeclaration) {
                FieldDeclaration fDecl = (FieldDeclaration) qNameDecl;
                return fDecl.modifiers.contains(Modifier.FINAL) && isConstExpr(fDecl.initializer);
            }
            return false;
        }
        return false;
    }

    private static Expression evalConstExpr(Expression expr) {
        if (expr instanceof BooleanLiteral ||
            expr instanceof CharacterLiteral ||
            expr instanceof IntegerLiteral ||
            expr instanceof NullLiteral ||
            expr instanceof StringLiteral) {
            return expr;
        }
        if (expr instanceof CastExpression) {
            CastExpression cExpr = (CastExpression) expr;
            return evalConstExpr(cExpr.unary);
        }
        if (expr instanceof PrefixExpression) {
            PrefixExpression preExpr = (PrefixExpression) expr;
            Expression subExpr = evalConstExpr(preExpr.expr);
            switch (preExpr.op) {
              case MINUS:
                int valSubExpr = extractInt(subExpr);
                return new IntegerLiteral(-valSubExpr);
              case NOT:
                BooleanLiteral boolSubExpr = (BooleanLiteral) subExpr;
                return new BooleanLiteral(!boolSubExpr.value);
            }
        }
        if (expr instanceof InfixExpression) {
            InfixExpression inExpr = (InfixExpression) expr;
            Expression lhs = evalConstExpr(inExpr.lhs);
            Expression rhs = evalConstExpr(inExpr.rhs);

            BooleanLiteral boolLHS = null;
            BooleanLiteral boolRHS = null;
            StringLiteral strLHS = null;
            StringLiteral strRHS = null;
            int valLHS = 0;
            int valRHS = 0;
            switch (inExpr.op) {
              case LOR:     //fall through
              case BITOR:
                boolLHS = (BooleanLiteral) lhs;
                boolRHS = (BooleanLiteral) rhs;
                return new BooleanLiteral(boolLHS.value || boolRHS.value);
              case AND:     //fall through
              case BITAND:
                boolLHS = (BooleanLiteral) lhs;
                boolRHS = (BooleanLiteral) rhs;
                return new BooleanLiteral(boolLHS.value && boolRHS.value);
              case NEQ:
                if (lhs instanceof BooleanLiteral) {
                    boolLHS = (BooleanLiteral) lhs;
                    boolRHS = (BooleanLiteral) rhs;
                    return new BooleanLiteral(boolRHS.value != boolRHS.value);
                }
                if (lhs instanceof NullLiteral) {
                    return new BooleanLiteral(rhs instanceof StringLiteral);
                }
                if (lhs instanceof StringLiteral) {
                    if (rhs instanceof NullLiteral) {
                        return new BooleanLiteral(true);
                    }
                    strLHS = (StringLiteral) lhs;
                    strRHS = (StringLiteral) rhs;
                    return new BooleanLiteral(!strLHS.value.equals(strRHS.value));
                }
                valLHS = extractInt(lhs);
                valRHS = extractInt(rhs);
                return new BooleanLiteral(valLHS != valRHS);
              case EQUAL:
                if (lhs instanceof BooleanLiteral) {
                    boolLHS = (BooleanLiteral) lhs;
                    boolRHS = (BooleanLiteral) rhs;
                    return new BooleanLiteral(boolLHS.value == boolRHS.value);
                }
                if (lhs instanceof NullLiteral) {
                    return new BooleanLiteral(rhs instanceof NullLiteral);
                }
                if (lhs instanceof StringLiteral) {
                    if (rhs instanceof NullLiteral) {
                        return new BooleanLiteral(false);
                    }
                    strLHS = (StringLiteral) lhs;
                    strRHS = (StringLiteral) rhs;
                    return new BooleanLiteral(strLHS.value.equals(strRHS.value));
                }
                valLHS = extractInt(lhs);
                valRHS = extractInt(rhs);
                return new BooleanLiteral(valLHS == valRHS);
              case LANGLE:
                valLHS = extractInt(lhs);
                valRHS = extractInt(rhs);
                return new BooleanLiteral(valLHS < valRHS);
              case RANGLE:
                valLHS = extractInt(lhs);
                valRHS = extractInt(rhs);
                return new BooleanLiteral(valLHS > valRHS);
              case GEQ:
                valLHS = extractInt(lhs);
                valRHS = extractInt(rhs);
                return new BooleanLiteral(valLHS >= valRHS);
              case LEQ:
                valLHS = extractInt(lhs);
                valRHS = extractInt(rhs);
                return new BooleanLiteral(valLHS <= valRHS);
              case PLUS:
                if (lhs instanceof StringLiteral) {
                    strLHS = (StringLiteral) lhs;
                    if (rhs instanceof BooleanLiteral) {
                        boolRHS = (BooleanLiteral) rhs;
                        return new StringLiteral(strLHS.value + boolRHS.value);
                    }
                    if (rhs instanceof CharacterLiteral) {
                        CharacterLiteral charRHS = (CharacterLiteral) rhs;
                        return new StringLiteral(strLHS.value + charRHS.value);
                    }
                    if (rhs instanceof IntegerLiteral) {
                        IntegerLiteral intRHS = (IntegerLiteral) rhs;
                        return new StringLiteral(strLHS.value + intRHS.value);
                    }
                    if (rhs instanceof NullLiteral) {
                        return new StringLiteral(strLHS.value + null);
                    }
                    if (rhs instanceof StringLiteral) {
                        StringLiteral charRHS = (StringLiteral) rhs;
                        return new StringLiteral(strLHS.value + boolRHS.value);
                    }
                }
                if (rhs instanceof StringLiteral) {
                    strRHS = (StringLiteral) rhs;
                    if (lhs instanceof BooleanLiteral) {
                        boolLHS = (BooleanLiteral) lhs;
                        return new StringLiteral(boolLHS.value + strRHS.value);
                    }
                    if (lhs instanceof CharacterLiteral) {
                        CharacterLiteral charLHS = (CharacterLiteral) lhs;
                        return new StringLiteral(charLHS.value + strRHS.value);
                    }
                    if (lhs instanceof IntegerLiteral) {
                        IntegerLiteral intLHS = (IntegerLiteral) lhs;
                        return new StringLiteral(intLHS.value + strRHS.value);
                    }
                    if (lhs instanceof NullLiteral) {
                        return new StringLiteral(null + strLHS.value);
                    }
                }
                
                valLHS = extractInt(lhs);
                valRHS = extractInt(rhs);
                return new IntegerLiteral(valLHS + valRHS);
              case MINUS:
                valLHS = extractInt(lhs);
                valRHS = extractInt(rhs);
                return new IntegerLiteral(valLHS * valRHS);
              case STAR:
              case SLASH:
                valLHS = extractInt(lhs);
                valRHS = extractInt(rhs);
                return new IntegerLiteral(valLHS / valRHS);
              case MOD:
                valLHS = extractInt(lhs);
                valRHS = extractInt(rhs);
                return new IntegerLiteral(valLHS % valRHS);
            }
        }
        if (expr instanceof SimpleName) {
            SimpleName sName = (SimpleName) expr;
            FieldDeclaration fDecl = (FieldDeclaration) sName.getDeclaration();
            return evalConstExpr(fDecl.initializer);
        }
        if (expr instanceof QualifiedName) {
            QualifiedName qName = (QualifiedName) expr;
            FieldDeclaration fDecl = (FieldDeclaration) qName.getDeclaration();
            return evalConstExpr(fDecl.initializer);
        }
        return null;
    }

    private static int extractInt(Expression expr) {
        if (expr instanceof IntegerLiteral) {
            IntegerLiteral intExpr = (IntegerLiteral) expr;
            return Integer.parseInt(intExpr.value);
        }
        CharacterLiteral charExpr = (CharacterLiteral) expr;
        return (int) charExpr.value.charAt(0);
    }
}
