package code_generation;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utility.StringUtility;
import ast.AST;
import ast.FieldDeclaration;
import ast.MethodDeclaration;
import ast.Modifier;
import ast.TypeDeclaration;
import ast.Visitor;
import ast.WhileStatement;
import environment.TraversalVisitor;

public class CodeGenerator extends TraversalVisitor {
    StatementCodeGenerator stmtGen;
    ExpressionCodeGenerator expGen;
    Map<Integer, String> SigOffsets = new HashMap<Integer, String>();
    static boolean debug = true;
    public CodeGenerator() {
        stmtGen = new StatementCodeGenerator();
        expGen = new ExpressionCodeGenerator();
    }

    public void visit(TypeDeclaration node) throws Exception {
        StringBuilder dataSection = new StringBuilder();
        StringBuilder vTableText = new StringBuilder();
        StringBuilder textSection = new StringBuilder();
        StringUtility.appendLine(vTableText, "section. text");
        StringUtility.appendLine(vTableText, "gloabl VTable#" + SigHelper.getClassSig(node));
        StringUtility.appendIndLn(vTableText, "VTable#" + SigHelper.getClassSig(node) + ":");        
        
        // creating .data section for static field
        StringUtility.appendLine(dataSection, "section .data");
        for (FieldDeclaration fDecl : node.getEnvironment().fields.values()) {
            if (fDecl.modifiers.contains(Modifier.STATIC)) {
                putFieldInData(dataSection, fDecl, node);
            }
            
        }
        for (FieldDeclaration fDecl : node.getEnvironment().getEnclosing().fields.values()) {
            if (fDecl.modifiers.contains(Modifier.STATIC)) {
                putFieldInData(dataSection, fDecl, node);
            }
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
        

        for (Map.Entry<String, MethodDeclaration> entry : node.getEnvironment().methods.entrySet()) {
            staticMethodVTableHandler(entry, node, textSection);
        }
        
        for (Map.Entry<String, MethodDeclaration> entry : node.getEnvironment().getEnclosing().methods.entrySet()) {
            staticMethodVTableHandler(entry, node, textSection);
        }
        
        
        for (Integer i = 0; i < SigOffsets.size(); i++) {
            StringUtility.appendLine(vTableText, "dd " + SigOffsets.get(i), 2);
        }

        if (debug) {
            // System.out.println(vTableText.toString());
            System.out.println(textSection.toString());
            // System.out.println(dataSection.toString());
        }
        
        for (MethodDeclaration mDecl: node.getEnvironment().methods.values()) {
            mDecl.accept(this);
        }
        
        // for (MethodDeclaration mDecl:
        // node.getEnvironment().getEnclosing().methods.values()) {
        // mDecl.accept(this);
        // }
    }
    
    private void staticMethodVTableHandler(Map.Entry<String, MethodDeclaration> entry, TypeDeclaration node, StringBuilder textSection) throws Exception {
        String mName = entry.getKey();
        MethodDeclaration mDecl = entry.getValue();
        String methodSig = SigHelper.getMethodSig(node, mDecl);
        String methodSigInDec = SigHelper.getMethodSig(mDecl);
        if (mDecl.modifiers.contains(Modifier.STATIC)) {
            StringUtility.appendLine(textSection, "gloabl " + methodSig);
            StringUtility.appendIndLn(textSection, methodSig + ":");
            StringUtility.appendLine(textSection, "dd " + methodSigInDec + "implementation", 2);
        } else {
            int offSet = node.getMethodOffSet(mName);
            SigOffsets.put(offSet, methodSigInDec + "implementation");
        }
    }

    private void putFieldInData(StringBuilder sb, FieldDeclaration fDecl, TypeDeclaration typeDec) {
        String fieldSig = SigHelper.getStaticFieldSig(typeDec, fDecl);
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

    public static void generate(List<AST> trees) throws Exception {
        for (AST t : trees) {
            Visitor rv = new CodeGenerator();
            if (debug) {
                if (t.root.types.get(0).getFullName().contains("String")) {
                    System.out.println(t.root.types.get(0).getFullName().toString());
                    t.root.accept(rv);
                }
            } else {
                t.root.accept(rv);
            }
        }
    }
}
