package code_generation;

import java.util.Set;

import utility.StringUtility;
import ast.ASTNode;
import ast.AssignmentExpression;
import ast.BooleanLiteral;
import ast.CharacterLiteral;
import ast.ClassInstanceCreationExpression;
import ast.Expression;
import ast.FieldDeclaration;
import ast.InfixExpression;
import ast.IntegerLiteral;
import ast.MethodDeclaration;
import ast.MethodInvocation;
import ast.Modifier;
import ast.NullLiteral;
import ast.PrefixExpression;
import ast.PrefixExpression.Operator;
import ast.QualifiedName;
import ast.SimpleName;
import ast.SimpleType;
import ast.StringLiteral;
import ast.TypeDeclaration;
import ast.VariableDeclaration;
import environment.NameHelper;
import environment.TraversalVisitor;
import exceptions.NameException;

public class ExpressionCodeGenerator extends TraversalVisitor {

    private static final String FALSE = "0x0";
    private static final String TRUE = "0xffffffff";
    private int stringLitCounter = 0;
    public static StringBuilder stringLitData = new StringBuilder();
    private Set<String> extern;
    public int infixCounter = 0;

    public ExpressionCodeGenerator(Set<String> extern) {
        this.extern = extern;
    }

    /*
     * Literals
     */
    // String is Object.
    public void visit(StringLiteral node) throws Exception {
        // can use counter because string literal is not global.
        stringLitCounter++;
        // TODO:integrate stringLitData into data section.
        StringUtility.appendLine(stringLitData, "STRING_" + stringLitCounter + ":" + "\t; define label for string literal");
        StringUtility.appendLine(stringLitData, "\t" + "dw " + '\'' + node.value + '\'');

        node.attachCode("\tmov eax, " + "STRING_" + stringLitCounter + "\n");
    }

    public void visit(NullLiteral node) throws Exception {
        node.attachCode("\tmov eax, " + FALSE + "\n");
    }

    public void visit(BooleanLiteral node) throws Exception {
        String booleanText;
        if (node.value == true) {
            booleanText = "\tmov eax, " + TRUE;
        } else {
            booleanText = "\tmov eax, " + FALSE;
        }
        node.attachCode(booleanText + "\n");
    }

    public void visit(CharacterLiteral node) throws Exception {
        String charText;

        // Assuming octal is valid.
        if (node.value.length() > 3) {
            charText = "\tmov eax, " + "0o" + node.value.substring(1);
        } else {
            charText = "\tmov eax, " + "'" + node.value + "'";
        }

        node.attachCode(charText + "\n");
    }

    public void visit(IntegerLiteral node) throws Exception {
        String intText;
        intText = "\tmov eax, " + node.value + "\n";
        node.attachCode(intText);
    }
    
    @Override
    public void visit(AssignmentExpression node) throws Exception {
        StringBuilder assignText = new StringBuilder();
        if (node.lhs != null) {
            node.lhs.accept(this);
            String lhsText = node.lhs.getCode();
            assignText.append(lhsText);
            StringUtility.appendIndLn(assignText, "push eax" + "\t; push lhs to stack");
        }
        if (node.expr != null) {
            node.expr.accept(this);
            String rhsText = node.expr.getCode();
            assignText.append(rhsText);
        } else {
            StringUtility.appendIndLn(assignText, "mov eax, " + FALSE + "\t; rhs is null");
        }
        StringUtility.appendIndLn(assignText, "pop ebx" + "\t; pop lhs to ebx");
        StringUtility.appendIndLn(assignText, "mov [ebx], eax" + "\t; assignment");
        node.attachCode(assignText.toString());
    }

    @Override
    public void visit(PrefixExpression node) throws Exception {
        StringBuilder prefixText = new StringBuilder();
        if (node.expr != null) {
            if (node.op.equals(Operator.MINUS)) {
                if (node.expr instanceof IntegerLiteral) {
                    ((IntegerLiteral)node.expr).value = "-" + ((IntegerLiteral)node.expr).value;
                } else {
                    StringUtility.appendIndLn(prefixText, "mov eax, - eax" + "\t; negation operation");
                }
                
            } else if (node.op.equals(Operator.NOT)) {
                StringUtility.appendIndLn(prefixText, "neg eax" + "\t; logical negation operation");
            }
            node.expr.accept(this);
            String exprCode = node.expr.getCode();
            prefixText.insert(0, exprCode);
        }
        node.attachCode(prefixText.toString());
    }


