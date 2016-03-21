package code_generation;


import utility.StringUtility;
import ast.FieldDeclaration;
import ast.Modifier;
import ast.WhileStatement;
import environment.TraversalVisitor;

public class CodeGenerator extends TraversalVisitor {
    StatementCodeGenerator stmtGen;
    ExpressionCodeGenerator expGen;

    public CodeGenerator() {
        stmtGen = new StatementCodeGenerator();
        expGen = new ExpressionCodeGenerator();
    }

    public void visit(FieldDeclaration node) throws Exception {
        String fieldSig = SigHelper.getFieldSig(node);
        StringBuilder fieldAssemblyText = new StringBuilder();
        
        for (Modifier im : node.modifiers) {
            im.accept(this);
        }
        
        if (node.modifiers.contains(Modifier.STATIC)) {
            StringUtility.appendLine(fieldAssemblyText, "global " + fieldSig + "\t; define global label for field");
            StringUtility.appendLine(fieldAssemblyText, fieldSig + ":" + "\t; label start");
            StringUtility.appendLine(fieldAssemblyText, "\t" + "dw 0x0" + "\t; default value: 0 false null");
        }
        
        node.type.accept(this);
        if (node.initializer != null) {
            node.initializer.accept(this);
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
