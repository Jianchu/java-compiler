package code_generation;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utility.StringUtility;
import ast.AST;
import ast.BodyDeclaration;
import ast.FieldDeclaration;
import ast.MethodDeclaration;
import ast.Modifier;
import ast.TypeDeclaration;
import ast.Visitor;
import environment.TraversalVisitor;

public class CodeGenerator extends TraversalVisitor {
    StatementCodeGenerator stmtGen;
    ExpressionCodeGenerator expGen;
    Map<Integer, String> SigOffsets = new HashMap<Integer, String>();
    static StringBuilder[] staticFieldInit = {new StringBuilder(), new StringBuilder()};
    static StringBuilder[] instanceFieldInit = {new StringBuilder(), new StringBuilder()};
    TypeDeclaration currentTypeDec;
    Set<String> extern;
    Set<String> exclude;
    StringBuilder dataSection;
    static Set<String> staticInitExtern = new HashSet<String>();

    public CodeGenerator() {
        this.extern = new HashSet<String>();
        this.exclude = new HashSet<String>();
        dataSection = new StringBuilder();
        expGen = new ExpressionCodeGenerator(extern, dataSection);
        stmtGen = new StatementCodeGenerator(extern, dataSection, expGen);
    }

    public void visit(TypeDeclaration node) throws Exception {
        this.currentTypeDec = node;
        this.exclude.add(SigHelper.getClssSigWithVTable(node));
        ExpressionCodeGenerator.stringLitData.setLength(0);
        String classSig = SigHelper.getClassSig(node);
        String testSig = classSig + "#test$$implementation";
        StringUtility.appendLine(instanceFieldInit[0], "instance_field_init$" + classSig + ":");
        StringBuilder vTableText = new StringBuilder();
        StringBuilder textSection = new StringBuilder();
        StringBuilder start = new StringBuilder();
        StringUtility.appendLine(textSection, "section .text");
        StringUtility.appendLine(vTableText, "global VTable#" + SigHelper.getClassSig(node));
        StringUtility.appendIndLn(vTableText, "VTable#" + SigHelper.getClassSig(node) + ":");        
        // creating .data section for static field
        StringUtility.appendLine(dataSection, "section .data");
        StringUtility.appendLine(dataSection, "align 4");
        for (FieldDeclaration fDecl : node.getEnvironment().fields.values()) {
            if (fDecl.modifiers.contains(Modifier.STATIC)) {
                String fieldSig = SigHelper.getFieldSig(fDecl);
                this.exclude.add(fieldSig);
                putFieldInData(dataSection, fDecl, node, false);
            }
            
        }
        for (FieldDeclaration fDecl : node.getEnvironment().getEnclosing().fields.values()) {
            if (fDecl.modifiers.contains(Modifier.STATIC)) {
                putFieldInData(dataSection, fDecl, node, true);
            }
            // inherited fields
        }

        for (FieldDeclaration fDecl : node.getEnvironment().fields.values()) {
            fDecl.accept(this);

        }
        StringUtility.appendIndLn(instanceFieldInit[0], "ret");
        
        // methods
        for (Map.Entry<String, MethodDeclaration> entry : node.getEnvironment().methods.entrySet()) {
            staticMethodVTableHandler(entry, node, textSection);
        }
        
        for (Map.Entry<String, MethodDeclaration> entry : node.getEnvironment().getEnclosing().methods.entrySet()) {
            String methodSigInDec = SigHelper.getMethodSigWithImp(entry.getValue());
            this.extern.add(methodSigInDec);
            staticMethodVTableHandler(entry, node, textSection);
        }
        
        StringUtility.appendLine(vTableText, "dd " + SigHelper.getClassSigWithHierarchy(node), 2);
        this.extern.add(SigHelper.getClassSigWithHierarchy(node));
        
        if (!node.isInterface) {
            StringUtility.appendLine(vTableText, "dd " + SigHelper.getClassSigWithUgly(node), 2);
            this.extern.add(SigHelper.getClassSigWithUgly(node));
            for (Integer i = 0; i < SigOffsets.size(); i++) {
                StringUtility.appendLine(vTableText, "dd " + SigOffsets.get(i), 2);
                
            }
        }
        
        	
        for (BodyDeclaration bDecl : node.members) {
        	if (bDecl instanceof MethodDeclaration) {
        		MethodDeclaration mDecl = (MethodDeclaration) bDecl;
        	        String methodSigInDec = SigHelper.getMethodSigWithImp(mDecl);
        	        this.exclude.add(methodSigInDec);
	            if (SigHelper.getMethodSigWithImp(mDecl).equals(testSig)) {
	                this.extern.add("__debexit");
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
        StringUtility.appendLine(vTableText, "global " + SigHelper.getArrayClssSigWithVTable(node));
        StringUtility.appendIndLn(vTableText, SigHelper.getArrayClssSigWithVTable(node) + ":");   
        StringUtility.appendLine(vTableText, "dd " + SigHelper.getArrayClassSigWithHierarchy(node), 2);
        this.extern.add(SigHelper.getArrayClassSigWithHierarchy(node));
        
        dataSection.append(ExpressionCodeGenerator.stringLitData);
        textSection.append(getInstanceFieldInit() + "\n");
        textSection.append(start + "\n");
        textSection.append(vTableText + "\n");
        this.extern.add("__malloc");
        this.extern.add("__exception");
        node.attachCode(getExtern().toString() + textSection.toString() + staticFieldInit[1].toString() + dataSection.toString());
        staticFieldInit[1].setLength(0);
    }
    
    private StringBuilder getExtern() {
        StringBuilder sb = new StringBuilder();
        for (String s : this.extern) {
            if (this.exclude.contains(s)) {
                continue;
            }
            StringUtility.appendLine(sb, "extern " + s);
        }
        sb.append("\n");
        return sb;
    }

    private void generateStart(StringBuilder start, String testSig) {
        StringUtility.appendLine(start, "global _start");
        StringUtility.appendIndLn(start, "_start:");
        StringUtility.appendLine(start, "call static_init", 2);
        StringUtility.appendLine(start, "call " + testSig, 2);
        StringUtility.appendLine(start, "call __debexit", 2);
        this.extern.add("static_init");
    }

    private void staticMethodVTableHandler(Map.Entry<String, MethodDeclaration> entry, TypeDeclaration node, StringBuilder textSection) throws Exception {
        String mName = entry.getKey();
        MethodDeclaration mDecl = entry.getValue();
        String methodSig = SigHelper.getMethodSig(node, mDecl);
        String methodSigInDec = SigHelper.getMethodSigWithImp(mDecl);
        if (mDecl.modifiers.contains(Modifier.STATIC)) {
            StringUtility.appendLine(textSection, "global " + methodSig);
	    StringUtility.appendIndLn(textSection, methodSig + ":");
	    if (mDecl.modifiers.contains(Modifier.NATIVE)) {
		String nativeSig = SigHelper.getNativeSig(mDecl);
		extern.add(nativeSig);
		StringUtility.appendLine(textSection, "dd " + nativeSig, 2);
	    } else {
		StringUtility.appendLine(textSection, "dd " + methodSigInDec, 2);
	    }
        } else {
            if (!node.isInterface) {
                int offSet = node.getMethodOffSet(mName);
		SigOffsets.put(offSet, methodSigInDec);
	    }
        }
    }

    private void putFieldInData(StringBuilder sb, FieldDeclaration fDecl, TypeDeclaration typeDec, boolean fromEnclosing) throws Exception {
        String fieldSig = SigHelper.getFieldSig(typeDec, fDecl);
        String fieldSigWithImp = SigHelper.getFieldSigWithImp(fDecl);
        StringUtility.appendLine(sb, "global " + fieldSig + "\t; define global label for field");
        StringUtility.appendLine(sb, fieldSig + ":" + "\t; label start");
        StringUtility.appendLine(sb, "\t" + "dd " + fieldSigWithImp + "\t; points to the dec");
        if (!fromEnclosing ) {
            staticInitExtern.add(fieldSig);
            StringUtility.appendLine(staticFieldInit[0], "call static_init_"
                    + fieldSig, 2);
            StringUtility.appendLine(sb, "global " + fieldSigWithImp + "\t; define global label for field");
            StringUtility.appendLine(sb, fieldSigWithImp + ":" + "\t; label start");
            StringUtility.appendLine(sb, "\t" + "dd 0x0" + "\t; default value: 0 false null");
        } else {
            this.extern.add(fieldSigWithImp);
        }
    }

    public void visit(FieldDeclaration node) throws Exception {
        String fieldSig = SigHelper.getFieldSig(currentTypeDec, node);
        String fieldSigInDec = SigHelper.getFieldSigWithImp(node);
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
                this.exclude.add(fieldSigInDec);
                StringUtility.appendIndLn(staticFieldInit[1], "global static_init_" + fieldSig);
                StringUtility.appendIndLn(staticFieldInit[1], "static_init_" + fieldSig + ":");
                ExpressionCodeGenerator.generateFieldAddr(staticFieldInit[1], node);
                StringUtility.appendLine(staticFieldInit[1], initCode, 2);
                StringUtility.appendIndLn(staticFieldInit[1], "mov " + "[" + fieldSig + "]" + ", eax" + "\t; initiallize field");              
                StringUtility.appendLine(staticFieldInit[1], "ret", 2);
            } else {
                StringUtility.appendIndLn(instanceFieldInit[0], "call instance_init_" + fieldSig);
                StringUtility.appendIndLn(instanceFieldInit[1], "instance_init_" + fieldSig + ":");
		ExpressionCodeGenerator.generateFieldAddr(instanceFieldInit[1], node);
                StringUtility.appendLine(instanceFieldInit[1], "push eax \t;store field address", 2);
                StringUtility.appendLine(instanceFieldInit[1], initCode, 2);
                StringUtility.appendLine(instanceFieldInit[1], "mov edx, eax \t; put value of field to edx", 2);
                StringUtility.appendLine(instanceFieldInit[1], "pop eax \t; pop field address back to eax", 2);
                StringUtility.appendIndLn(instanceFieldInit[1], "mov [eax], edx" + "\t; initiallize field");
                StringUtility.appendIndLn(instanceFieldInit[1], "ret" + "\t; initiallize field ret");
                
            }
        }
    }

    public void visit(MethodDeclaration node) throws Exception {
    	StringBuilder sb = new StringBuilder();
    		
	StringUtility.appendLine(sb, "global " + SigHelper.getMethodSigWithImp(node));
    	StringUtility.appendLine(sb, SigHelper.getMethodSigWithImp(node) + ":");	// generate method label
    	
		StringUtility.appendIndLn(sb, "push ebp \t; save old frame pointer");
		StringUtility.appendIndLn(sb, "mov ebp, esp \t; move ebp to top of stack");
		StringUtility.appendIndLn(sb, "sub esp, " + (node.frameSize * 4) + "\t; space for local variables");
		
		// if constructor, call field initializer
		if (node.isConstructor) {
			TypeDeclaration tDecl = (TypeDeclaration) node.getParent();
			StringUtility.appendIndLn(sb, "push dword [ebp+8] \t; push object for initialiser"); 	// push object for initializer call
			StringUtility.appendIndLn(sb, "call " + SigHelper.instanceFieldInitSig(tDecl));
			StringUtility.appendIndLn(sb, "add esp, 4 \t; remove object for initializer");
		}
		
		
		if (node.body != null) {
			// TODO: check occasions where this would be null
			ExpressionCodeGenerator.currentMethod = node;	// set current method 
			node.body.accept(stmtGen);
			ExpressionCodeGenerator.currentMethod = null;	// remove current method
			sb.append(node.body.getCode());
		} 
		
		// clean up in case there is no return statement
		StringUtility.appendIndLn(sb, "mov eax, 0 \t; in the case of no return, make sure eax is null"); 
		StringUtility.appendIndLn(sb, "mov esp, ebp \t; delete frame");
		StringUtility.appendIndLn(sb, "pop ebp \t; restore to previous frame");
		StringUtility.appendIndLn(sb, "ret \t; end of method");
		
		node.attachCode(sb.toString());
    }
    
    protected static String getStaticFieldInit() {
        StringUtility.appendIndLn(staticFieldInit[0], "ret");
        String staticFieldInitString = staticFieldInit[0].toString();
        staticFieldInit[0].setLength(0);
        return getStaticFieldInitExtern() + staticFieldInitString;
    }
    
    private static String getStaticFieldInitExtern() {
        StringBuilder sb = new StringBuilder();
        for (String s : staticInitExtern) {
            StringUtility.appendLine(sb, "extern static_init_" + s);
        }
        sb.append("\n");
        StringUtility.appendLine(sb, "global static_init");
        StringUtility.appendIndLn(sb, "static_init:");
        return sb.toString();
    }

    private String getInstanceFieldInit() {
        String instanceFieldInitString = instanceFieldInit[0].toString() + instanceFieldInit[1].toString();
        instanceFieldInit[0].setLength(0);
        instanceFieldInit[1].setLength(0);
        return instanceFieldInitString;
    }

    public static void generate(List<AST> trees) throws Exception {
        staticFieldInit[0].setLength(0);
        staticFieldInit[1].setLength(0);
        instanceFieldInit[0].setLength(0);
        instanceFieldInit[1].setLength(0);
        staticInitExtern = new HashSet<String>();
        for (AST t : trees) {
            Visitor rv = new CodeGenerator();
            t.root.accept(rv);
        }
    }
}
