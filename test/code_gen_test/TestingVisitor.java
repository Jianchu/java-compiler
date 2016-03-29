package code_gen_test;

import java.util.HashSet;
import java.util.List;

import ast.AST;
import ast.FieldDeclaration;
import ast.MethodDeclaration;
import ast.Modifier;
import ast.VariableDeclaration;
import ast.Visitor;
import code_generation.ExpressionCodeGenerator;
import code_generation.SigHelper;
import environment.TraversalVisitor;

public class TestingVisitor extends TraversalVisitor {
    
    public static void test(List<AST> trees) throws Exception {
        for (AST t : trees) {
            Visitor rv = new ExpressionCodeGenerator(new HashSet<String>());
            if (t.root.types.get(0).getFullName().contains("J1e_A_CastToArray")) {
                System.out.println(t.root.types.get(0).getFullName().toString());
                t.root.accept(rv);
            }
        }
    }

    public void visit(MethodDeclaration node) throws Exception {
        System.out.println(SigHelper.getMethodSigWithImp(node));
        for (Modifier mo : node.modifiers) {
            mo.accept(this);
        }
        if (!node.isConstructor) {
            // check for void
            if (node.returnType != null) {
                node.returnType.accept(this);
            }
        }
        for (VariableDeclaration va : node.parameters) {
            va.accept(this);
        }
        if (node.body != null) {
            node.body.accept(this);
        }
    }

    public void visit(FieldDeclaration node) throws Exception {
        System.out.println(SigHelper.getFieldSig(node));
        for (Modifier im : node.modifiers) {
            im.accept(this);
        }
        node.type.accept(this);
        if (node.initializer != null) {
            node.initializer.accept(this);
        }
    }

}
