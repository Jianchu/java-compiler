package code_generation;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utility.StringUtility;
import ast.TypeDeclaration;

public class UglyTableBuilder {
    private static final Map<TypeDeclaration, List<String>> ugly = OffSet.ugly;
    private static StringBuilder uglyText = new StringBuilder();

    public static void build() {
        StringBuilder uglyHeader = new StringBuilder();
        Set<String> sigs = new HashSet<String>();
        StringUtility.appendLine(uglyText, "section .data " + uglyText);
        for (TypeDeclaration typeDec : ugly.keySet()) {
            String typeSig = SigHelper.getClassSigWithUgly(typeDec);
            StringUtility.appendLine(uglyText, "global " + typeSig);
            StringUtility.appendIndLn(uglyText, typeSig + ":");
            List<String> methodSigs = ugly.get(typeDec);
            for (String methodSig : methodSigs) {
                StringUtility.appendLine(uglyText, "dd " + methodSig, 2);
                sigs.add(methodSig);
            }
        }
        
        for (String sig : sigs) {
            StringUtility.appendLine(uglyHeader, "extern " + sig);
        }
        uglyHeader.append("\n");
        uglyText.insert(0, uglyHeader);
    }

    public static String getUgly() {
        return uglyText.toString();
    }
}
