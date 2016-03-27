package code_generation;

import utility.StringUtility;
import ast.*;
import environment.NameHelper;
import environment.TraversalVisitor;
import exceptions.NameException;

public class ExpressionCodeGenerator extends TraversalVisitor {

    private static final String FALSE = "0x0";
    private static final String TRUE = "0xffffffff";
    private int stringLitCounter = 0;
    
    /*
     * (non-Javadoc)
     * Literals
     */
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
            charText = "mov eax, " + "'" + node.value + "'";
        }

        node.attachCode(charText);
    }

    public void visit(IntegerLiteral node) throws Exception {
        String intText;
        intText = "mov eax, " + node.value;
        node.attachCode(intText);
    }
    
    /*
     * 
     */
    @Override
    public void visit(MethodInvocation node) throws Exception {
    	StringBuilder sb = new StringBuilder();

    	// generate code for arguments
    	int numArgs;
    	for (numArgs =0; numArgs < node.arglist.size() ; numArgs++) {
    		Expression expr = node.arglist.get(numArgs);
    		expr.accept(this);
    		StringUtility.appendIndLn(sb, expr.getCode());
    		StringUtility.appendIndLn(sb, "push eax \t; push argument " + numArgs);
    	}
    	
    	if (node.id != null) {
    		// Primary.ID(...)
    		node.expr.accept(this);	// generate code for Primary expression
    		StringUtility.appendIndLn(sb, node.getCode());	// by this point eax should contain address to object return by Primary expression
    		StringUtility.appendIndLn(sb, "push eax \t; push object for method invocation");
    		MethodDeclaration mDecl = (MethodDeclaration) node.id.getDeclaration();	// the actual method being called
    		
    		// call method
    		StringUtility.appendIndLn(sb, generateMethodCall(mDecl));
    		
    	} else {
    		// Name(...)
    		if (node.expr instanceof SimpleName) {	// SimpleName(...), implicit this
    			SimpleName sn = (SimpleName) node.expr;
    			StringUtility.appendIndLn(sb, "mov eax, [ebp + 8] \t; move object address to eax"); // this only happens in the method of the same class
    			StringUtility.appendIndLn(sb, "push eax \t; push object address");
    			MethodDeclaration mDecl = (MethodDeclaration) sn.getDeclaration();
    			StringUtility.appendIndLn(sb, generateMethodCall(mDecl));
    			
    		} else {	// QualifiedName(...)
    			
    			
    			
    		}
    		
    	}
    	
		// clean up
		StringUtility.appendIndLn(sb, "add esp " + (numArgs+1) + "*4" + "\t; caller cleanup arguments.");	
		node.attachCode(sb.toString());
    	
    }
    
    /**
     * generating actual method call by offset, assumes that the object is in eax
     * @param mDecl
     * @return
     * @throws NameException
     * @throws Exception
     */
    private String generateMethodCall(MethodDeclaration mDecl) throws NameException, Exception {
    	String call;
    	TypeDeclaration tDecl = (TypeDeclaration) mDecl.getParent();
    	if (mDecl.modifiers.contains(Modifier.STATIC)) {
    		call = "call " + SigHelper.getMethodSigWithImp(mDecl);	// call static methods
    		
    	} else {	// instance method
			// generate method call
			if (tDecl.isInterface) {	// interface method
				int offset = OffSet.getInterfaceMethodOffset(NameHelper.mangle(mDecl));
				call = "call [[eax] + " + offset + "*4] \t; call interface method.";		//TODO: check if the level of indirection is proper 
			} else {
				int offset = tDecl.getMethodOffSet(NameHelper.mangle(mDecl));
				call = "call [[eax] + " + offset + "*4] \t; call class method.";
			}
    	}
		return call;
    }
    
}
