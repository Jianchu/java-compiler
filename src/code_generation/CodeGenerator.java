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
    private static StringBuilder[] staticFieldInit = {new StringBuilder(), new StringBuilder()};
    private static StringBuilder[] instanceFieldInit = {new StringBuilder(), new StringBuilder()};
    TypeDeclaration currentTypeDec;
    static boolean debug = true;

    public CodeGenerator() {
        stmtGen = new StatementCodeGenerator();
        expGen = new ExpressionCodeGenerator();
    }

    public void visit(TypeDeclaration node) throws Exception {
        this.currentTypeDec = node;
        String classSig = SigHelper.getClassSig(node);
        String testSig = classSig + "#test$$";
        StringUtility.appendLine(instanceFieldInit[0], classSig + "$instance_field_init:");
        StringBuilder dataSection = new StringBuilder();
        StringBuilder vTableText = new StringBuilder();
        StringBuilder textSection = new StringBuilder();
        StringBuilder start = new StringBuilder();
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
            System.out.println(getInstanceFieldInit());
        }
        
        for (MethodDeclaration mDecl: node.getEnvironment().methods.values()) {
            if (SigHelper.getMethodSig(mDecl).equals(testSig)) {
                generateStart(start, testSig);
                System.out.println(SigHelper.getMethodSig(mDecl));
            }
            mDecl.accept(this);
        }

    }
    
    private void generateStart(StringBuilder start, String testSig) {
        StringUtility.appendLine(start, "gloabl _start");
        StringUtility.appendIndLn(start, "_start:");
        StringUtility.appendLine(start, "call " + testSig, 2);
        StringUtility.appendLine(start, "call debexit", 2);
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

    private void putFieldInData(StringBuilder sb, FieldDeclaration fDecl, TypeDeclaration typeDec) throws Exception {
        String fieldSig = SigHelper.getFieldSig(typeDec, fDecl);
        StringUtility.appendLine(staticFieldInit[0], "call static_init_" + fieldSig, 2);
        StringUtility.appendLine(sb, "global " + fieldSig + "\t; define global label for field");
        StringUtility.appendLine(sb, fieldSig + ":" + "\t; label start");
        StringUtility.appendLine(sb, "\t" + "dw 0x0" + "\t; default value: 0 false null");
    }

    public void visit(FieldDeclaration node) throws Exception {
        String fieldSig = SigHelper.getFieldSig(currentTypeDec, node);
        StringBuilder fieldAssemblyText = new StringBuilder();
        for (Modifier im : node.modifiers) {
            im.accept(this);
        }
        node.type.accept(this);
        
        if (node.initializer != null) {
            node.initializer.accept(expGen);
            String initCode = node.initializer.getCode();
            if (initCode == null) {
                initCode = "; no right hand side yet.";
            }
            if (node.modifiers.contains(Modifier.STATIC)) {
                StringUtility.appendIndLn(staticFieldInit[1], "static_init_" + fieldSig + ":");
                StringUtility.appendLine(staticFieldInit[1], initCode, 2);
                StringUtility.appendLine(staticFieldInit[1], "mov " + fieldSig + " eax", 2);
            } else {
                StringUtility.appendIndLn(instanceFieldInit[0], "call instance_init_" + fieldSig);
                StringUtility.appendIndLn(instanceFieldInit[1], "instance_init_" + fieldSig + ":");
                // TODO: add a method for evaluating the address of instance field, and putting it to eax. 
                StringUtility.appendLine(instanceFieldInit[1], "push eax /t;store field address", 2);
                StringUtility.appendLine(instanceFieldInit[1], initCode, 2);
                StringUtility.appendLine(instanceFieldInit[1], "mov edx, eax /t; put value of field to edx", 2);
                StringUtility.appendLine(instanceFieldInit[1], "pop eax /t; pop field address back to eax", 2);
                StringUtility.appendLine(instanceFieldInit[1], "mov dword [eax], edx /t; initiallize field", 2);
                
            }
        }
        node.attachCode(fieldAssemblyText.toString());
    }

    public void visit(WhileStatement node) throws Exception {
        node.accept(stmtGen);
    }

    protected static String getStaticFieldInit() {
        return staticFieldInit[0].toString() + staticFieldInit[1].toString();
    }
    
    private String getInstanceFieldInit() {
        return instanceFieldInit[0].toString() + instanceFieldInit[1].toString();
    }

    public static void generate(List<AST> trees) throws Exception {
        for (AST t : trees) {
            Visitor rv = new CodeGenerator();
            if (debug) {
                if (t.root.types.get(0).getFullName().contains("Byte")) {
                    System.out.println(t.root.types.get(0).getFullName().toString());
                    t.root.accept(rv);
                }
            } else {
                // t.root.accept(rv);
            }
        }
    }
}
