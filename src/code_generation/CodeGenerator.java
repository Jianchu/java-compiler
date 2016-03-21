package code_generation;


import utility.StringUtility;
import ast.FieldDeclaration;
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
