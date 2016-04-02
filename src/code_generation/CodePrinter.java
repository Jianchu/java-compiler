package code_generation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import utility.StringUtility;
import ast.AST;
import ast.ArrayType;
import ast.PrimitiveType;
import ast.PrimitiveType.Value;
import ast.TypeDeclaration;
import ast.Visitor;
import environment.TraversalVisitor;

public class CodePrinter extends TraversalVisitor {

    private File output = new File(System.getProperty("user.dir") + "/output");
    private String uglyText = UglyTableBuilder.getUgly();
    private String staticFieldInit = CodeGenerator.getStaticFieldInit();
    private String HierarchyTable = HierarchyTableBuilder.getHierarchyTable();

    public void printCode(List<AST> trees) throws Exception {
        if (!output.exists()) {
            output.mkdirs();
        } else {
            for (File file : output.listFiles()) {
                file.delete();
            }
        }
        writeUgly();
        writeStaticFieldInit();
        writeHierarchyTable();
        writePrimitiveVTable();
        for (AST t : trees) {
            Visitor rv = new CodePrinter();
            t.root.accept(rv);
        }
    }

    private void writeUgly() throws FileNotFoundException {
        File uglyFile = new File(output.getAbsolutePath() + "/ugly.s");
        PrintWriter writer = new PrintWriter(uglyFile);
        writer.write(uglyText);
        writer.close();
    }

    private void writeStaticFieldInit() throws FileNotFoundException {
        File staticFieldInitFile = new File(output.getAbsolutePath() + "/staticinit.s");
        PrintWriter writer = new PrintWriter(staticFieldInitFile);
        StringBuilder fileHead = new StringBuilder();
        StringUtility.appendLine(fileHead, "extern __malloc");
        StringUtility.appendLine(fileHead, "extern __exception");
        StringUtility.appendLine(fileHead, "extern __debexit");
        StringUtility.appendLine(fileHead, "section .text");
        writer.write(fileHead.toString());
        writer.write(staticFieldInit);
        writer.close();
    }

    private void writeHierarchyTable() throws FileNotFoundException {
        File hierarchyFile = new File(output.getAbsolutePath() + "/hierarchy.s");
        PrintWriter writer = new PrintWriter(hierarchyFile);
        writer.write(HierarchyTable);
        writer.close();
    }
    
    private void writePrimitiveVTable() throws FileNotFoundException {
        File primitiveVTableFile = new File(output.getAbsolutePath() + "/primitivevtable.s");
        PrintWriter writer = new PrintWriter(primitiveVTableFile);
        StringBuilder pvtable = new StringBuilder();
        StringBuilder header = new StringBuilder();
        header.append(PrimitiveVTableHelper(Value.BOOLEAN, pvtable));
        header.append(PrimitiveVTableHelper(Value.BYTE, pvtable));
        header.append(PrimitiveVTableHelper(Value.CHAR, pvtable));
        header.append(PrimitiveVTableHelper(Value.INT, pvtable));
        header.append(PrimitiveVTableHelper(Value.SHORT, pvtable));
        writer.write(header.toString() + pvtable.toString());
        writer.close();
    }
    
    private String PrimitiveVTableHelper(Value value, StringBuilder pvtable) {
        PrimitiveType primitiveType = new PrimitiveType(value);
        ArrayType arrayType = new ArrayType(primitiveType);
        StringBuilder extern = new StringBuilder();
        StringUtility.appendLine(extern, "extern " + SigHelper.getClassSigWithHierarchy(arrayType));
        StringUtility.appendLine(extern, "extern " + SigHelper.getClassSigWithHierarchy(primitiveType));
        StringUtility.appendLine(pvtable, "global " + SigHelper.getClssSigWithVTable(arrayType));
        StringUtility.appendIndLn(pvtable, SigHelper.getClssSigWithVTable(arrayType) + ":");   
        StringUtility.appendLine(pvtable, "dd " + SigHelper.getClassSigWithHierarchy(arrayType), 2);
        return extern.toString();
        
    }
    
    @Override
    public void visit(TypeDeclaration node) throws Exception {
        String fullName = node.getFullName();
        String classAssembly = node.getCode();
        if (classAssembly != null) {
            File classAssemblyFile = new File(output.getAbsolutePath() + "/" + fullName + ".s");
            PrintWriter writer = new PrintWriter(classAssemblyFile);
            writer.write(classAssembly);
            writer.close();
        }
    }
}
