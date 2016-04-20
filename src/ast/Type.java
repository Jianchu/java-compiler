package ast;

import java.util.LinkedList;
import java.util.List;

import parser.ParseTree;
import exceptions.ASTException;

/**
 * For types. I have decide not to use QualifiedType for now.
 * 
 * 
 * 
 * @author zanel
 *
 */
public abstract class Type extends ASTNode{

    TypeDeclaration decl;

    /**
     * This method parse four types of parse tree nodes: Type, ClassType,
     * InterfaceType, ArrayType
     * 
     * @param pt
     * @return
     * @throws ASTException
     */
    public static Type parseType(ParseTree pt) throws ASTException {
        switch (pt.getTokenType()) {
        case Type:
            ParseTree PrimitiveOrRef = pt.getChildren().get(0);
            return parseType(PrimitiveOrRef);

        case PrimitiveType:
            return new PrimitiveType(pt);
        case ReferenceType:
            return parseType(pt.getFirstChild());
        case ClassOrInterfaceType:
            return new SimpleType(pt);
        case ClassType:
        case InterfaceType:
            return new SimpleType(pt.getFirstChild());

        case ArrayType:
            return new ArrayType(pt);
        }
        throw new ASTException("unexpected: " + pt.getTokenType());
    }

    public static List<Type> parseInterfaceTypeList(ParseTree pt)
            throws ASTException {
        List<Type> result = new LinkedList<Type>();
        for (ParseTree child : pt.getChildren()) {
            switch (child.getTokenType()) {
            case InterfaceTypeList:
                result.addAll(parseInterfaceTypeList(child));
                break;
            case InterfaceType:
                result.add(parseType(child));
            default:
                break;
            }
        }
        return result;
    }

    public void attachDeclaration(TypeDeclaration typeDecl) {
        decl = typeDecl;
    }

    public TypeDeclaration getDeclaration() {
        return decl;
    }

    public abstract String toString();

    public boolean equals(Object o) {
        if (!(o instanceof Type)) {
            return false;
        }

        Type t = (Type) o;
        if (this instanceof SimpleType) {
            return this.getDeclaration() == t.getDeclaration();
        } else if (this instanceof PrimitiveType) {
            return this.toString().equals(t.toString());
        } else if (this instanceof ArrayType || t instanceof ArrayType) {
            ArrayType thisArr = (ArrayType) this;
            ArrayType thatArr = (ArrayType) t;
            return thisArr.type.equals(thatArr.type);
        } else {
            return false;
        }

    }
}