    @Override
    public void visit(InfixExpression node) throws Exception {
        StringBuilder infixText = new StringBuilder();
        if (node.lhs != null && node.rhs != null) {
            int n = infixCounter;
            infixCounter++;
            node.lhs.accept(this);
            infixText.append(node.lhs.getCode());
            if (node.op == InfixExpression.Operator.LOR || node.op == InfixExpression.Operator.AND) {
                StringUtility.appendLine(infixText, "cmp eax," + (node.op == InfixExpression.Operator.LOR ? FALSE : TRUE));
                StringUtility.appendLine(infixText, "jne INFIX_" + n);
            }
            StringUtility.appendLine(infixText, "push eax\t; push lhs to stack");
            node.rhs.accept(this);
            infixText.append(node.rhs.getCode());
            StringUtility.appendLine(infixText, "mov ebx, eax\t; move rhs to ebx");
            StringUtility.appendLine(infixText, "pop eax\t; pop lhs back to eax");
            switch (node.op) {
              case LOR: // fall through
              case AND:
                StringUtility.appendLine(infixText, "INFIX_" + n + ":");
                break;
              case BITOR:
                StringUtility.appendLine(infixText, "or eax, ebx");
                break;
              case BITAND:
                StringUtility.appendLine(infixText, "and eax, ebx");
                break;
              case NEQ:
                StringUtility.appendLine(infixText, "cmp eax, ebx");
                StringUtility.appendLine(infixText, "mov eax, " + TRUE);
                StringUtility.appendLine(infixText, "jne INFIX_" + n);
                StringUtility.appendLine(infixText, "mov eax, " + FALSE);
                StringUtility.appendLine(infixText, "INFIX_" + n + ":");
                break;
              case EQUAL:
                StringUtility.appendLine(infixText, "cmp eax, ebx");
                StringUtility.appendLine(infixText, "mov eax, " + FALSE);
                StringUtility.appendLine(infixText, "jne INFIX_" + n);
                StringUtility.appendLine(infixText, "mov eax, " + TRUE);
                StringUtility.appendLine(infixText, "INFIX_" + n + ":");
                break;
              case LANGLE:
                StringUtility.appendLine(infixText, "cmp eax, ebx");
                StringUtility.appendLine(infixText, "mov eax, " + FALSE);
                StringUtility.appendLine(infixText, "jge INFIX_" + n);
                StringUtility.appendLine(infixText, "mov eax, " + TRUE);
                StringUtility.appendLine(infixText, "INFIX_" + n + ":");
                break;
              case RANGLE:
                StringUtility.appendLine(infixText, "cmp eax, ebx");
                StringUtility.appendLine(infixText, "mov eax, " + FALSE);
                StringUtility.appendLine(infixText, "jle INFIX_" + n);
                StringUtility.appendLine(infixText, "mov eax, " + TRUE);
                StringUtility.appendLine(infixText, "INFIX_" + n + ":");
                break;
              case LEQ:
                StringUtility.appendLine(infixText, "cmp eax, ebx");
                StringUtility.appendLine(infixText, "mov eax, " + TRUE);
                StringUtility.appendLine(infixText, "jle INFIX_" + n);
                StringUtility.appendLine(infixText, "mov eax, " + FALSE);
                StringUtility.appendLine(infixText, "INFIX_" + n + ":");
                break;
              case GEQ:
                StringUtility.appendLine(infixText, "cmp eax, ebx");
                StringUtility.appendLine(infixText, "mov eax, " + TRUE);
                StringUtility.appendLine(infixText, "jge INFIX_" + n);
                StringUtility.appendLine(infixText, "mov eax, " + FALSE);
                StringUtility.appendLine(infixText, "INFIX_" + n + ":");
                break;
              case PLUS:
                if (node.lhs.getType() instanceof SimpleType && node.lhs.getType().getDeclaration().getFullName().equals("java.lang.String")) {
                    //
                } else {
                    StringUtility.appendLine(infixText, "add eax, ebx");
                }
                break;
              case MINUS:
                StringUtility.appendLine(infixText, "sub eax, ebx");
                break;
              case STAR:
                StringUtility.appendLine(infixText, "imul eax, ebx");
                break;
              case SLASH:
                StringUtility.appendLine(infixText, "mov edx, 0");
                StringUtility.appendLine(infixText, "idiv ebx");
                break;
              case MOD:
                StringUtility.appendLine(infixText, "mov edx, 0");
                StringUtility.appendLine(infixText, "idiv ebx");
                StringUtility.appendLine(infixText, "mov eax, edx");
                break;
            }
        }
        node.attachCode(infixText.toString());
    }


