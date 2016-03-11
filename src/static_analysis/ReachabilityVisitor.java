package static_analysis;

import ast.Block;
import ast.ExpressionStatement;
import ast.ForStatement;
import ast.IfStatement;
import ast.ReturnStatement;
import ast.VariableDeclarationStatement;
import ast.WhileStatement;
import environment.TraversalVisitor;

public class ReachabilityVisitor extends TraversalVisitor {

    @Override
    public void visit(Block node) throws Exception {
        super.visit(node);
    }

    @Override
    public void visit(ExpressionStatement node) throws Exception {
        super.visit(node);
    }

    @Override
    public void visit(ForStatement node) throws Exception {
        super.visit(node);
    }

    @Override
    public void visit(IfStatement node) throws Exception {
        super.visit(node);
    }

    @Override
    public void visit(ReturnStatement node) throws Exception {
        super.visit(node);
    }

    @Override
    public void visit(VariableDeclarationStatement node) throws Exception {
        super.visit(node);
    }

    @Override
    public void visit(WhileStatement node) throws Exception {
        super.visit(node);
    }
}
