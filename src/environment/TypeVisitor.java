package environment;

import ast.ArrayType;
import ast.PrimitiveType;
import ast.QualifiedName;
import ast.SimpleName;
import ast.SimpleType;

/**
 * Responsible for type linking
 * @author zanel
 *
 */
public class TypeVisitor extends SemanticsVisitor {

    @Override
    public void visit(SimpleName node) throws Exception {
    }

    // Fully qualified names are easy
    @Override
    public void visit(QualifiedName node) throws Exception {
        // Search qualified name in global
    }

    @Override
    public void visit(ArrayType node) throws Exception {
    }

    @Override
    public void visit(PrimitiveType node) throws Exception {
    }

    @Override
    public void visit(SimpleType node) throws Exception {
    }
}
