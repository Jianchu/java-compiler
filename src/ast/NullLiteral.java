package ast;


/**
 * for now empty class is enough.
 * @author zanel
 *
 */
public class NullLiteral extends Expression {

    public NullLiteral() {

    }

    public void accept(Visitor v) throws Exception {
        v.visit(this);
    }
}
