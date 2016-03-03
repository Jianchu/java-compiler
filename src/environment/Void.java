package environment;

import ast.Type;
import ast.Visitor;

public class Void extends Type {
    @Override
    public String toString() {
        return "void";
    }

    @Override
    public void accept(Visitor v) throws Exception {
    }
}