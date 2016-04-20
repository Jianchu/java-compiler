package ast;


public class ThisExpression extends Expression {

    public ThisExpression() {

    }

    public void accept(Visitor v) throws Exception {
        v.visit(this);
    }
}
