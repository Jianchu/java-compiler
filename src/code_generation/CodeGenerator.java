package code_generation;

import ast.WhileStatement;
import environment.TraversalVisitor;

public class CodeGenerator extends TraversalVisitor {
    StatementCodeGenerator stmtGen;
    ExpressionCodeGenerator expGen;

    public CodeGenerator() {
        stmtGen = new StatementCodeGenerator();
        expGen = new ExpressionCodeGenerator();
    }

    public void visit(WhileStatement node) throws Exception {
        node.accept(stmtGen);
    }
}
