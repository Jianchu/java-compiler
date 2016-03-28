package code_generation;

import utility.StringUtility;
import ast.WhileStatement;
import environment.TraversalVisitor;

public class StatementCodeGenerator extends TraversalVisitor {

    private static final String FALSE = "0x0";
    private static final String TRUE = "0xffffffff";
    private ExpressionCodeGenerator expGen;
    private Integer loopCounter = 0;

    public StatementCodeGenerator() {
        this.expGen = new ExpressionCodeGenerator();
    }

    public void visit(WhileStatement node) throws Exception {
        loopCounter++;
        StringBuilder whileAssemblyText = new StringBuilder();
        StringUtility.appendLine(whileAssemblyText, "jmp COND_" + loopCounter + ":");
        StringUtility.appendLine(whileAssemblyText, "LOOP_" + loopCounter + ":");
        if (node.whileStatement != null) {
            node.whileStatement.accept(this);
            StringUtility.appendLine(whileAssemblyText, node.whileStatement.getCode());
        }
        StringUtility.appendLine(whileAssemblyText, "COND_" + loopCounter + ":");
        
        if (node.whileCondition != null) {
            node.whileCondition.accept(expGen);
            StringUtility.appendLine(whileAssemblyText, node.whileCondition.getCode());
        }
        StringUtility.appendLine(whileAssemblyText, "cmp eax, " + FALSE);
        StringUtility.appendLine(whileAssemblyText, "jne LOOP_" + loopCounter);

        node.attachCode(whileAssemblyText.toString());
        // visitNextStatement(node);
    }

}
