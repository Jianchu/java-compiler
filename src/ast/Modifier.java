package ast;

import parser.ParseTree;

public class Modifier {
	public static final int PUBLIC = 1;
	public static final int PROTECTED = 2;
	public static final int STATIC = 3;
	public static final int ABSTRACT = 4;
	public static final int FINAL = 5;
	public static final int NATIVE = 6;
	
	public Modifier next;
	private int mod;
	public Modifier(ParseTree pt) {
		for (ParseTree child : pt.getChildren()) {
			switch (child.getTokenType()) {
			case Modifiers:
				next = new Modifier(child);
				break;
			case Modifier:
				parseSingleModifier(child);
			}
		}
	}
	
	private void parseSingleModifier(ParseTree pt) {
		ParseTree child = pt.getChildren().get(0);
		switch(child.getTokenType()) {
		case PUBLIC:
			mod = this.PUBLIC;
		case PROTECTED:
			mod = this.PROTECTED;
		case STATIC:
			mod = this.STATIC;
		case ABSTRACT:
			mod = this.ABSTRACT;
		case FINAL:
			mod = this.FINAL;
		case NATIVE:
			mod = this.NATIVE;
		}
	}
}
