package code_generation;

import utility.StringUtility;
import ast.BooleanLiteral;
import ast.CharacterLiteral;
import ast.IntegerLiteral;
import ast.NullLiteral;
import ast.StringLiteral;
import environment.TraversalVisitor;

public class ExpressionCodeGenerator extends TraversalVisitor {

    private static final String FALSE = "0x0";
    private static final String TRUE = "0xffffffff";
    private int stringLitCounter = 0;

    // String is Object.
    public void visit(StringLiteral node) throws Exception {
        // can use counter because string literal is not global.
        stringLitCounter++;
        // TODO:integrate stringLitData into data section.
        StringBuilder stringLitData = new StringBuilder();
        StringUtility.appendLine(stringLitData, "STRING_" + stringLitCounter + ":" + "\t; define label for string literal");
        StringUtility.appendLine(stringLitData, "\t" + "dw " + '\'' + node.value + '\'');

        node.attachCode("mov eax, " + "STRING_" + stringLitCounter);
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
        String charText;

        // Assuming octal is valid.
        if (node.value.length() > 3) {
            charText = "mov eax, " + "0o" + node.value.substring(1);
        } else {
            charText = "mov eax, " + node.value;
        }

        node.attachCode(charText);
    }

    public void visit(IntegerLiteral node) throws Exception {
        String intText;
        intText = "mov eax, " + Integer.valueOf(node.value);
        node.attachCode(intText);
    }
}
