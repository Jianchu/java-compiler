package code_generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utility.StringUtility;
import ast.ASTNode;
import ast.ArrayAccess;
import ast.ArrayCreationExpression;
import ast.ArrayType;
import ast.AssignmentExpression;
import ast.BodyDeclaration;
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
import environment.SymbolTable;
import environment.TraversalVisitor;
import exceptions.NameException;

public class ExpressionCodeGenerator extends TraversalVisitor {

    private static final String FALSE = "0x0";
    private static final String TRUE = "0xffffffff";
    private int litCounter = 0;
    public static StringBuilder stringLitData = new StringBuilder();
    private Set<String> extern;
    public int infixCounter = 0;
    private int instanceOfCounter = 0;
    StringBuilder dataSection;
    public static MethodDeclaration currentMethod;
    private int ncCounter = 0;
    private int aaCounter = 0; // for array access label
    private int castCounter = 0; // for cast finish label
     private boolean isLV = false;
    //private boolean isPrefix = false;

     public ExpressionCodeGenerator(Set<String> extern, StringBuilder dataSection) {
	 this.extern = extern;
	 this.dataSection = dataSection;
	 
     }

     /*
      * Literals
      */
     // String is Object.
     public void visit(StringLiteral node) throws Exception {
         StringBuilder stringText = new StringBuilder();
	 litCounter++;

         String strSig = SigHelper.getClssSigWithVTable(SymbolTable.getGlobal().get("java.lang.String"));
         extern.add(strSig);
	 StringUtility.appendLine(dataSection, "STRING_" + litCounter + ":" + "\t; define label for string literal");
         StringUtility.appendLine(dataSection, "\tdd " + strSig);
         StringUtility.appendLine(dataSection, "\tdd " + "STRCHARS_" + litCounter);

         String charArrSig = SigHelper.getArrayVTableSigFromNonArray(new PrimitiveType(PrimitiveType.Value.CHAR));
         extern.add(charArrSig);
	 StringUtility.appendLine(dataSection, "STRCHARS_" + litCounter + ":");
         StringUtility.appendLine(dataSection, "\tdd " + charArrSig);
         List<String> string = StringLitHelper(node.value);
         StringUtility.appendLine(dataSection, "\t" + "dd " + string.size());
         for (String s : string) {
             if (s.charAt(0) == '\\') {
                 StringUtility.appendLine(dataSection, "\t" + "dd `" + s + "`");
             } else {
                 StringUtility.appendLine(dataSection, "\t" + "dd '" + s + "'");
             }
         }
         StringUtility.appendLine(stringText, "\tmov dword eax, " + "STRING_" + litCounter);
	 node.attachCode(stringText.toString());
     }

