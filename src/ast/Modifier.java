package ast;

import parser.ParseTree;
import exceptions.ASTException;

public class Modifier implements Next{
	public static final int PUBLIC = 1;
	public static final int PROTECTED = 2;
	public static final int STATIC = 3;
	public static final int ABSTRACT = 4;
	public static final int FINAL = 5;
	public static final int NATIVE = 6;
	
	private Modifier next = null;
	public int mod = 0;
	public Modifier(ParseTree pt) {
		for (ParseTree child : pt.getChildren()) {
			switch (child.getTokenType()) {
			case Modifiers:
				next = new Modifier(child);
				break;
			case Modifier:
				parseSingleModifier(child);
				break;
			}
		}
	}
	
	public Modifier(int modifier) throws ASTException {
		if (modifier < 7 && modifier > 0)
			mod = modifier;
		else throw new ASTException("unsupported modifier value");
	}
	
	private void parseSingleModifier(ParseTree pt) {
		ParseTree child = pt.getChildren().get(0);
		switch(child.getTokenType()) {
		case PUBLIC:
			mod = this.PUBLIC;
			break;
		case PROTECTED:
			mod = this.PROTECTED;
			break;
		case STATIC:
			mod = this.STATIC;
			break;
		case ABSTRACT:
			mod = this.ABSTRACT;
			break;
		case FINAL:
			mod = this.FINAL;
			break;
		case NATIVE:
			mod = this.NATIVE;
			break;
		}
	}
	
	public boolean hasNext() {
		return next != null;
	}
	
	public Modifier next() {
		return next;
	}
	
	@Override
	public String toString(){
		switch(mod) {
		case PUBLIC:
			return "public";
		case PROTECTED:
			return "protected";
		case STATIC:
			return "static";
		case ABSTRACT:
			return "abstract";
		case FINAL:
			return "final";
		case NATIVE:
			return "native";
		default:
			throw new RuntimeException("unrecoginzed modifier");
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Modifier) {
			Modifier m = (Modifier) o;
			return m.mod == this.mod;
		} else if (o instanceof Integer) {
			return ((Integer) o).equals(this.mod);
		} else
			return false;
	}
	
    public void accept(Visitor v) throws Exception {
        v.visit(this);
    }
    
    public static void main(String args[]) throws ASTException {
    	Modifier m = new Modifier(Modifier.FINAL);
    	System.out.println(m.equals(Modifier.FINAL));
    }
}
