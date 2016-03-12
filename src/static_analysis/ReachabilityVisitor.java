package static_analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ast.AST;
import ast.Block;
import ast.CompilationUnit;
import ast.ExpressionStatement;
import ast.ForStatement;
import ast.IfStatement;
import ast.MethodDeclaration;
import ast.ReturnStatement;
import ast.Statement;
import ast.VariableDeclaration;
import ast.VariableDeclarationStatement;
import ast.Visitor;
import ast.WhileStatement;
import environment.TraversalVisitor;
import exceptions.ReachabilityException;

public class ReachabilityVisitor extends TraversalVisitor {

    private Map<Statement, Boolean> outMap = new HashMap<Statement, Boolean>();
    private Statement lastStatement;
    private String currentTypeName;

    // for testing
    @Override
    public void visit(CompilationUnit node) throws Exception {
        this.currentTypeName = node.types.get(0).getFullName();
        super.visit(node);
    }

    @Override
    public void visit(Block node) throws Exception {

        if (node.statements.size() > 0) {
            Statement currentStatement = node.statements.get(0);
            Statement nextStatement = null;
            currentStatement.accept(this);
            while (currentStatement.hasNext()) {

                nextStatement = currentStatement.next();
                if (!outMap.get(currentStatement).booleanValue()) {
                    throw new ReachabilityException("Unreachable statement");
                }
                nextStatement.accept(this);
                currentStatement = nextStatement;
            }
            if (currentStatement != null) {
                outMap.put(node, outMap.get(currentStatement));
                this.lastStatement = currentStatement;
            }
        } else {
            outMap.put(node, true);
        }
    }

    @Override
    public void visit(ExpressionStatement node) throws Exception {
        outMap.put(node, true);
    }

    @Override
    public void visit(ForStatement node) throws Exception {
        if (node.forBody != null) {
            node.forBody.accept(this);
        }
        int constantFlag = ConstantExpression.isConstant(node.forCondition);
        if (constantFlag == 0 || constantFlag == 2) {
            outMap.put(node, true);
        } else if (constantFlag == 1) {
            outMap.put(node, false);
        }
    }

    @Override
    public void visit(IfStatement node) throws Exception {
        if (node.hasElse) {
            Statement ifStatement = node.ifStatement;
            if (ifStatement != null) {
                ifStatement.accept(this);
            }
            Statement elseStatement = node.elseStatement;
            if (elseStatement != null) {
                elseStatement.accept(this);
            }
            boolean outOfNode = outMap.get(ifStatement) || outMap.get(elseStatement);
            outMap.put(node, outOfNode);
        } else {
            Statement ifStatement = node.ifStatement;
            if (ifStatement != null) {
                ifStatement.accept(this);
            }
            outMap.put(node, true);
        }
    }

    @Override
    public void visit(ReturnStatement node) throws Exception {
        outMap.put(node, false);
    }

    @Override
    public void visit(VariableDeclarationStatement node) throws Exception {
        outMap.put(node, true);
    }

    @Override
    public void visit(WhileStatement node) throws Exception {
        if (node.whileStatement != null) {
            node.whileStatement.accept(this);
        }
        int constantFlag = ConstantExpression.isConstant(node.whileCondition);
        if (constantFlag == 0 || constantFlag == 2) {
            outMap.put(node, true);
        } else if (constantFlag == 1) {
            outMap.put(node, false);
        }
    }

    @Override
    public void visit(MethodDeclaration node) throws Exception {
        outMap.put(node.body, true);
        if (node.body != null) {
            node.body.accept(this);
            if (node.returnType != null) {
                if (!(this.lastStatement instanceof ReturnStatement)) {
                    if (this.outMap.get(this.lastStatement)) {
                        throw new ReachabilityException("The last statement of a method whose return type is not void must be a return statement");
                    }
                }
            }
            this.lastStatement = null;
        }
    }

    public static void checkReachability(List<AST> trees) throws Exception {
        for (AST t : trees) {
            Visitor rv = new ReachabilityVisitor();
            t.root.accept(rv);
        }
    }
}
