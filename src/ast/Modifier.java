package ast;

import java.util.LinkedList;
import java.util.List;

import parser.ParseTree;
import exceptions.ASTException;

public class Modifier implements Next{
    public static final Modifier PUBLIC = new Modifier(1);
    public static final Modifier PROTECTED = new Modifier(2);
    public static final Modifier STATIC = new Modifier(3);
    public static final Modifier ABSTRACT = new Modifier(4);
    public static final Modifier FINAL = new Modifier(5);
    public static final Modifier NATIVE = new Modifier(6);

    private Modifier next = null;
    private int mod_val = 0;
    private Modifier mod = this;

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

    private Modifier(int modifier) {
        mod_val = modifier;
    }

    public Modifier(Modifier m) {
        mod = m;
    }

    private void parseSingleModifier(ParseTree pt) {
        ParseTree child = pt.getChildren().get(0);
        switch (child.getTokenType()) {
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
    public String toString() {
        int val;
        if (this.mod_val == 0)
            val = this.mod.mod_val;
        else
            val = this.mod_val;

        switch (val) {
        case 1:
            return "public";
        case 2:
            return "protected";
        case 3:
            return "static";
        case 4:
            return "abstract";
        case 5:
            return "final";
        case 6:
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
        } else {
            return false;
        }
    }

    public void accept(Visitor v) throws Exception {
        v.visit(this);
    }

    public static void main(String args[]) throws ASTException {
        Modifier m = new Modifier(Modifier.FINAL);
        System.out.println(m.equals(Modifier.FINAL));
        List<Modifier> ml = new LinkedList<Modifier>();
        ml.add(m);
        System.out.println(ml.contains(Modifier.FINAL));
    }
}
