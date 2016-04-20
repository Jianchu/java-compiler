package ast;

import parser.ParseTree;
import exceptions.ASTException;

public class ArrayType extends Type {
    public Type type;

    public ArrayType(ParseTree pt) throws ASTException {
        ParseTree child = pt.getFirstChild();
        switch (child.getTokenType()) {
        case PrimitiveType:
            type = new PrimitiveType(child);
            break;
        case Name:
            type = new SimpleType(Name.parseName(child));
            break;
        }
    }

    public ArrayType(Type elemType) {
        type = elemType;
    }

    public void accept(Visitor v) throws Exception {
        v.visit(this);
    }

    public String toString() {
        return type.toString() + "[]";
    }

    // @Override
    // public TypeDeclaration getDeclaration() {
    // throw new
    // RuntimeException("ArrayType do not support getting declaration. Get the type in array.");
    // }
}
