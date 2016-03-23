package code_generation;

import ast.BooleanLiteral;
import ast.CharacterLiteral;
import ast.IntegerLiteral;
import ast.NullLiteral;
import ast.StringLiteral;
import environment.TraversalVisitor;

public class ExpressionCodeGenerator extends TraversalVisitor {

    private static final String FALSE = "0x0";
    private static final String TRUE = "0xffffffff";

    public void visit(StringLiteral node) throws Exception {
        String stringSig = SigHelper.getConstantSig(node);
    }

    public void visit(NullLiteral node) throws Exception {
        node.attachCode("mov eax, " + FALSE);
    }

    public void visit(BooleanLiteral node) throws Exception {
        String booleanText;
        if (node.value == true) {
            booleanText = "mov eax, " + TRUE;
        } else {
            booleanText = "mov eax, " + FALSE;
        }
        node.attachCode(booleanText);
    }

    public void visit(CharacterLiteral node) throws Exception {
        char c = node.value.charAt(0);
        String charText;
        if (c == '\\' && node.value.length() < 3) {
            c = node.value.charAt(1);
            if (c == 'b') {
                c = '\b';
            } else if (c == 't') {
                c = '\t';
            } else if (c == 'n') {
                c = '\n';
            } else if (c == 'f') {
                c = '\f';
            } else if (c == 'r') {
                c = '\r';
            } else if (c == '"') {
                c = '\"';
            } else if (c == '\'') {
                c = '\'';
            } else if (c == '\\') {
                c = '\\';
            } else {
                c = '\0';
            }
            charText = "mov eax, " + c;
            // Assuming octal is valid.
        } else if (node.value.length() > 3) {
            charText = "mov eax, " + "0o" + node.value.substring(1);
        } else {
            charText = "mov eax, " + ((int) c);
        }
        node.attachCode(charText);
    }

    public void visit(IntegerLiteral node) throws Exception {
        String intText;
        intText = "mov eax, " + Integer.valueOf(node.value);
        node.attachCode(intText);
    }
}
