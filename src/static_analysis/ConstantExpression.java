package static_analysis;

import ast.Expression;

public class ConstantExpression {

    static int NOT_CONSTANT = 0;
    static int CONSTANT_TRUE = 1;
    static int CONSTANT_FALSE = 2;
    //see section 15.28 of JLS
    public static int isConstant(Expression expr) {
        return NOT_CONSTANT;
    }
}
