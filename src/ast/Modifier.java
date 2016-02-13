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
	
	public Modifier next = null;
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

    public void accept(Visitor v) throws ASTException {
        v.visit(this);
    }
}
