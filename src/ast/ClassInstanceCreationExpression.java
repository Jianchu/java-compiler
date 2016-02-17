package ast;

import java.util.List;

import exceptions.ASTException;
import parser.ParseTree;

public class ClassInstanceCreationExpression extends Expression {
    public Expression name;
    public List<Expression> arglist;    

    public ClassInstanceCreationExpression(ParseTree pt) throws ASTException {
        List<ParseTree> subtrees = pt.getChildren();
        name = Name.parseName(subtrees.get(1));
        arglist = null;
        if (subtrees.size() > 4) {
            arglist = Expression.parseArglist(subtrees.get(3));
        }
    }
    
	public void accept(Visitor v) throws Exception {
		v.visit(this);
	}
}
