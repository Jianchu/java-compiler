package ast;

import java.util.List;

import exceptions.ASTException;
import exceptions.NameException;
import scanner.Symbol;
import parser.ParseTree;

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
				throw new NameException("123");
			}
			if (this.isArray) {
				this.type = new ArrayType((Name) this.expr);
			} else {
				this.type = new SimpleType((Name) this.expr);
			}
			this.expr = null;	// clear the useless expression now
		}
    }
    
	public void accept(Visitor v) throws Exception {
		v.visit(this);
	}
}