    private List<String> StringLitHelper(String value) {
        List<String> string = new ArrayList<String>();
        int length = value.length();
        for (int i = 0; i < length; i++) {
            char current = value.charAt(i);
            if (current == '\\') {
                StringBuilder escape = new StringBuilder();
                escape.append(current);
                char next = value.charAt(++i);
                if (next >= '0' && next <= '3') {
                    escape.append(next);
                    if (i < length - 1) {
                        next = value.charAt(++i);
                        if (next >= '0' && next <= '7') {
                            escape.append(next);
                            if (i < length - 1) {
                                next = value.charAt(++i);
                                if (next >= '0' && next <= '7') {
                                    escape.append(next);
                                    string.add(escape.toString());
                                } else {
                                    escape.append(next);
                                    next = value.charAt(++i);
                                }
                            } else {
                                string.add(escape.toString());
                            }
                        } else {
                            string.add(escape.toString());
                            string.add(String.valueOf(next));
                        }
                    } else {
                        string.add(escape.toString());
                    }
                } else if (next >= '4' && next <= '7') {
                    escape.append(next);
                    if (i < length - 1) {
                        next = value.charAt(++i);
                        if (next >= '0' && next <= '7') {
                            escape.append(next);
                            string.add(escape.toString());
                        } else {
                            string.add(escape.toString());
                            string.add(String.valueOf(next));
                        }
                    } else {
                        string.add(escape.toString());
                    }
                } else {
                    escape.append(next);
                    string.add(escape.toString());
                }
            } else {
                string.add(String.valueOf(current));
            }
        }
        return string;
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
             String rhsText = "";
             if (!(node.lhs.getType() instanceof PrimitiveType) && node.expr.getType() instanceof PrimitiveType) {
                rhsText = CreateCICEText(node.expr);
             } else {
                node.expr.accept(this);
                 rhsText = node.expr.getCode();
             }
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


    private void generatePlus(InfixExpression node) throws Exception {
        StringBuilder plusText = new StringBuilder();
        if (node.lhs != null) {
            node.lhs.accept(this);
        }
        if (node.rhs != null) {
            node.rhs.accept(this);
        }

        Type lhsType = node.lhs.getType();
        Type rhsType = node.rhs.getType();
        if ((lhsType instanceof SimpleType && lhsType.getDeclaration().getFullName().equals("java.lang.String"))
                || (rhsType instanceof SimpleType && rhsType.getDeclaration().getFullName().equals("java.lang.String"))) {
            MethodInvocation lhsValueOf = createValueOf(node.lhs);
            MethodInvocation rhsValueOf = createValueOf(node.rhs);
            MethodInvocation concat = crateConcat(lhsValueOf, rhsValueOf);
            concat.accept(this);
            plusText.append(concat.getCode());
        } else {
            String lhsCode = node.lhs.getCode();
            String rhsCode = node.rhs.getCode();
            plusText.append(lhsCode);
            StringUtility.appendLine(plusText, "push eax ; store lhs");
            plusText.append(rhsCode);
            StringUtility.appendLine(plusText, "pop ebx ; restore lhs");
            StringUtility.appendLine(plusText, "add eax, ebx");
        }
        node.attachCode(plusText.toString());
    }

    /**
     * 6concat16java.lang.String
     * 7valueOf16java.lang.String
     * 7valueOf16java.lang.Object
     * 7valueOf5short
     * 7valueOf4char
     * 7valueOf7boolean
     * 7valueOf3int
     * 7valueOf4byte
     */
    
    private MethodInvocation createValueOf(Expression expr) {
        TypeDeclaration tDec = SymbolTable.getGlobal().get("java.lang.String");
        Map<String, MethodDeclaration> methods = tDec.getEnvironment().methods;
        Type type = expr.getType();
        MethodDeclaration Mdec = null;
        if (type instanceof PrimitiveType) {
            PrimitiveType pType = (PrimitiveType) type;
            switch (pType.value) {
            case BOOLEAN:
                Mdec =  methods.get("7valueOf7boolean");
              break;
            case CHAR:
                Mdec =  methods.get("7valueOf4char");
              break;
            default:
                Mdec =  methods.get("7valueOf3int");
          }
        } else if (type instanceof SimpleType) {
            // String
            if (type.getDeclaration().getFullName().equals("java.lang.String")) {
                Mdec = methods.get("7valueOf16java.lang.String");
                // Object
            } else {
                Mdec = methods.get("7valueOf16java.lang.Object");
            }
        } else if (type == null) {
            Mdec = methods.get("7valueOf16java.lang.String");
            StringLiteral nullLit = new StringLiteral("null");
            nullLit.attachType(this.simpleTypeBuilder(tDec));
            expr = nullLit;
        } else if (type instanceof ArrayType) {
            Mdec = methods.get("7valueOf16java.lang.String");
            StringLiteral nullLit = new StringLiteral(SigHelper.getTypeSig(type));
            nullLit.attachType(this.simpleTypeBuilder(tDec));
            expr = nullLit;
        }

        // qualifier
        SimpleName sName = new SimpleName("java.lang.String");
        sName.attachDeclaration(tDec);
        // qualified name
        QualifiedName qName = new QualifiedName(sName, "valueOf");
        qName.attachDeclaration(Mdec);
        // parameter
        List<Expression> arglist = new ArrayList<Expression>();
        arglist.add(expr);
        MethodInvocation mInvo = new MethodInvocation(qName, null, arglist);
        return mInvo;
    }

    //
    private MethodInvocation crateConcat(Expression fromStringType, Expression fromNonStringType) {
        TypeDeclaration tDec = SymbolTable.getGlobal().get("java.lang.String");
        Map<String, MethodDeclaration> methods = tDec.getEnvironment().methods;
        MethodDeclaration Mdec =  methods.get("6concat16java.lang.String");
        //name and declaration
        SimpleName sName = new SimpleName("concat");
        sName.attachDeclaration(Mdec);
        //parameters
        List<Expression> arglist = new ArrayList<Expression>();
        arglist.add(fromNonStringType);
        MethodInvocation mInvo = new MethodInvocation(fromStringType, sName, arglist);
        return mInvo;
    }
    
    private void env() {
        TypeDeclaration tDec = SymbolTable.getGlobal().get("java.lang.String");
        Map<String, MethodDeclaration> methods = tDec.getEnvironment().methods;
        for (String s : methods.keySet()) {
            System.out.println(s);
        }
    }
    
    
     @Override
     public void visit(InfixExpression node) throws Exception {
        if (node.op.equals(InfixExpression.Operator.PLUS)) {
            generatePlus(node);
            return;
        }
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
                 StringUtility.appendLine(infixText, "mov eax, ebx");
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
			     extern.add("java.lang.Boolean#~init~$Z$implementation");
			     StringUtility.appendLine(infixText, "call java.lang.Boolean#~init~$Z$implementation");
			     StringUtility.appendLine(infixText, "pop ebx\t; clean up");
			     StringUtility.appendLine(infixText, "pop ebx\t; clean up");
			     StringUtility.appendLine(infixText, "push eax");
			     extern.add("java.lang.Boolean#toString$$implementation");
			     StringUtility.appendLine(infixText, "call java.lang.Boolean#toString$$implementation");
			     break;
			   case CHAR:
			     extern.add("java.lang.Character#~init~$C$implementation");
			     StringUtility.appendLine(infixText, "call java.lang.Character#~init~$C$implementation");
			     StringUtility.appendLine(infixText, "pop ebx\t; clean up");
			     StringUtility.appendLine(infixText, "pop ebx\t; clean up");
			     StringUtility.appendLine(infixText, "push eax");
			     extern.add("java.lang.Character#toString$$implementation");
			     StringUtility.appendLine(infixText, "call java.lang.Character#toString$$implementation");
			     break;
			   default:
			     extern.add("java.lang.Integer#~init~$I$implementation");
			     StringUtility.appendLine(infixText, "call java.lang.Integer#~init~$I$implementation");
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
		     if (lhsType instanceof PrimitiveType) {
			 StringUtility.appendLine(infixText, "push eax");
			 extern.add("__malloc");
			 StringUtility.appendLine(infixText, "call __malloc");
			 StringUtility.appendLine(infixText, "push eax \t; push object address");
			 switch (((PrimitiveType) lhsType).value) {
			   case BOOLEAN:
			     extern.add("java.lang.Boolean#~init~$Z$implementation");
			     StringUtility.appendLine(infixText, "call java.lang.Boolean#~init~$Z$implementation");
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
		 StringUtility.appendLine(infixText, "cmp ebx, 0");
                 StringUtility.appendLine(infixText, "jne INFIX_" + n);
                 extern.add("__exception");
                 StringUtility.appendLine(infixText, "call __exception");
                 StringUtility.appendLine(infixText, "INFIX_" + n + ":");
		 StringUtility.appendLine(infixText, "cdq\t; sign-extend");
		 StringUtility.appendLine(infixText, "idiv ebx");
		 break;
	       case MOD:
		 StringUtility.appendLine(infixText, "cmp ebx, 0");
                 StringUtility.appendLine(infixText, "jne INFIX_" + n);
                 extern.add("__exception");
                 StringUtility.appendLine(infixText, "call __exception");
                 StringUtility.appendLine(infixText, "INFIX_" + n + ":");
		 StringUtility.appendLine(infixText, "cdq\t; sign-extend");
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
	     StringUtility.appendLine(instanceofText, "cmp eax, " + FALSE + "\t ;check null", 2);
	     StringUtility.appendLine(instanceofText, "je _INSTANCEOFEND_" + instanceOfCounter, 2);
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
	 String unaryCode;
	 if (node.unary != null) {
	     node.unary.accept(this); //NOTE added this -- Z
	     unaryCode = node.unary.getCode();
	 } else {
	     unaryCode = "\tmov eax, 0\n";
	 }
	 
	 castText.append(unaryCode);

	 int offset = 0;
	 boolean isPrimitive = false;
	 if (castToType instanceof SimpleType) {
	     SimpleType sType = (SimpleType)castToType;
	     offset = HierarchyTableBuilder.getTypeOffSet(sType.getDeclaration().getFullName());
	 } else if (castToType instanceof ArrayType) {
	     ArrayType aType = (ArrayType)castToType;
	     if (aType.type instanceof SimpleType) {
	         offset = HierarchyTableBuilder.getTypeOffSet(aType.type.getDeclaration().getFullName() + "[]");
	     } else {
	         offset = HierarchyTableBuilder.getTypeOffSet(aType.type.toString() + "[]");
	     }
	 } else if (castToType instanceof PrimitiveType) {
	     isPrimitive = true;
	 }
	 if (!isPrimitive) {
	     //TODO: check for null
	     StringUtility.appendIndLn(castText, "cmp eax, 0"); // check for null
	     StringUtility.appendIndLn(castText, "je CastFinish" + castCounter);

	     int frame = offset * 4;
	     StringUtility.appendLine(castText, "mov ebx, eax \t ;copy eax", 2);
	     StringUtility.appendLine(castText, "mov eax, [eax] \t ;get first frame of object, the pointer of VTable", 2);
	     StringUtility.appendLine(castText, "mov eax, [eax] \t ;get first frame of VTable, the point of subclass table", 2);
            StringUtility.appendLine(castText, "add eax, " + frame
                    + "\t ;get the pointer of type in subclass table", 2);
	     StringUtility.appendLine(castText, "cmp dword [eax], " + TRUE, 2);
	     StringUtility.appendLine(castText, "jne __exception", 2);
	     StringUtility.appendLine(castText, "mov eax, ebx \t ;restore eax", 2);
	     
	     StringUtility.appendLine(castText, "CastFinish" + (castCounter++) + ":");	     
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
	     String initText = "";
	     if (!(node.type instanceof PrimitiveType) && node.initializer.getType() instanceof PrimitiveType) {
                initText = CreateCICEText(node.initializer);
	     } else {
	         node.initializer.accept(this);
	         initText = node.initializer.getCode();
	     }
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
	 if (offset < 0) {	// formals starts from -1, actual offset should be 12
	     offset = (-(offset - 2)) * 4;	// real offset 
	     StringUtility.appendIndLn(sb, "mov dword eax, ebp ");
	     StringUtility.appendIndLn(sb, "add eax, " + offset + " \t; eax contains formal address");
	 } else {	// locals starts from 0, actual offset should be 4
	     offset = (offset + 1) * 4;
	     StringUtility.appendIndLn(sb, "mov dword eax, ebp ");
	     StringUtility.appendIndLn(sb, "sub eax, " + offset + "\t; eax contains local address");	// eax now points to object
	 }
     }

    public String CreateCICEText(Expression expr) throws Exception {
         List<Expression> arglist = new ArrayList<Expression>();
         arglist.add(expr);
         Type type = expr.getType();
         ClassInstanceCreationExpression cice = null;
         String ciceText = "";
         if (type instanceof PrimitiveType) {
             PrimitiveType pType = (PrimitiveType) type;
             switch (pType.value) {
             case BOOLEAN:
                 TypeDeclaration bDec = SymbolTable.getGlobal().get("java.lang.Boolean");
                 SimpleType sBType = simpleTypeBuilder(bDec);
                 MethodDeclaration bConstructor = getConstructor(bDec);
                 cice = new ClassInstanceCreationExpression(sBType, arglist);
                 cice.addConstructor(getConstructor(bDec));
               break;
             case CHAR:
                 TypeDeclaration cDec = SymbolTable.getGlobal().get("java.lang.Character");
                 SimpleType sCType = simpleTypeBuilder(cDec);
                 cice = new ClassInstanceCreationExpression(sCType, arglist);
                 cice.addConstructor(getConstructor(cDec));
               break;
             default:
                 TypeDeclaration iDec = SymbolTable.getGlobal().get("java.lang.Integer");
                 SimpleType sIType = simpleTypeBuilder(iDec);
                 cice = new ClassInstanceCreationExpression(sIType, arglist);
                 cice.addConstructor(getConstructor(iDec));
           } 
         }
         if (cice != null) {
             this.visit(cice);
             ciceText = cice.getCode();
         }
         return ciceText;
     }
     
    private MethodDeclaration getConstructor(TypeDeclaration tDec) {
        for (BodyDeclaration bDecl : tDec.members) {
            if (bDecl instanceof MethodDeclaration) {
                MethodDeclaration mDec = (MethodDeclaration)bDecl;
                if (mDec.isConstructor) {
                    return mDec;
                }
            }
        }
        return null;
    }

     private SimpleType simpleTypeBuilder(TypeDeclaration typeDec) {
         String name = typeDec.getFullName();
         SimpleType simpleType = new SimpleType(new SimpleName(name));
         simpleType.attachDeclaration(typeDec);
         return simpleType;
     }
     /*
      * OO features
      */

     @Override
     public void visit(ClassInstanceCreationExpression node) throws Exception {
	 StringBuilder sb = new StringBuilder();    
	 StringUtility.appendIndLn(sb, "; class instance creation: " + node.type);
	 CodeGenUtil.saveRegisters(sb);

	 int numArgs = 0;
	 // generate code for arguments
	 numArgs = generateArgs(sb, node.arglist);

	 // malloc
	 TypeDeclaration tDecl = node.type.getDeclaration();
	 int objSize = tDecl.getFieldOffSetList().size() + 1;	//	leave space for counter
	 StringUtility.appendIndLn(sb, "mov eax, 4*" + objSize + "\t; size of object");
	 extern.add("__malloc");
	 StringUtility.appendIndLn(sb, "call __malloc");

	 // pointer to VTable
	 String vt = SigHelper.getClssSigWithVTable(tDecl);
	 extern.add(vt);
	 StringUtility.appendIndLn(sb, "mov dword [eax], " + vt);

	 // implicit super call
	 if (tDecl.superClass != null) {
		 MethodDeclaration superConstructor = getDefaultConstructor(tDecl.superClass.getDeclaration());
		 generateConstructorCall(sb, superConstructor);
	 }

	 // call actual constructor
	 generateConstructorCall(sb, node.getConstructor());

	 //clean up arguments
	 StringUtility.appendIndLn(sb, "add esp, 4 * " + numArgs);
	 CodeGenUtil.restoreRegisters(sb);

	 node.attachCode(sb.toString());
     }

     private void generateConstructorCall(StringBuilder sb, MethodDeclaration constructorDecl) {
	 StringUtility.appendIndLn(sb, "push eax");	// save object reference
	 String constructorLabel = SigHelper.getMethodSigWithImp(constructorDecl);
	 extern.add(constructorLabel.trim());
	 StringUtility.appendIndLn(sb, "call " + constructorLabel);
	 // clean up
	 StringUtility.appendIndLn(sb, "pop eax \t; pop object address back in eax");
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

	 //caller save registers
	 CodeGenUtil.saveRegisters(sb);

	 // generate code for arguments
	 int numArgs = 0;
	 //	 numArgs = generateArgs(sb, node.arglist);
	 
	 MethodDeclaration mDecl = null;
	 if (node.id != null) {
		 // Primary.ID(...)
		 node.expr.accept(this);	// generate code for Primary expression
		 StringUtility.appendLine(sb, node.expr.getCode());	// by this point eax should contain address to object return by Primary expression
		 StringUtility.appendIndLn(sb, "push eax \t; push object for method invocation");
		 mDecl = (MethodDeclaration) node.id.getDeclaration();	// the actual method being called
		 
	 } else {
		 // Name(...)
	     if (node.expr instanceof SimpleName) {	// SimpleName(...), implicit this
			 SimpleName sn = (SimpleName) node.expr;
			 StringUtility.appendIndLn(sb, "mov dword eax, [ebp + 8] \t; move object address to eax"); // this only happens in the method of the same class
			 StringUtility.appendIndLn(sb, "push eax \t; push object address");
			 mDecl = (MethodDeclaration) sn.getDeclaration();
			 
		 } else {	// QualifiedName(...)
			 QualifiedName qn = (QualifiedName) node.expr;
			 mDecl = (MethodDeclaration) qn.getDeclaration();
			 Name qualifier = qn.getQualifier();
			 if (qualifier.getDeclaration() instanceof TypeDeclaration) {	// static methods
				 StringUtility.appendIndLn(sb, "push 0 \t; place holder because there is no this object for static method");
			
			 } else if (qualifier.getDeclaration() instanceof FieldDeclaration || qualifier.getDeclaration() instanceof VariableDeclaration) {	// instance method
			     boolean oldIsLV = isLV;
			     isLV = false;
			     qn.getQualifier().accept(this); 	// generate code from name (accessing instance field, or local variable
			     isLV = oldIsLV;
			     StringUtility.appendLine(sb, qn.getQualifier().getCode());
			     generateNullCheck(sb);
			     StringUtility.appendIndLn(sb, "push eax \t; push object for method invocation");
			 } else {
			     throw new Exception("qualifier type unexpected: " + qualifier + ":" + qualifier.getDeclaration());
			 }
	     }
	 }
	 numArgs = generateArgsAndSwap(sb, node.arglist);
	 // call method
	 generateMethodCall(sb, mDecl);


		 // clean up
		 StringUtility.appendIndLn(sb, "add esp, " + (numArgs+1) + "*4" + "\t; caller cleanup arguments.");	
		 CodeGenUtil.restoreRegisters(sb);
		 node.attachCode(sb.toString()); 

     }
    
    private void generateNullCheck(StringBuilder sb) {
	StringUtility.appendIndLn(sb, "cmp eax, 0");
	StringUtility.appendIndLn(sb, "jne NullCheckOK" + ncCounter);
	extern.add("__exception");
	StringUtility.appendIndLn(sb, "call __exception");
	StringUtility.appendLine(sb, "NullCheckOK" + (ncCounter++) + ":");
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
	     String label;
	     if (mDecl.modifiers.contains(Modifier.NATIVE)) {
		 StringUtility.appendIndLn(sb, "mov eax, [esp + 4]");
		 label = SigHelper.getNativeSig(mDecl);
	     } else {
		 label = SigHelper.getMethodSigWithImp(mDecl);
	     }
	     extern.add(label.trim());
	     StringUtility.appendIndLn(sb, "call " + label);
	     
	 } else {	// instance method
			 // generate method call
			 if (tDecl.isInterface) {	// interface method
				 int offset = OffSet.getInterfaceMethodOffset(NameHelper.mangle(mDecl));
				 offset = offset*4;
				 StringUtility.appendIndLn(sb, "mov dword eax, [eax] \t; point to VTable");	//enter object
				 StringUtility.appendIndLn(sb, "mov dword eax, [eax + 4] \t; point to Ugly");	// ASSUME: the second entry of VTable is ugly column
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

     private int generateArgsAndSwap(StringBuilder sb, List<Expression> argList) throws Exception {
	 int numArgs = 0;
	 if (argList != null) {
		 for (numArgs =0; numArgs < argList.size() ; numArgs++) {
			 Expression expr = argList.get(numArgs);
			 expr.accept(this);
			 StringUtility.appendLine(sb, expr.getCode());
			 StringUtility.appendLine(sb, "pop ebx"); //pop object address
			 StringUtility.appendIndLn(sb, "push eax \t; push argument " + numArgs);
			 StringUtility.appendIndLn(sb, "push ebx"); // push object address back
		 }
	 }
	 StringUtility.appendIndLn(sb, "pop eax"); // to keep object address in eax
	 StringUtility.appendIndLn(sb, "push eax"); 
	 return numArgs;
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

    public static void generateFieldAddr(StringBuilder sb, FieldDeclaration fDecl, Set<String> extern) throws Exception {
	 if (fDecl.modifiers.contains(Modifier.STATIC)) {
	     String label = SigHelper.getFieldSigWithImp(fDecl);
	     extern.add(label);
	     StringUtility.appendIndLn(sb, "mov eax, " + label);
	 }else {
	     TypeDeclaration parent = (TypeDeclaration) fDecl.getParent();
	     int offset = parent.getFieldOffSet(SigHelper.getFieldSig(fDecl));
	     offset = 4 * (offset + 1);
	     StringUtility.appendIndLn(sb, "mov dword eax, [ebp + 8] \t; put object address in eax");

	     StringUtility.appendIndLn(sb, "add eax, " +  offset + " \t; field address");	// eax contains field address
	 }
     }

     public void visit(SimpleName node) throws Exception {
	 StringBuilder sb = new StringBuilder();
	 ASTNode decl = node.getDeclaration();
	 if (decl instanceof FieldDeclaration) {	// field, has to be instance field
		 FieldDeclaration fDecl = (FieldDeclaration) decl;
		 generateFieldAddr(sb, fDecl, extern);

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
	     
	     String label = SigHelper.getFieldSigWithImp((FieldDeclaration) node.getDeclaration());	// directs to parent, not instance field
	     extern.add(label);
	     StringUtility.appendIndLn(sb, "mov dword eax, " + label);
	 } else if (qDecl instanceof FieldDeclaration || qDecl instanceof VariableDeclaration) {
	     boolean oldLV = isLV;
	     isLV = false;
	     // used to be a isPrefix
	     qualifier.accept(this);
	     isLV = oldLV;
		 StringUtility.appendIndLn(sb, qualifier.getCode());
		 
		 // before accessm, check null
		 generateNullCheck(sb);

		 if (node.getDeclaration() == null && node.getID().equals("length")) {
		     // fucking array length

		     StringUtility.appendIndLn(sb, "add eax, 4 ; array.length"); // array length
		 } else {

		     // node is instance field, with object in eax
		     FieldDeclaration fDecl = (FieldDeclaration) node.getDeclaration();
		     TypeDeclaration tDecl = (TypeDeclaration) fDecl.getParent();
		     int offset = tDecl.getFieldOffSet(SigHelper.getFieldSig(fDecl));
		     offset = (offset + 1) * 4;	// real offset 
		     StringUtility.appendIndLn(sb, "add eax, " + offset);
		 }
		 
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
		 
		 StringUtility.appendIndLn(sb, getCodeForPrefix(node.expr));
		 //node.expr.accept(this);
		 //StringUtility.appendLine(sb, node.expr.getCode());
		 // assume object at eax
		 if (node.expr.getType() instanceof ArrayType && node.id.toString().equals("length")) {    // array.length
		     
		     StringUtility.appendIndLn(sb, "add eax, 4");
		 } else {// instance field
		     TypeDeclaration tDecl = node.expr.getType().getDeclaration();
		     int offset = tDecl.getFieldOffSet(SigHelper.getFieldSig((FieldDeclaration) node.id.getDeclaration()));
		     offset = (offset+1) * 4;// real offset
		     StringUtility.appendIndLn(sb, "add eax, " + offset);
		 }

		 processLV(sb);

		 node.attachCode(sb.toString());
     }

    private String getCodeForPrefix(Expression e) throws Exception {
	boolean oldIsLV = isLV;
	isLV = false;
	e.accept(this);
	isLV = oldIsLV;
	return e.getCode();
    }

     public void visit(ArrayCreationExpression node) throws Exception {
	 StringBuilder sb = new StringBuilder();
	 StringUtility.appendIndLn(sb, "; array creation");

	 // evaluate expression
	 node.expr.accept(this);
	 sb.append(node.expr.getCode());
	

	 // size in eax
	 StringUtility.appendIndLn(sb, "push eax");
	 StringUtility.appendIndLn(sb, "mov ebx, 4");
	 StringUtility.appendIndLn(sb, "mul ebx");
	 StringUtility.appendIndLn(sb, "add eax, 8"); //room for vtable and size

	 extern.add("__malloc");
	 StringUtility.appendIndLn(sb, "call __malloc");
	 StringUtility.appendIndLn(sb, "pop ebx"); // put size in ebx

	 // array address in eax
	 StringUtility.appendIndLn(sb, "push eax");
	 //StringUtility.appendIndLn(sb, "mov eax, [eax]"); // enter array
	 StringUtility.appendIndLn(sb, "mov dword [eax], " + SigHelper.getArrayVTableSigFromNonArray(node.type));	// first place is the vtable, vtable then points to hierarchy
	 StringUtility.appendIndLn(sb, "add eax, 4"); // second place holds size
	 StringUtility.appendIndLn(sb, "mov dword [eax], ebx" );
	 StringUtility.appendIndLn(sb, "pop eax");	// put array address back in eax, done
	 extern.add(SigHelper.getArrayVTableSigFromNonArray(node.type));
	 node.attachCode(sb.toString());
     }

    public void visit(ArrayAccess node) throws Exception {
	StringBuilder sb = new StringBuilder();
	
	boolean oldIsLV = isLV;
	
	isLV = false;
	node.array.accept(this);
	isLV = oldIsLV;
	sb.append(node.array.getCode());
	StringUtility.appendIndLn(sb, "push eax"); // push array address
	StringUtility.appendIndLn(sb, "push eax"); //again for later

	oldIsLV = isLV;
	isLV = false;
	node.index.accept(this);
	sb.append(node.index.getCode());
	isLV = oldIsLV;

	//StringUtility.appendIndLn(sb, "mov ecx, eax");
	StringUtility.appendIndLn(sb, "pop ebx");
	StringUtility.appendIndLn(sb, "mov ebx, [ebx+4]");
	StringUtility.appendIndLn(sb, "cmp eax, ebx");
	StringUtility.appendIndLn(sb, "jl ArrayAccessBody" + aaCounter);
	extern.add("__exception");
	StringUtility.appendIndLn(sb, "call __exception");
	
	StringUtility.appendLine(sb, "ArrayAccessBody" + (aaCounter++)  +": ");
	StringUtility.appendIndLn(sb, "mov ebx, eax ");
	StringUtility.appendIndLn(sb, "mov eax, 4 "); 
	StringUtility.appendIndLn(sb, "imul ebx ; get real offset");
	StringUtility.appendIndLn(sb, "mov ebx, eax"); // store offset in ebx
	

	StringUtility.appendIndLn(sb, "pop eax"); // get array
	StringUtility.appendIndLn(sb, "add eax, 8"); // skip vtable and size
	StringUtility.appendIndLn(sb, "add eax, ebx");
	
	processLV(sb);
	
	node.attachCode(sb.toString());
    }

    private void processLV(StringBuilder sb) throws Exception {
	if (!isLV ) {
	    StringUtility.appendIndLn(sb, "mov eax, [eax]");
	}
    }
    
    public void visit(SimpleType node) throws Exception {
	// intentionally do nothing
    }
    
}
