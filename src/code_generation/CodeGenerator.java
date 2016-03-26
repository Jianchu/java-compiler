package code_generation;


import java.util.HashMap;
import java.util.Map;

import utility.StringUtility;
import ast.FieldDeclaration;
import ast.MethodDeclaration;
import ast.Modifier;
import ast.TypeDeclaration;
import ast.WhileStatement;
import environment.TraversalVisitor;

public class CodeGenerator extends TraversalVisitor {
    StatementCodeGenerator stmtGen;
    ExpressionCodeGenerator expGen;

    public CodeGenerator() {
        stmtGen = new StatementCodeGenerator();
        expGen = new ExpressionCodeGenerator();
    }

    public void visit(TypeDeclaration node) throws Exception {
        StringBuilder dataSection = new StringBuilder();
        StringBuilder vTableText = new StringBuilder();
        StringUtility.appendLine(vTableText, "gloabl VTable_" + SigHelper.getClassSig(node));
        StringUtility.appendIndLn(vTableText, "VTable_" + SigHelper.getClassSig(node) + ":");        
        
        // creating .data section
        StringUtility.appendLine(dataSection, "section .data");
        for (FieldDeclaration fDecl : node.getEnvironment().fields.values()) {
            putFieldInData(dataSection, fDecl);
        }
        for (FieldDeclaration fDecl : node.getEnvironment().getEnclosing().fields.values()) {
            putFieldInData(dataSection, fDecl);
            // inherited fields
        }

        // creating .text section
        for (FieldDeclaration fDecl : node.getEnvironment().fields.values()) {
            fDecl.accept(this);

        }
        for (FieldDeclaration fDecl : node.getEnvironment().getEnclosing().fields.values()) {
            fDecl.accept(this);

            // inherited fields
        }
        
        // methods
        Map<Integer, String> SigOffsets = new HashMap<Integer, String>();
        
        for (String mName: node.getEnvironment().methods.keySet()) {
            MethodDeclaration mDecl = node.getEnvironment().methods.get(mName);
            String methodSig = SigHelper.getMethodSig(mDecl);
            if (mDecl.modifiers.contains(Modifier.STATIC)) {
                
            } else {
                int offSet = node.getMethodOffSet(mName);
                SigOffsets.put(offSet, methodSig);
            }
        }
        
        for (String mName : node.getEnvironment().getEnclosing().methods.keySet()) {
            MethodDeclaration mDecl = node.getEnvironment().getEnclosing().methods.get(mName);
            String methodSig = SigHelper.getMethodSig(mDecl);
            if (mDecl.modifiers.contains(Modifier.STATIC)) {
                
            } else {
                int offSet = node.getMethodOffSet(mName);
                SigOffsets.put(offSet, methodSig);
            }
        }
        
        for (Integer i = 0; i < SigOffsets.size(); i++) {
            StringUtility.appendLine(vTableText, "dd " + SigOffsets.get(i), 2);
        }
    }
    
    private void putFieldInData(StringBuilder sb, FieldDeclaration fDecl) {
        String fieldSig = SigHelper.getFieldSig(fDecl);
        StringUtility.appendLine(sb, "global " + fieldSig + "\t; define global label for field");
        StringUtility.appendLine(sb, fieldSig + ":" + "\t; label start");
        StringUtility.appendLine(sb, "\t" + "dw 0x0" + "\t; default value: 0 false null");
    }

    public void visit(FieldDeclaration node) throws Exception {
        String fieldSig = SigHelper.getFieldSig(node);
        StringBuilder fieldAssemblyText = new StringBuilder();
        
        for (Modifier im : node.modifiers) {
            im.accept(this);
        }
        
        node.type.accept(this);
        if (node.initializer != null) {
            node.initializer.accept(expGen);
            String initCode = node.initializer.getCode();
            StringUtility.appendLine(fieldAssemblyText, initCode);
            StringUtility.appendLine(fieldAssemblyText, "mov dword [" + fieldSig + "], eax" + "/t; initiallize field");
        }
        node.attachCode(fieldAssemblyText.toString());
    }

    public void visit(WhileStatement node) throws Exception {
        node.accept(stmtGen);
    }
}
