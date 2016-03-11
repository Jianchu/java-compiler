package static_analysis;

import java.util.HashMap;
import java.util.Map;

import ast.Block;
import ast.ExpressionStatement;
import ast.ForStatement;
import ast.IfStatement;
import ast.ReturnStatement;
import ast.Statement;
import ast.VariableDeclarationStatement;
import ast.WhileStatement;
import environment.TraversalVisitor;
import exceptions.ReachabilityException;

public class ReachabilityVisitor extends TraversalVisitor {

    private Map<Statement, Boolean> outMap = new HashMap<Statement, Boolean>();
    
    @Override
    public void visit(Block node) throws Exception {
        super.visit(node);
        if (node.statements.size() > 0) {
            Statement firstStatement = node.statements.get(0);
            Statement nextStatement = null;
            firstStatement.accept(this);
            while (firstStatement.hasNext()) {
                nextStatement = firstStatement.next();
                if (!outMap.get(nextStatement).booleanValue()) {
                    throw new ReachabilityException("Unreachable statement");
                }
                nextStatement.accept(this);
                firstStatement = nextStatement;
            }
            if (nextStatement != null) {
                outMap.put(node, outMap.get(nextStatement));
            }
        }
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
        outMap.put(node, new Boolean(false));
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