    /*
     * OO features
     */
    
    @Override
    public void visit(ClassInstanceCreationExpression node) throws Exception {
    	StringBuilder sb = new StringBuilder();    	
    	// generate code for arguments
    	int numArgs = 0;
    	if (node.arglist != null) {
	    	for (numArgs =0; numArgs < node.arglist.size() ; numArgs++) {
	    		Expression expr = node.arglist.get(numArgs);
	    		expr.accept(this);
	    		StringUtility.appendLine(sb, expr.getCode());
	    		StringUtility.appendIndLn(sb, "push eax \t; push argument " + numArgs);
	    	}    	
    	}
    	
    	// malloc
    	TypeDeclaration tDecl = node.type.getDeclaration();
    	int objSize = tDecl.getFieldOffSetList().size() + 2;
    	StringUtility.appendIndLn(sb, "mov eax, 4*" + objSize + "\t; size of object");
    	extern.add("__malloc");
    	StringUtility.appendIndLn(sb, "call __malloc");
    	StringUtility.appendIndLn(sb, "push eax \t; push object address");
    	
    	// pointer to VTable
    	StringUtility.appendIndLn(sb, "mov [eax], " + SigHelper.getClssSigWithVTable(tDecl));

    	// implicit super call
    	if (tDecl.superClass != null) {
    		MethodDeclaration superConstructor = getDefaultConstructor(tDecl.superClass.getDeclaration());
    		
    		String superConstructorLabel = SigHelper.getMethodSigWithImp(superConstructor);
    		extern.add(superConstructorLabel.trim());
        	StringUtility.appendIndLn(sb, "call " + superConstructorLabel);
    	}
    	
    	// call actual constructor
    	String constructorLabel = SigHelper.getMethodSigWithImp(node.getConstructor());
    	extern.add(constructorLabel.trim());
    	StringUtility.appendIndLn(sb, "call " + constructorLabel);
    	
    	// clean up
    	StringUtility.appendIndLn(sb, "pop eax \t; pop object address back in eax");
    	StringUtility.appendIndLn(sb, "add esp, 4 * " + numArgs);
    	node.attachCode(sb.toString());
    }
    
    private MethodDeclaration getDefaultConstructor(TypeDeclaration superClass) throws Exception {
    	MethodDeclaration result = null;
    	for (MethodDeclaration constructor : superClass.getEnvironment().constructors.values()) {
    		if (constructor.parameters.size() == 0)
    			result = constructor;
    	}
		if (result == null)
			throw new Exception("No default constructor is defined for super class: " + superClass.id);
    	
    	return result;
	}

