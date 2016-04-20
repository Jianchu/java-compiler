package code_generation;

import ast.Block;
import ast.MethodDeclaration;
import ast.Modifier;
import ast.VariableDeclaration;
import environment.TraversalVisitor;

public class VariableOffSetVisitor extends TraversalVisitor{
    int offsetCounter = 0; // local variable offset starts from 0
    MethodDeclaration currMethod;
    boolean debug = false;

    @Override
    public void visit(MethodDeclaration node) throws Exception {
        currMethod = node;
        if (debug) {
            System.out.println(node.id);
        }

        for (Modifier mo : node.modifiers) {
            mo.accept(this);
        }
        if (!node.isConstructor) {
            // check for void
            if (node.returnType != null) {
                node.returnType.accept(this);
            }
        }
        
        // compute offset for arguments
        int paramMaxOffSet = -node.parameters.size();
        for (int i = 0; i < node.parameters.size(); i++) {
            // parameter offsets are negative numbers
            node.addVarOffSet(node.parameters.get(i), paramMaxOffSet + i);
        }
        
        if (node.body != null) {
            offsetCounter = 0; // initialize
            node.body.accept(this);
            offsetCounter = 0; // reset
        }
        
        currMethod = null;
        
    }

    @Override
    public void visit(Block node) throws Exception {
        int oldCounter = offsetCounter; // store for later
        super.visit(node);
        // if (node.statements.size() > 0) {
        // Statement first = node.statements.get(0);
        // first.accept(this);
        // }

        // for (Statement s : node.statements) {
        // s.accept(this);
        // }

        offsetCounter = oldCounter;
    }

    @Override
    public void visit(VariableDeclaration node) throws Exception {
        node.type.accept(this);
        if (node.initializer != null) {
            node.initializer.accept(this);
        }
        if (debug) {
            System.out.println("\t" + node.id + " " + offsetCounter);
        }
        // store offset
        currMethod.addVarOffSet(node, offsetCounter);
        if (currMethod.frameSize < offsetCounter + 1) {
            currMethod.frameSize = offsetCounter + 1;
        }
        currMethod.frameSize++; // increment framesize
        offsetCounter ++;
        
        if (debug) {
            System.out.println(node.id);
        }
    }

}
