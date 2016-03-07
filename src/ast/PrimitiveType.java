package ast;

import java.util.Set;
import java.util.TreeSet;

import parser.ParseTree;

public class PrimitiveType extends Type {
    public Value value;
    
    public PrimitiveType(ParseTree pt) {
        ParseTree child = pt.getFirstChild();
        switch (child.getTokenType()) {
        case BOOLEAN:
            value = Value.BOOLEAN;
            break;
        case NumericType:
            ParseTree Numeric = child.getFirstChild();
            ParseTree Integral = Numeric.getFirstChild();
            switch(Integral.getTokenType()) {
            case INT:
                value = Value.INT;
                break;
            case BYTE:
                value = Value.BYTE;
                break;
            case CHAR:
                value = Value.CHAR;
                break;
            case SHORT:
                value = Value.SHORT;
                break;
            }
            
        }
    }
    
    public PrimitiveType(Value value) {
        this.value = value;
    }

    public enum Value {
        BOOLEAN,
        INT,
        BYTE,
        CHAR,
        SHORT
    }
    
    @Override
    public String toString() {
        return value.toString().toLowerCase();
    }
    
    public void accept(Visitor v) throws Exception {
        v.visit(this);
    }
    
    public static Set<String> primitives() {
    	Set<String> primitives = new TreeSet<String>();
		for (Value v : Value.values()) {
			primitives.add(v.toString().toLowerCase());
		}
		return primitives;
    }
    
}