	/**
     * Deals with method names
     */
    @Override
    public void visit(MethodInvocation node) throws Exception {
    	StringBuilder sb = new StringBuilder();

    	//TODO: caller save registers
    	
    	// generate code for arguments
    	int numArgs = 0;
    	if (node.arglist != null) {
	    	for (numArgs =0; numArgs < node.arglist.size() ; numArgs++) {
	    		Expression expr = node.arglist.get(numArgs);
	    		expr.accept(this);
	    		StringUtility.appendLine(sb, expr.getCode());
	    		StringUtility.appendIndLn(sb, "push eax \t; push argument " + numArgs);
	    	}
    	}
    	
    	if (node.id != null) {
    		// Primary.ID(...)
    		node.expr.accept(this);	// generate code for Primary expression
    		StringUtility.appendLine(sb, node.getCode());	// by this point eax should contain address to object return by Primary expression
    		StringUtility.appendIndLn(sb, "push eax \t; push object for method invocation");
    		MethodDeclaration mDecl = (MethodDeclaration) node.id.getDeclaration();	// the actual method being called
    		// call method
    		generateMethodCall(sb, mDecl);
    		
    	} else {
    		// Name(...)
    		if (node.expr instanceof SimpleName) {	// SimpleName(...), implicit this
    			SimpleName sn = (SimpleName) node.expr;
    			StringUtility.appendIndLn(sb, "mov dword eax, [ebp + 8] \t; move object address to eax"); // this only happens in the method of the same class
    			StringUtility.appendIndLn(sb, "push eax \t; push object address");
    			MethodDeclaration mDecl = (MethodDeclaration) sn.getDeclaration();
    			generateMethodCall(sb, mDecl);
    		} else {	// QualifiedName(...)
    			QualifiedName qn = (QualifiedName) node.expr;
    			MethodDeclaration mDecl = (MethodDeclaration) qn.getDeclaration();
    			if (qn.getQualifier().getDeclaration() instanceof TypeDeclaration) {	// static methods
    				StringUtility.appendIndLn(sb, "push 0 \t; place holder because there is no this object for static method");
    				generateMethodCall(sb, mDecl);
    			} else {	// instance method
    				qn.accept(this); 	// generate code from name (accessing instance field, or local variable
    				StringUtility.appendLine(sb, qn.getCode());
    	    		StringUtility.appendIndLn(sb, "push eax \t; push object for method invocation");
    	    		// call method
    	    		generateMethodCall(sb, mDecl);
    			}
    		}
    	}
    	
		// clean up
		StringUtility.appendIndLn(sb, "add esp, " + (numArgs+1) + "*4" + "\t; caller cleanup arguments.");	
		node.attachCode(sb.toString());
    	
    }
    
    /**
     * generating actual method call by offset, assumes that the object is in eax
     * @param mDecl
     * @return
     * @throws NameException
     * @throws Exception
     */
    private void generateMethodCall(StringBuilder sb, MethodDeclaration mDecl) throws NameException, Exception {
    	
    	TypeDeclaration tDecl = (TypeDeclaration) mDecl.getParent();
    	if (mDecl.modifiers.contains(Modifier.STATIC)) {
    		String label = SigHelper.getMethodSigWithImp(mDecl);
    		extern.add(label.trim());
    		StringUtility.appendIndLn(sb, "call " + label);
    	} else {	// instance method
			// generate method call
			if (tDecl.isInterface) {	// interface method
				int offset = OffSet.getInterfaceMethodOffset(NameHelper.mangle(mDecl));
				StringUtility.appendIndLn(sb, "mov eax, [eax] \t; point to VTable");
				StringUtility.appendIndLn(sb, "mov eax, [eax] \t; point to Ugly");
				StringUtility.appendIndLn(sb, "call [eax + " + offset + "*4] \t; call interface method.");
				//TODO: check if the level of indirection is proper 
			} else {
				int offset = tDecl.getMethodOffSet(NameHelper.mangle(mDecl));
				StringUtility.appendIndLn(sb, "mov eax, [eax] \t; point to VTable");
				StringUtility.appendIndLn(sb, "call [eax + " + (offset + 2) + "*4] \t; call class method."); //skip VTable and Inheritance Table
			}
    	}
    }
    
    public void visit(SimpleName node) throws Exception {
    	StringBuilder sb = new StringBuilder();
    	ASTNode decl = node.getDeclaration();
    	if (decl instanceof FieldDeclaration) {	// field
    		FieldDeclaration fDecl = (FieldDeclaration) decl;
    		TypeDeclaration parent = (TypeDeclaration) fDecl.getParent();
    		int offset = parent.getFieldOffSet(fDecl.id);
    		StringUtility.appendIndLn(sb, "mov eax, [ebp + 8] \t; put object address in eax");
    		StringUtility.appendIndLn(sb, "mov eax, [eax] \t; enter object");
    		StringUtility.appendIndLn(sb, "mov eax, [eax + " + offset + "*4] \t; access object");
    	} else if (decl instanceof VariableDeclaration) {	// variable
    		
    	}
    }
    
}
