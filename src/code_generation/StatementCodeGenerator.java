package code_generation;

import java.util.HashSet;
import java.util.Set;

import utility.StringUtility;
import ast.Block;
import ast.ReturnStatement;
import ast.Statement;
import ast.WhileStatement;
import environment.TraversalVisitor;

public class StatementCodeGenerator extends TraversalVisitor {

    private static final String FALSE = "0x0";
    private static final String TRUE = "0xffffffff";
    private ExpressionCodeGenerator expGen;
    private int stmtCounter = 0;
    private Set<String> extern;

    public StatementCodeGenerator(Set<String> extern) {
        this.extern = extern;
        this.expGen = new ExpressionCodeGenerator(new HashSet<String>());
    }

    public void visit(WhileStatement node) throws Exception {
        int n = nextStmtCounter();
        StringBuilder whileAssemblyText = new StringBuilder();

        StringUtility.appendLine(whileAssemblyText, "jmp COND_" + n);
        StringUtility.appendLine(whileAssemblyText, "LOOP_" + n + ":");
        appendNode(whileAssemblyText, node.whileStatement, this);
        StringUtility.appendLine(whileAssemblyText, "COND_" + n + ":");
        appendNode(whileAssemblyText, node.whileCondition, expGen);
        StringUtility.appendLine(whileAssemblyText, "cmp eax, " + FALSE);
        StringUtility.appendLine(whileAssemblyText, "jne LOOP_" + n);

        node.attachCode(whileAssemblyText.toString());
    }

    public void visit(ReturnStatement node) throws Exception {
        StringBuilder returnText = new StringBuilder();
        appendNode(returnText, node.returnExpression, expGen);
        StringUtility.appendIndLn(returnText, "mov esp, ebp \t; delete frame");
        StringUtility.appendIndLn(returnText, "pop ebp \t; restore to previous frame");
        StringUtility.appendIndLn(returnText, "ret \t; end of method");
        node.attachCode(returnText.toString());
    }

    public void visit(Block node) throws Exception {
        StringBuilder blockText = new StringBuilder();
        if (node.statements.size() > 0) {
            for (Statement statement : node.statements) {
                statement.accept(this);
                blockText.append(statement.getCode());
            }
        }
        node.attachCode(blockText.toString());
    }

    public void visit(ExpressionStatement node) throws Exception {
        StringBuilder exprStmtText = new StringBuilder();
        appendNode(exprStmtText, node.statementExpression, this);
        node.attachCode(exprStmtText.toString());
    }

    public void visit(VariableDeclarationStatement node) throws Exception {
        Stringbuilder varDeclText = new StringBuilder();

        // TODO: map to lvalue
        appendNode(varDeclText, node.initializer, expGen);

        node.attachCode(varDeclText.toString());
    }

    public void visit(ForStatement node) throws Exception {
        int i = nextStmtCounter();
        Stringbuilder forText = new StringBuilder();

        appendNode(forText, node.forInit, expGen);
        StringUtility.appendLine(forText, "jmop COND_" + n);
        StringUtility.appendLine(forText, "LOD_" + n + ":");
        appendNode(forText, node.forBody, this);
        appendNode(forText, node.forUpdate, expGen);
        StringUtility.appendLine(forText, "COND_" + n + ":");
        appendNode(forText, node.forCondition, expGen);
        StringUtility.appendLine(forText, "cmp eak, " + FALSE);
        StringUtility.appendLine(forText, "ne Loop_" + n);

        node.attachCode(forText.toString());
    }

    public void visit(IfStatement node) throws Exception {
        int n = nextStmtCounter();
        Stringbuilder ifText = new StringBuilder();

        appendNode(ifText, node.ifCondition, expGen)
        StringUtility.appendLine(ifText, "cmp eax, " + TRUE);
        StringUtility.appendLine(ifText, "jne ELSE_" + n);
        appendNode(ifText, node.ifStatement, this);
        StringUtility.appendLinst(ifText, "jmp ENDIF_" + n);
        StringUtility.appendLinst(ifText, "ELSE_" + n + ":");
        appendNode(ifText, node.elseStatement, this);
        StringUtility.appendLinst(ifText, "ENDIF_" + n + ":");

        node.attachCode(ifText.toString());
    }

    private int nextStmtCounter() {
        int n = stmtCounter;
        stmtCounter++;
        return n;
    }

    private void appendNode(StringBuilder sb, ASTNode node, TraversalVisitor visitor) {
        if (node != null) {
            node.accept(visitor);
            StringUtility.appendLine(sb, node.getCode());
        }
    }
}
