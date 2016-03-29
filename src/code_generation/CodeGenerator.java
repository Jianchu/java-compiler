package code_generation;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utility.StringUtility;
import ast.AST;
import ast.Block;
import ast.BodyDeclaration;
import ast.FieldDeclaration;
import ast.MethodDeclaration;
import ast.Modifier;
import ast.ReturnStatement;
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
    Set<String> extern;

    public CodeGenerator() {
        this.extern = new HashSet<String>();
        stmtGen = new StatementCodeGenerator(extern);
        expGen = new ExpressionCodeGenerator(extern);
    }

    public void visit(TypeDeclaration node) throws Exception {
        this.currentTypeDec = node;
        ExpressionCodeGenerator.stringLitData.setLength(0);
        String classSig = SigHelper.getClassSig(node);
        String testSig = classSig + "#test$$implementation";
        StringUtility.appendLine(instanceFieldInit[0], "instance_field_init$" + classSig + ":");
        StringBuilder dataSection = new StringBuilder();
        StringBuilder vTableText = new StringBuilder();
        StringBuilder textSection = new StringBuilder();
        StringBuilder start = new StringBuilder();
        StringBuilder header = new StringBuilder();
        StringUtility.appendLine(textSection, "section .text");
        StringUtility.appendLine(vTableText, "global VTable#" + SigHelper.getClassSig(node));
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
        StringUtility.appendIndLn(instanceFieldInit[0], "ret");
        
        // methods
        

        for (Map.Entry<String, MethodDeclaration> entry : node.getEnvironment().methods.entrySet()) {
            staticMethodVTableHandler(entry, node, textSection);
        }
        
        for (Map.Entry<String, MethodDeclaration> entry : node.getEnvironment().getEnclosing().methods.entrySet()) {
            String methodSigInDec = SigHelper.getMethodSigWithImp(entry.getValue());
            StringUtility.appendLine(header, "extern " + methodSigInDec);
            staticMethodVTableHandler(entry, node, textSection);
        }
        
        if (!node.isInterface) {
            for (Integer i = 0; i < SigOffsets.size(); i++) {
                StringUtility.appendLine(vTableText, "dd " + SigOffsets.get(i), 2);
                
            }
        }
        
        	
        for (BodyDeclaration bDecl : node.members) {
        	if (bDecl instanceof MethodDeclaration) {
        		MethodDeclaration mDecl = (MethodDeclaration) bDecl;
	            if (SigHelper.getMethodSigWithImp(mDecl).equals(testSig)) {
	                StringUtility.appendLine(header, "extern __debexit");
	                generateStart(start, testSig);
	            }
	            mDecl.accept(this);
	            String methodText = mDecl.getCode();
	            if (methodText == null) {
	                methodText = "; no method body yet\n";
	            }
	            textSection.append(methodText);
        	}
       	}
        
        dataSection.append(ExpressionCodeGenerator.stringLitData);
        textSection.append(getInstanceFieldInit() + "\n");
        textSection.append(start + "\n");
        textSection.append(vTableText + "\n");
        StringUtility.appendLine(header, "extern __malloc");
        StringUtility.appendLine(header, "extern __exception");
        header.append("\n");
        node.attachCode(header.toString() + dataSection.toString() + textSection.toString());
    }
    
    private void generateStart(StringBuilder start, String testSig) {
        StringUtility.appendLine(start, "global _start");
        StringUtility.appendIndLn(start, "_start:");
        StringUtility.appendLine(start, "call " + testSig, 2);
        StringUtility.appendLine(start, "call __debexit", 2);
    }

    private void staticMethodVTableHandler(Map.Entry<String, MethodDeclaration> entry, TypeDeclaration node, StringBuilder textSection) throws Exception {
        String mName = entry.getKey();
        MethodDeclaration mDecl = entry.getValue();
        String methodSig = SigHelper.getMethodSig(node, mDecl);
        String methodSigInDec = SigHelper.getMethodSigWithImp(mDecl);
        if (mDecl.modifiers.contains(Modifier.STATIC)) {
            StringUtility.appendLine(textSection, "global " + methodSig);
            StringUtility.appendIndLn(textSection, methodSig + ":");
            StringUtility.appendLine(textSection, "dd " + methodSigInDec, 2);
        } else {
            if (!node.isInterface) {
                int offSet = node.getMethodOffSet(mName);
                SigOffsets.put(offSet, methodSigInDec);
            }
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
        // StringBuilder fieldAssemblyText = new StringBuilder();
        for (Modifier im : node.modifiers) {
            im.accept(this);
        }
        node.type.accept(this);
        
        if (node.initializer != null) {
            node.initializer.accept(expGen);
            String initCode = node.initializer.getCode();
            if (initCode == null) {
                initCode = "; no right hand side yet.\n";
            }
            if (node.modifiers.contains(Modifier.STATIC)) {
                StringUtility.appendIndLn(staticFieldInit[1], "static_init_" + fieldSig + ":");
                StringUtility.appendLine(staticFieldInit[1], initCode, 2);
                StringUtility.appendLine(staticFieldInit[1], "mov " + fieldSig + ", eax", 2);
            } else {
                StringUtility.appendIndLn(instanceFieldInit[0], "call instance_init_" + fieldSig);
                StringUtility.appendIndLn(instanceFieldInit[1], "instance_init_" + fieldSig + ":");
                // TODO: add a method for evaluating the address of instance field, and putting it to eax. 
                StringUtility.appendIndLn(instanceFieldInit[1], codeGenFieldAddr(node));
                StringUtility.appendLine(instanceFieldInit[1], "push eax \t;store field address", 2);
                StringUtility.appendLine(instanceFieldInit[1], initCode, 2);
                StringUtility.appendLine(instanceFieldInit[1], "mov edx, eax \t; put value of field to edx", 2);
                StringUtility.appendLine(instanceFieldInit[1], "pop eax \t; pop field address back to eax", 2);
                StringUtility.appendLine(instanceFieldInit[1], "mov dword [eax], edx \t; initiallize field", 2);
                
            }
        }
        // node.attachCode(fieldAssemblyText.toString());
    }

    private String codeGenFieldAddr(FieldDeclaration node) {
        // TODO Auto-generated method stub
        return null;
    }

    public void visit(MethodDeclaration node) throws Exception {
    	StringBuilder sb = new StringBuilder();
    	StringUtility.appendLine(sb, "global " + SigHelper.getMethodSigWithImp(node));
    	StringUtility.appendLine(sb, SigHelper.getMethodSigWithImp(node) + ":");	// generate method label
    	
		StringUtility.appendIndLn(sb, "push ebp \t; save old frame pointer");
		StringUtility.appendIndLn(sb, "mov ebp, esp \t; move ebp to top of stack");
		StringUtility.appendIndLn(sb, "sub esp, " + node.frameSize + "\t; space for local variables");
		
		// if constructor, call field initializer
		if (node.isConstructor) {
			TypeDeclaration tDecl = (TypeDeclaration) node.getParent();
			StringUtility.appendIndLn(sb, "push dword [ebp+8] \t; push object for initialiser"); 	// push object for initializer call
			StringUtility.appendIndLn(sb, "call " + SigHelper.instanceFieldInitSig(tDecl));
			StringUtility.appendIndLn(sb, "add esp, 4 \t; remove object for initializer");
		}
		
		
		if (node.body != null) {
			node.body.accept(this);
			sb.append(node.body.getCode());
		}
		
		// clean up in case there is no return statement
		StringUtility.appendIndLn(sb, "mov eax, 0 \t; in the case of no return, make sure eax is null"); 
		StringUtility.appendIndLn(sb, "mov esp, ebp \t; delete frame");
		StringUtility.appendIndLn(sb, "pop ebp \t; restore to previous frame");
		StringUtility.appendIndLn(sb, "ret \t; end of method");
		
		node.attachCode(sb.toString());
    }
    
    public void visit(WhileStatement node) throws Exception {
        node.accept(stmtGen);
    }

    public void visit(Block node) throws Exception {
        node.accept(stmtGen);
    }

    public void visit(ReturnStatement node) throws Exception {
        node.accept(stmtGen);
    }

    protected static String getStaticFieldInit() {
        String staticFieldInitString = staticFieldInit[0].toString() + staticFieldInit[1].toString();
        staticFieldInit[0].setLength(0);
        staticFieldInit[1].setLength(0);
        return staticFieldInitString;
    }
    
    private String getInstanceFieldInit() {
        String instanceFieldInitString = instanceFieldInit[0].toString() + instanceFieldInit[1].toString();
        instanceFieldInit[0].setLength(0);
        instanceFieldInit[1].setLength(0);
        return instanceFieldInitString;
    }

    public static void generate(List<AST> trees) throws Exception {
        for (AST t : trees) {
            Visitor rv = new CodeGenerator();
            t.root.accept(rv);
        }
    }
}
