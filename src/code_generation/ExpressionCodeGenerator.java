package code_generation;

import java.util.List;
import java.util.Set;

import utility.StringUtility;
import ast.ASTNode;
import ast.ArrayAccess;
import ast.ArrayCreationExpression;
import ast.ArrayType;
import ast.AssignmentExpression;
import ast.BooleanLiteral;
import ast.CastExpression;
import ast.CharacterLiteral;
import ast.ClassInstanceCreationExpression;
import ast.Expression;
import ast.FieldAccess;
import ast.FieldDeclaration;
import ast.InfixExpression;
import ast.InstanceofExpression;
import ast.IntegerLiteral;
import ast.MethodDeclaration;
import ast.MethodInvocation;
import ast.Modifier;
import ast.Name;
import ast.NullLiteral;
import ast.PrefixExpression;
import ast.PrefixExpression.Operator;
import ast.PrimitiveType;
import ast.QualifiedName;
import ast.SimpleName;
import ast.SimpleType;
import ast.StringLiteral;
import ast.ThisExpression;
import ast.Type;
import ast.TypeDeclaration;
import ast.VariableDeclaration;
import ast.VariableDeclarationExpression;
import environment.NameHelper;
import environment.TraversalVisitor;
import exceptions.NameException;

public class ExpressionCodeGenerator extends TraversalVisitor {

    private static final String FALSE = "0x0";
    private static final String TRUE = "1";
    private int litCounter = 0;
    public static StringBuilder stringLitData = new StringBuilder();
    private Set<String> extern;
    public int infixCounter = 0;
    private int instanceOfCounter = 0;
    StringBuilder dataSection;
    public static MethodDeclaration currentMethod;

    private boolean isLV = false;

    public ExpressionCodeGenerator(Set<String> extern, StringBuilder dataSection) {
        this.extern = extern;
        this.dataSection = dataSection;
    }

    /*
     * Literals
     */
    // String is Object.
    public void visit(StringLiteral node) throws Exception {
        // can use counter because string literal is not global.
        litCounter++;
        // TODO:integrate stringLitData into data section.
        StringUtility.appendLine(dataSection, "STRING_" + litCounter + ":" + "\t; define label for string literal");
        StringUtility.appendLine(dataSection, "\t" + "dd " + '\'' + node.value + '\'');

        node.attachCode("\tmov dword eax, " + "[STRING_" + litCounter + "]" + "\n");
    }

    public void visit(NullLiteral node) throws Exception {
        StringBuilder nullText = new StringBuilder();
        litCounter++;
        StringUtility.appendLine(dataSection, "NULL_" + litCounter + ":" + "\t; define label for null literal");
        StringUtility.appendLine(dataSection, "\t" + "dd " + FALSE);
        StringUtility.appendLine(nullText, "\t" + "mov dword eax, " + "[NULL_" + litCounter + "]");
        node.attachCode(nullText.toString());
        
    }

    public void visit(BooleanLiteral node) throws Exception {
        StringBuilder booleanText = new StringBuilder();
        litCounter++;
        StringUtility.appendLine(dataSection, "BOOLEAN_" + litCounter + ":" + "\t; define label for boolean literal");
        if (node.value == true) {
            StringUtility.appendLine(dataSection, "\t" + "dd " + TRUE);
        } else {
            StringUtility.appendLine(dataSection, "\t" + "dd " + FALSE);
        }
        StringUtility.appendLine(booleanText, "\t" + "mov dword eax, " + "[BOOLEAN_" + litCounter + "]");
        node.attachCode(booleanText + "\n");
    }

    public void visit(CharacterLiteral node) throws Exception {
        StringBuilder charText = new StringBuilder();
        litCounter++;
        StringUtility.appendLine(dataSection, "CHAR_" + litCounter + ":" + "\t; define label for char literal");

        // Assuming octal is valid.
        if (node.value.length() > 3) {
            StringUtility.appendLine(dataSection, "\t" + "dd " + node.value.substring(1));
        } else {
            StringUtility.appendLine(dataSection, "\t" + "dd " + "'" + node.value + "'");
        }
        StringUtility.appendLine(charText, "\t" + "mov dword eax, " + "[CHAR_" + litCounter + "]");
        node.attachCode(charText + "\n");
    }

