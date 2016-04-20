package ast;

import java.util.List;

import parser.ParseTree;
import scanner.Symbol;
import ast.PrimitiveType.Value;
import exceptions.ASTException;
import exceptions.NameException;

public class CastExpression extends Expression {
    public Type type;
    public Expression expr;
    public Expression unary;
    public boolean isArray;

    public CastExpression(ParseTree pt) throws ASTException {
        List<ParseTree> subtrees = pt.getChildren();
        if (subtrees.get(1).getTokenType() == Symbol.PrimitiveType) {
            type = Type.parseType(subtrees.get(1));
        } else {
            expr = Expression.parseExpression(subtrees.get(1));
        }
        if (subtrees.size() == 5) {
            isArray = true;
            unary = Expression.parseExpression(subtrees.get(4));
        } else {
            isArray = false;
            unary = Expression.parseExpression(subtrees.get(3));
        }
        
        // tranform the expression to type
        if (this.expr != null) {
            if (!(this.expr instanceof Name)) {
                throw new NameException("Unexpected expresssion in name.");
            }
            Name exprN = (Name) this.expr;
            Type tempType;
            if (PrimitiveType.primitives().contains(exprN.toString())) {
                tempType = new PrimitiveType(Value.valueOf(exprN.toString()));
            } else {
                tempType = new SimpleType(exprN);
            }

            if (this.isArray) {
                this.type = new ArrayType(tempType);
            } else {
                this.type = tempType;
            }
            this.expr = null; // clear the useless expression now
            // if expr = null, then type = PrimitiveType, and if isArray = true,
            // type should be array type rather than PrimitiveType
        } else if (this.expr == null && isArray) {
            this.type = new ArrayType(this.type);
        }
    }

    public void accept(Visitor v) throws Exception {
        v.visit(this);
    }
}