    public void visit(IntegerLiteral node) throws Exception {
        StringBuilder intText = new StringBuilder();
        litCounter++;
        StringUtility.appendLine(dataSection, "INT_" + litCounter + ":" + "\t; define label for int literal");
        StringUtility.appendLine(dataSection, "\t" + "dd " + node.value);
        StringUtility.appendLine(intText, "\t" + "mov dword eax, " + "[INT_" + litCounter + "]");
        node.attachCode(intText.toString());
    }
    
    @Override
    public void visit(AssignmentExpression node) throws Exception {
        StringBuilder assignText = new StringBuilder();
        if (node.lhs != null) {
	    isLV = true;
            node.lhs.accept(this);
            isLV = false;
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
        //StringUtility.appendIndLn(assignText, "mov eax, [eax]" + "\t; assignment");
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
                    StringUtility.appendIndLn(prefixText, "neg eax" + "\t; negation operation");
                }
                
            } else if (node.op.equals(Operator.NOT)) {
                StringUtility.appendIndLn(prefixText, "not eax" + "\t; logical negation operation");
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
                Type lhsType = node.lhs.getType();
                Type rhsType = node.rhs.getType();
                if (lhsType instanceof SimpleType &&
                    lhsType.getDeclaration().getFullName().equals("java.lang.String")) {
                    StringUtility.appendLine(infixText, "push eax");
                    if (rhsType instanceof PrimitiveType) {
                        StringUtility.appendLine(infixText, "push ebx");
                        extern.add("__malloc");
                        StringUtility.appendLine(infixText, "call __malloc");
                        StringUtility.appendLine(infixText, "push eax \t; push object address");
                        switch (((PrimitiveType) rhsType).value) {
                          case BOOLEAN:
                            extern.add("java.lang.Boolean#~init~$B$implementation");
                            StringUtility.appendLine(infixText, "call java.lang.Boolean#~init~$B$implementation");
                            StringUtility.appendLine(infixText, "pop ebx\t; clean up");
                            StringUtility.appendLine(infixText, "pop ebx\t; clean up");
                            StringUtility.appendLine(infixText, "push eax");
                            extern.add("java.lang.Boolean#toString$$implementation");
                            StringUtility.appendLine(infixText, "call java.lang.Boolean#toString$$implementation");
                            break;
                          case CHAR:
                            extern.add("java.lang.Character#~init~$B$implementation");
                            StringUtility.appendLine(infixText, "call java.lang.Character#~init~$C$implementation");
                            StringUtility.appendLine(infixText, "pop ebx\t; clean up");
                            StringUtility.appendLine(infixText, "pop ebx\t; clean up");
                            StringUtility.appendLine(infixText, "push eax");
                            extern.add("java.lang.Character#toString$$implementation");
                            StringUtility.appendLine(infixText, "call java.lang.Character#toString$$implementation");
                            break;
                          default:
                            extern.add("java.lang.Integer#~init~$I$implementation");
                            StringUtility.appendLine(infixText, "call java.lang.Inteter#~init~$I$implementation");
                            StringUtility.appendLine(infixText, "pop ebx\t; clean up");
                            StringUtility.appendLine(infixText, "pop ebx\t; clean up");
                            StringUtility.appendLine(infixText, "push eax");
                            extern.add("java.lang.Integer#toString$$implementation");
                            StringUtility.appendLine(infixText, "call java.lang.Integer#toString$$implementation");
                        }
                        StringUtility.appendLine(infixText, "pop ebx\t; clean up");
                        StringUtility.appendLine(infixText, "push eax");
                    } else if (rhsType instanceof SimpleType &&
                               rhsType.getDeclaration().getFullName().equals("java.lang.String")) {
                        StringUtility.appendLine(infixText, "push ebx");
                    }
                    extern.add("java.lang.String#concat$java.lang.String$implementation");
                    StringUtility.appendLine(infixText, "call java.lang.String#concat$java.lang.String$implementation");
                    StringUtility.appendLine(infixText, "pop ebx\t; clean up");
                    StringUtility.appendLine(infixText, "pop ebx\t; clean up");
                } else if (rhsType instanceof SimpleType &&
                           rhsType.getDeclaration().getFullName().equals("java.lang.String")) {
                    StringUtility.appendLine(infixText, "push ebx");
                    if (rhsType instanceof PrimitiveType) {
                        StringUtility.appendLine(infixText, "push eax");
                        extern.add("__malloc");
                        StringUtility.appendLine(infixText, "call __malloc");
                        StringUtility.appendLine(infixText, "push eax \t; push object address");
                        switch (((PrimitiveType) rhsType).value) {
                          case BOOLEAN:
                            extern.add("java.lang.Boolean#~init~$B$implementation");
                            StringUtility.appendLine(infixText, "call java.lang.Boolean#~init~$B$implementation");
                            StringUtility.appendLine(infixText, "pop ebx\t; clean up");
                            StringUtility.appendLine(infixText, "pop ebx\t; clean up");
                            StringUtility.appendLine(infixText, "push eax");
                            extern.add("java.lang.Boolean#toString$$implementation");
                            StringUtility.appendLine(infixText, "call java.lang.Boolean#toString$$implementation");
                            break;
                          case CHAR:
                            extern.add("java.lang.Character#~init~$B$implementation");
                            StringUtility.appendLine(infixText, "call java.lang.Character#~init~$C$implementation");
                            StringUtility.appendLine(infixText, "pop ebx\t; clean up");
                            StringUtility.appendLine(infixText, "pop ebx\t; clean up");
                            StringUtility.appendLine(infixText, "push eax");
                            extern.add("java.lang.Character#toString$$implementation");
                            StringUtility.appendLine(infixText, "call java.lang.Character#toString$$implementation");
                            break;
                          default:
                            extern.add("java.lang.Integer#~init~$I$implementation");
                            StringUtility.appendLine(infixText, "call java.lang.Inteter#~init~$I$implementation");
                            StringUtility.appendLine(infixText, "pop ebx\t; clean up");
                            StringUtility.appendLine(infixText, "pop ebx\t; clean up");
                            StringUtility.appendLine(infixText, "push eax");
                            extern.add("java.lang.Integer#toString$$implementation");
                            StringUtility.appendLine(infixText, "call java.lang.Integer#toString$$implementation");
                        }
                        StringUtility.appendLine(infixText, "pop ebx\t; clean up");
                    }
                    StringUtility.appendLine(infixText, "pop ebx");
                    StringUtility.appendLine(infixText, "push eax");
                    StringUtility.appendLine(infixText, "push ebx");
                    extern.add("java.lang.String#concat$java.lang.String$implementation");
                    StringUtility.appendLine(infixText, "call java.lang.String#concat$java.lang.String$implementation");
                    StringUtility.appendLine(infixText, "pop ebx\t; clean up");
                    StringUtility.appendLine(infixText, "pop ebx\t; clean up");
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

    public void visit(InstanceofExpression node) throws Exception {
        
        instanceOfCounter++;
        StringBuilder instanceofText = new StringBuilder();
        if (node.expr != null) {
            node.expr.accept(this);
        }
        String exprCode = node.expr.getCode();
        instanceofText.append(exprCode);
        //[[[eax]] + offset * 4]
        if (node.type != null) {
            node.type.accept(this);
            int offset = 0;
            if (node.type instanceof SimpleType) {
                SimpleType sType = (SimpleType)node.type;
                offset = HierarchyTableBuilder.getTypeOffSet(sType.getDeclaration().getFullName());
            } else if (node.type instanceof ArrayType) {
                ArrayType aType = (ArrayType)node.type;
                offset = HierarchyTableBuilder.getTypeOffSet(aType.toString());
            }
            int frame = offset * 4;
            StringUtility.appendLine(instanceofText, "mov eax, [eax] \t ;get first frame of object, the pointer of VTable", 2);
            StringUtility.appendLine(instanceofText, "mov eax, [eax] \t ;get first frame of VTable, the point of subclass table", 2);
            StringUtility.appendLine(instanceofText, "add eax, " + frame + "\t ;get the pointer of type in subclass table", 2);
            StringUtility.appendLine(instanceofText, "push eax \t ;put eax in stack", 2);
            StringUtility.appendLine(instanceofText, "mov eax, " + FALSE + " \t ;set eax to false", 2);
            StringUtility.appendLine(instanceofText, "pop ebx", 2);
            StringUtility.appendLine(instanceofText, "cmp dword [ebx], " + TRUE, 2);
            StringUtility.appendLine(instanceofText, "jne _INSTANCEOFEND_" + instanceOfCounter, 2);
            StringUtility.appendLine(instanceofText, "mov eax, " + TRUE, 2);
            StringUtility.appendLine(instanceofText, "_INSTANCEOFEND_" + instanceOfCounter + ":", 2);
        }
        node.attachCode(instanceofText.toString());
    }

    public void visit(CastExpression node) throws Exception {
        StringBuilder castText = new StringBuilder();
        Type castToType = null;
        if (node.type != null) {
            node.type.accept(this);
            castToType = node.type;
        }
        if (node.expr != null) {
            node.expr.accept(this);
            castToType = node.expr.getType();
        }
        if (node.unary != null) {
            node.unary.accept(this);
        }
        String unaryCode = node.unary.getCode();
        castText.append(unaryCode);

        int offset = 0;
        boolean isPrimitive = false;
        if (castToType instanceof SimpleType) {
            SimpleType sType = (SimpleType)castToType;
            offset = HierarchyTableBuilder.getTypeOffSet(sType.getDeclaration().getFullName());
        } else if (castToType instanceof ArrayType) {
            ArrayType aType = (ArrayType)castToType;
            offset = HierarchyTableBuilder.getTypeOffSet(aType.toString());
        } else if (castToType instanceof PrimitiveType) {
            isPrimitive = true;
        }
        if (!isPrimitive) {
            int frame = offset * 4;
            StringUtility.appendLine(castText, "mov ebx, eax \t ;copy eax", 2);
            StringUtility.appendLine(castText, "mov eax, [eax] \t ;get first frame of object, the pointer of VTable", 2);
            StringUtility.appendLine(castText, "mov eax, [eax] \t ;get first frame of VTable, the point of subclass table", 2);
            StringUtility.appendLine(castText, "add eax, " + frame + "\t ;get the pointer of type in subclass table", 2);                
            StringUtility.appendLine(castText, "cmp dword [eax], " + TRUE, 2);
            StringUtility.appendLine(castText, "jne __exception", 2);
            StringUtility.appendLine(castText, "mov eax, ebx \t ;restore eax", 2);
        }
        node.attachCode(castText.toString());
    }

    // TODO:
    public void visit(VariableDeclaration node) throws Exception {
	StringBuilder varDecText = new StringBuilder();
	genVarAddress(varDecText, node);
	// now eax should contain address of variable
	StringUtility.appendIndLn(varDecText, "push eax" + "\t; push variable address to stack");
	if (node.initializer != null) {
            node.initializer.accept(this);
            String initText = node.initializer.getCode();
            varDecText.append(initText);
        } else {
            StringUtility.appendIndLn(varDecText, "mov eax, " + FALSE + "\t; init is null");
        }
        StringUtility.appendIndLn(varDecText, "pop ebx" + "\t; pop variable address to ebx");
        //StringUtility.appendIndLn(varDecText, "mov eax, [eax]" + "\t; assignment");
        StringUtility.appendIndLn(varDecText, "mov [ebx], eax" + "\t; assignment");
        node.attachCode(varDecText.toString());
    }

    public void visit(VariableDeclarationExpression node) throws Exception {
        StringBuilder varDecExprText = new StringBuilder();
        node.variableDeclaration.accept(this);
        String varDecCode = node.variableDeclaration.getCode();
        varDecExprText.append(varDecCode);
        node.attachCode(varDecExprText.toString());
    }

    private void genVarAddress(StringBuilder sb, VariableDeclaration vDecl) throws Exception {
	int offset = currentMethod.getVarOffSet(vDecl);
	if (offset < 0) {	// formals starts from -1
	    offset = (-(offset - 1)) * 4;	// real offset 
	    StringUtility.appendIndLn(sb, "mov dword eax, ebp ");
	    StringUtility.appendIndLn(sb, "add eax, " + offset + " \t; eax contains formal address");
	} else {	// locals starts from 0
	    offset = (offset + 1) * 4;
	    StringUtility.appendIndLn(sb, "mov dword eax, ebp ");
	    StringUtility.appendIndLn(sb, "sub eax, " + offset + "\t; eax contains local address");	// eax now points to object
	}
    }

    /*
     * OO features
     */
    
    @Override
    public void visit(ClassInstanceCreationExpression node) throws Exception {
    	StringBuilder sb = new StringBuilder();    	
    	StringUtility.appendIndLn(sb, "; class instance creation: " + node.type);
	// generate code for arguments
    	int numArgs = 0;
    	numArgs = generateArgs(sb, node.arglist);
    	
    	// malloc
    	TypeDeclaration tDecl = node.type.getDeclaration();
    	int objSize = tDecl.getFieldOffSetList().size() + 1;	//	leave space for counter
    	StringUtility.appendIndLn(sb, "mov eax, 4*" + objSize + "\t; size of object");
    	extern.add("__malloc");
    	StringUtility.appendIndLn(sb, "call __malloc");
    	
    	StringUtility.appendIndLn(sb, "push eax \t; push object address");
    	
    	// pointer to VTable
    	String vt = SigHelper.getClssSigWithVTable(tDecl);
    	extern.add(vt);
    	StringUtility.appendIndLn(sb, "push eax");	// save object reference to exit vtable
    	StringUtility.appendIndLn(sb, "mov eax, [eax] \t; enter object");	
    	StringUtility.appendIndLn(sb, "mov dword eax, " + vt);
    	StringUtility.appendIndLn(sb, "pop eax	\n");
    	
    	// implicit super call
    	if (tDecl.superClass != null) {
    		MethodDeclaration superConstructor = getDefaultConstructor(tDecl.superClass.getDeclaration());
    		generateConstructorCall(sb, superConstructor);
    	}

    	// call actual constructor
    	generateConstructorCall(sb, node.getConstructor());
    	
    	// clean up
    	StringUtility.appendIndLn(sb, "pop eax \t; pop object address back in eax");
    	StringUtility.appendIndLn(sb, "add esp, 4 * " + numArgs);
    	node.attachCode(sb.toString());
    }
    
    private void generateConstructorCall(StringBuilder sb, MethodDeclaration constructorDecl) {
    	StringUtility.appendIndLn(sb, "push eax");	// save object reference
		String constructorLabel = SigHelper.getMethodSigWithImp(constructorDecl);
		extern.add(constructorLabel.trim());
    	StringUtility.appendIndLn(sb, "call " + constructorLabel);
    	StringUtility.appendIndLn(sb, "pop eax	\n");
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

    public void visit(ThisExpression node) throws Exception {
	node.attachCode("\tmov eax, [ebp+8] \n"); // simply move object from ebp to 8
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
    	numArgs = generateArgs(sb, node.arglist);
    	
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
    			Name qualifier = qn.getQualifier();
			if (qualifier.getDeclaration() instanceof TypeDeclaration) {	// static methods
    				StringUtility.appendIndLn(sb, "push 0 \t; place holder because there is no this object for static method");
    				generateMethodCall(sb, mDecl);
    			} else if (qualifier.getDeclaration() instanceof FieldDeclaration || qualifier.getDeclaration() instanceof VariableDeclaration) {	// instance method
    				qn.getQualifier().accept(this); 	// generate code from name (accessing instance field, or local variable
    				StringUtility.appendLine(sb, qn.getCode());
				StringUtility.appendIndLn(sb, "push eax \t; push object for method invocation");
				// call method
				generateMethodCall(sb, mDecl);
    			} else {
			    throw new Exception("qualifier type unexpected: " + qualifier + ":" + qualifier.getDeclaration());
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
				offset = offset*4;
				StringUtility.appendIndLn(sb, "mov dword eax, [eax] \t; point to VTable");	//enter object
				StringUtility.appendIndLn(sb, "mov dword eax, [eax + 1] \t; point to Ugly");	// ASSUME: the second entry of VTable is ugly column
				StringUtility.appendIndLn(sb, "call [eax + " + offset + "] \t; call interface method.");
				//TODO: check if the level of indirection is proper 
			} else {
				int offset = tDecl.getMethodOffSet(NameHelper.mangle(mDecl));
				offset = (offset + 2) * 4;	// real offset
				StringUtility.appendIndLn(sb, "mov dword eax, [eax] \t; point to VTable");
				StringUtility.appendIndLn(sb, "call [eax + " + offset + "] \t; call class method."); //skip ugly and hierarchy Table
			}
    	}
    }
    
    private int generateArgs(StringBuilder sb, List<Expression> argList) throws Exception {
    	int numArgs = 0;
    	if (argList != null) {
	    	for (numArgs =0; numArgs < argList.size() ; numArgs++) {
	    		Expression expr = argList.get(numArgs);
	    		expr.accept(this);
	    		StringUtility.appendLine(sb, expr.getCode());
	    		StringUtility.appendIndLn(sb, "push eax \t; push argument " + numArgs);
	    	}
    	}
    	return numArgs;
    }

    public static void generateFieldAddr(StringBuilder sb, FieldDeclaration fDecl) throws Exception {
	TypeDeclaration parent = (TypeDeclaration) fDecl.getParent();
	int offset = parent.getFieldOffSet(fDecl.id);
	offset = 4 * offset;
	StringUtility.appendIndLn(sb, "mov dword eax, [ebp + 8] \t; put object address in eax");
	StringUtility.appendIndLn(sb, "mov dword eax, [eax] \t; enter object");
	StringUtility.appendIndLn(sb, "add eax, " +  offset + " \t; field address");	// eax contains field address
    }
    
    public void visit(SimpleName node) throws Exception {
    	StringBuilder sb = new StringBuilder();
    	ASTNode decl = node.getDeclaration();
    	if (decl instanceof FieldDeclaration) {	// field, has to be instance field
    		FieldDeclaration fDecl = (FieldDeclaration) decl;
    		TypeDeclaration parent = (TypeDeclaration) fDecl.getParent();
    		int offset = parent.getFieldOffSet(fDecl.id);
    		offset = 4 * offset;
    		StringUtility.appendIndLn(sb, "mov dword eax, [ebp + 8] \t; put object address in eax");
    		StringUtility.appendIndLn(sb, "mov dword eax, [eax] \t; enter object");
    		StringUtility.appendIndLn(sb, "add eax, " +  offset + " \t; field address");	// eax contains field address
    	} else if (decl instanceof VariableDeclaration) {	// variable
    		VariableDeclaration vDecl = (VariableDeclaration) decl;
		genVarAddress(sb, vDecl);
	
    	} else {
    		throw new Exception("Simple name declaration unexpected: " + node);
    	}
	if (!isLV) {
	    StringUtility.appendIndLn(sb, "mov eax, [eax]"); // if not lvalue, move value into eax
	}

    	node.attachCode(sb.toString());
    }
    
    public void visit(QualifiedName node) throws Exception {
    	StringBuilder sb = new StringBuilder();
    	Name qualifier = node.getQualifier();
    	ASTNode qDecl = qualifier.getDeclaration();
    	if (qDecl instanceof TypeDeclaration) {	//A.B.c.d, node is static field
    		String label = SigHelper.getFieldSig((FieldDeclaration) node.getDeclaration());	// directs to parent, not instance field
    		StringUtility.appendIndLn(sb, "mov dword eax, " + label);
    	} else if (qDecl instanceof FieldDeclaration || qDecl instanceof VariableDeclaration) {
    		qualifier.accept(this);
    		StringUtility.appendIndLn(sb, qualifier.getCode());
    		
		if (node.getDeclaration() == null && node.getID().equals("length")) {
		    // fucking array length
		    StringUtility.appendIndLn(sb, "mov eax, [eax]"); // enter array 
		    StringUtility.appendIndLn(sb, "add eax, 4"); // array length
		    return;
		}
		
		// node is instance field, with object in eax
		FieldDeclaration fDecl = (FieldDeclaration) node.getDeclaration();
		TypeDeclaration tDecl = (TypeDeclaration) fDecl.getParent();
    		int offset = tDecl.getFieldOffSet(fDecl.id);
    		offset = (offset + 1) * 4;	// real offset 
    		StringUtility.appendIndLn(sb, "mov eax, [eax]");	// enter object
    		StringUtility.appendIndLn(sb, "add eax, " + offset);
    	} else {
    		throw new Exception("qualified name prefix not recoginsed: " + qualifier.toString());
    	}

	if (!isLV) {
	    StringUtility.appendIndLn(sb, "mov eax, [eax]"); // if not lvalue, move value into eax
	}
	node.attachCode(sb.toString());
    }
    
    public void visit(FieldAccess node) throws Exception {
		StringBuilder sb = new StringBuilder();
		
		node.expr.accept(this);
		StringUtility.appendLine(sb, node.getCode());
		// assume object at eax
		if (node.expr.getType() instanceof ArrayType && node.id.equals("length")) {    // array.length
		    StringUtility.appendIndLn(sb, "mov eax, [eax] \t; enter array"); // enter array
		    StringUtility.appendIndLn(sb, "add eax, 4");
		} else {// instance field
		    TypeDeclaration tDecl = node.expr.getType().getDeclaration();
		    int offset = tDecl.getFieldOffSet(node.id.toString());
		    offset = offset * 4;// real offset
		    StringUtility.appendIndLn(sb, "mov eax, [eax] \t; enter object");
		    StringUtility.appendIndLn(sb, "add eax, " + offset);
		}
		
		node.attachCode(sb.toString());
    }
    
    public void visit(ArrayCreationExpression node) throws Exception {
    	StringBuilder sb = new StringBuilder();
	StringUtility.appendIndLn(sb, "; array creation");

    	// evaluate expression
    	node.expr.accept(this);
    	sb.append(node.getCode());
    	
    	// size in eax
    	StringUtility.appendIndLn(sb, "push eax");
    	extern.add("__malloc");
    	StringUtility.appendIndLn(sb, "call __malloc");
    	StringUtility.appendIndLn(sb, "pop ebx"); // put size in ebx
    	
    	// array address in eax
    	StringUtility.appendIndLn(sb, "push eax");
    	StringUtility.appendIndLn(sb, "mov eax, [eax]"); // enter array
    	StringUtility.appendIndLn(sb, "mov dword [eax], " + SigHelper.getArrayVTableSigFromNonArray(node.type));	// first place is the vtable, vtable then points to hierarchy
    	StringUtility.appendIndLn(sb, "add eax, 1"); // second place holds size
    	StringUtility.appendIndLn(sb, "mov dword [eax], ebx" );
    	StringUtility.appendIndLn(sb, "pop eax");	// put array address back in eax, done
        extern.add(SigHelper.getArrayVTableSigFromNonArray(node.type));
	node.attachCode(sb.toString());
    }

    public void visit(ArrayAccess node) throws Exception {
	StringBuilder sb = new StringBuilder();

	node.index.accept(this);
	sb.append(node.index.getCode());
	StringUtility.appendIndLn(sb, "push eax"); // push index

	node.array.accept(this);
	sb.append(node.array.getCode());
	StringUtility.appendIndLn(sb, "pop ebx"); // get index
	StringUtility.appendIndLn(sb, "mov eax, [eax]"); // enter array
	StringUtility.appendIndLn(sb, "add eax, 1"); // skip vtable
	StringUtility.appendIndLn(sb, "add eax, ebx");
	
	node.attachCode(sb.toString());
    }
    
    public void visit(SimpleType node) throws Exception {
	// intentionally do nothing
    }
    
}
