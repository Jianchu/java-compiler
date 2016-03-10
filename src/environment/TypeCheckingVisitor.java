package environment;

//import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ast.AST;
import ast.ASTNode;
import ast.ArrayAccess;
import ast.ArrayCreationExpression;
import ast.ArrayType;
import ast.AssignmentExpression;
import ast.BooleanLiteral;
import ast.CastExpression;
import ast.CharacterLiteral;
import ast.ClassInstanceCreationExpression;
import ast.CompilationUnit;
import ast.Expression;
import ast.ExpressionStatement;
import ast.FieldAccess;
import ast.FieldDeclaration;
import ast.ForStatement;
import ast.IfStatement;
import ast.InfixExpression;
import ast.InfixExpression.Operator;
import ast.InstanceofExpression;
import ast.IntegerLiteral;
import ast.MethodDeclaration;
import ast.MethodInvocation;
import ast.Modifier;
import ast.Name;
import ast.NullLiteral;
import ast.PrefixExpression;
import ast.PrimitiveType;
import ast.PrimitiveType.Value;
import ast.QualifiedName;
import ast.ReturnStatement;
import ast.SimpleName;
import ast.SimpleType;
import ast.StringLiteral;
import ast.ThisExpression;
import ast.Type;
import ast.TypeDeclaration;
import ast.VariableDeclaration;
import ast.VariableDeclarationExpression;
import ast.VariableDeclarationStatement;
import ast.Visitor;
import ast.WhileStatement;
import exceptions.NameException;
import exceptions.TypeCheckingException;

public class TypeCheckingVisitor extends EnvTraversalVisitor {
    private final Map<String, TypeDeclaration> global = SymbolTable.getGlobal();
    private final TypeHelper helper = new TypeHelper();
    private String currentTypeName;
    private MethodDeclaration currentMethod;
    private TypeDeclaration currentTypeDecl;
    private FieldDeclaration currentField;
    // maybe need to add or delete some methods...
    

    // for forward reference checking
	Set<FieldDeclaration> unseenFields = new HashSet<FieldDeclaration>();
	boolean isFieldInit = false;
    
    @Override
    public void visit(CompilationUnit node) throws Exception {
        for (TypeDeclaration typeDecl : node.types) {
            this.currentTypeName = typeDecl.getFullName();
            typeDecl.accept(this);
        }
    }

    @Override
    public void visit(ArrayAccess node) throws Exception {
        if (node.array != null) {
            node.array.accept(this);
        }
        if (node.index != null) {
            node.index.accept(this);
        }
        Type arrayType = node.array.getType();
        Type indexType = node.index.getType();
        if (arrayType instanceof ArrayType) {
            Set<Value> values = new HashSet<Value>();
            values.add(Value.BOOLEAN);
            if (CheckSinglePrimitive(indexType, values, null)) {
                node.attachType(((ArrayType) arrayType).type);
            } else {
                throw new TypeCheckingException("Index cannot be boolean.");
            }
        } else {
            throw new TypeCheckingException("Access a non-array type by array access.");
        }
    }

    @Override
    public void visit(ArrayCreationExpression node) throws Exception {
        if (node.type != null) {
            node.type.accept(this);
        } else {
            throw new TypeCheckingException("No null array.");
        }
        if (node.expr != null) {
            node.expr.accept(this);
        }
        Type indexType = node.expr.getType();
        
        Set<Value> values = new HashSet<Value>();
        values.add(Value.BOOLEAN);
        if (CheckSinglePrimitive(indexType, values, null)) {
            node.attachType(arrayTypeBuilder(node.type));
        } else {
            throw new TypeCheckingException("Index cannot be boolean.");
        }
    }

    /**
     * lhs: QualifiedName/SimpleName/ArrayAccess/FieldAccess
     * expr: QualifiedName/SimpleName/ArrayAccess/MethodInvocation/FieldAccess
     * 
     */
    @Override
    public void visit(AssignmentExpression node) throws Exception {
        if (node.lhs != null) {
            node.lhs.accept(this);
        }
        Type lhsType = node.lhs.getType();

        if (node.expr != null) {
            node.expr.accept(this);
        }
        Type exprType = node.expr.getType();

        /**
        if (node.lhs instanceof ArrayAccess) {
            ArrayAccess arrayAccess = (ArrayAccess) node.lhs;
            Type typeOfArray = arrayAccess.getType();
            if (TypeHelper.assignable(typeOfArray, exprType)) {
                node.attachType(typeOfArray);
            } else {
                throw new TypeCheckingException("Invalid assignment: incomparable types");
            }
            
        } 
        **/
        if (node.lhs instanceof QualifiedName) {
            QualifiedName qlhs = (QualifiedName) node.lhs;
            if (qlhs.isArrayLength) {
                throw new TypeCheckingException("Cannot assign a value to Array.length");
            }
        }
        
        if (TypeHelper.assignable(lhsType, exprType)) {
            node.attachType(lhsType);
        } else {
//        	System.out.println((lhsType instanceof ArrayType) + " : " + (exprType instanceof ArrayType));
            throw new TypeCheckingException("Invalid assignment, incomparable types: " + lhsType + ":=" + exprType);
        }
    }

    @Override
    public void visit(BooleanLiteral node) throws Exception {
        node.attachType(new PrimitiveType(Value.BOOLEAN));
    }

    @Override
    public void visit(CastExpression node) throws Exception {
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
        Type unaryType = node.unary.getType();
        
//        System.out.println("" + castToType + (castToType.getDeclaration() == null));
//        System.out.println("" + unaryType + (unaryType.getDeclaration() == null));


        // break cast into three cases:
        if (checkPrimitive(castToType, unaryType, false)) {
        	node.attachType(castToType);
        } else if (checkPrimitive(castToType, unaryType, true)) {
        	node.attachType(new PrimitiveType(Value.BOOLEAN));
        } else if (TypeHelper.assignable(castToType, unaryType) || TypeHelper.assignable(unaryType, castToType)) {
            if (castToType instanceof ArrayType) {
                ArrayType aCastToType = (ArrayType) castToType;
                node.attachType(aCastToType);
            } else if (castToType instanceof SimpleType) {
                node.attachType(simpleTypeBuilder((SimpleType) castToType));
            }
        } else {
        	throw new TypeCheckingException("No type found for cast.");
        }
    }

    @Override
    public void visit(CharacterLiteral node) throws Exception {
        node.attachType(new PrimitiveType(Value.CHAR));
    }
    

    /**
     * get the constructors of node.type
     * check whether node.arglist matches the parameters of one of the constructors
     * the type of node is node.type
     */
    @Override
    public void visit(ClassInstanceCreationExpression node) throws Exception {
        List<Expression> realParameters = new LinkedList<Expression>();
        if (node.type != null) {
            node.type.accept(this);
        }
        Type instanceType = node.type;
        if (node.arglist != null) {
            for (Expression expr : node.arglist) {
                expr.accept(this);
            }
            realParameters = node.arglist;
        }
        List<Type> realArgTypes = new ArrayList<Type>();
        for (Expression e : realParameters) {
            realArgTypes.add(e.getType());
        }
        String realConstructorName = NameHelper.mangle(node.type.toString(), realArgTypes);
        
        try {
            TypeDeclaration typeDec = node.type.getDeclaration();
            if (typeDec.modifiers.contains(Modifier.ABSTRACT)) {
                throw new TypeCheckingException("The type in a class instance creation expression must be a non-abstract class.");
            }
                        
            Map<String, MethodDeclaration> constructors = typeDec.getEnvironment().constructors;
            for (String s : constructors.keySet()) {
                List<Type> argTypes = new ArrayList<Type>();
                MethodDeclaration conDec = constructors.get(s);
                List<VariableDeclaration> DecParameters = conDec.parameters;
                for (VariableDeclaration varDec : DecParameters) {
                    argTypes.add(varDec.type);
                }
                String decConstructorName = NameHelper.mangle(node.type.toString(), argTypes);
                if (decConstructorName.equals(realConstructorName)) {
                    checkConstructorProtected(typeDec);
                    node.attachType(instanceType);
                    return;
                }
            }
            throw new TypeCheckingException("Not found corresponding constructor");
        } catch (Exception e) {
            // e.printStackTrace();
            throw new TypeCheckingException("Type environment not found");
        }
    }

    private void checkConstructorProtected(TypeDeclaration typeDec) throws TypeCheckingException {
        if (!(samePkg(typeDec, currentTypeDecl))) {
            // if not from the same package
            throw new TypeCheckingException(
                    "Illegal access to protected member: ");
        }
    }

    /**
     * TODO: 
     * node.expr is the qualifier
     * node.id is field's name
     * the type of A.B.C.f is type of C or C.
     */
    @Override
    public void visit(FieldAccess node) throws Exception {
        // TODO: check protected
    	if (node.expr != null) {
            node.expr.accept(this);
        }
        Type exprType = node.expr.getType();
        
        if (exprType instanceof ArrayType && node.id.toString().equals("length")) {
        	node.attachType(new PrimitiveType(Value.INT));
        } else if (exprType instanceof SimpleType) {
            TypeDeclaration prefixDecl = node.expr.getType().getDeclaration();
            FieldDeclaration fDecl = prefixDecl.getEnvironment().lookUpField(node.id.toString());
            node.id.attachDeclaration(fDecl);
            node.attachType(fDecl.type);
            if (fDecl.modifiers.contains(Modifier.PROTECTED))
            	checkProtectedField(prefixDecl, fDecl);
            
        } else {
        	throw new TypeCheckingException("field access unrecognized type." );
        }

        
    }



	@Override
    public void visit(InfixExpression node) throws Exception {
        if (node.lhs != null) {
            node.lhs.accept(this);
        }
        Type lhsType = node.lhs.getType();

        if (node.rhs != null) {
            node.rhs.accept(this);
        }
        Type rhsType = node.rhs.getType();

        Operator op = node.op;
        
        Type type = typeCheckInfixExp(lhsType, rhsType, op);
        node.attachType(type);
    }

    @Override
    public void visit(InstanceofExpression node) throws Exception {
        if (node.expr != null) {
            node.expr.accept(this);
        }
        if (node.type != null) {
            node.type.accept(this);
        }

        Type exprType = node.expr.getType();

        if ((exprType instanceof PrimitiveType) || (node.type instanceof PrimitiveType)) {
            throw new TypeCheckingException("Expression clause of instanceof must have reference type");
        }
        
        if (TypeHelper.assignable(exprType, node.type) || TypeHelper.assignable(node.type, exprType)) {
            node.attachType(new PrimitiveType(Value.BOOLEAN));
        } else {
            throw new TypeCheckingException("Uncomparable types in instanceof: " + exprType + ":=" + node.type  );
        }
    }

    @Override
    public void visit(IntegerLiteral node) throws Exception {
        node.attachType(new PrimitiveType(Value.INT));
    }

    /**
     * TODO:
     * node.expr: A.B.C.m
     * get declaration of methods in type C (or the type of C) who have name m. 
     * check whether node.arglist matches the parameters of one of the methods
     * the type of node is the return type of m.
     */
    @Override
    public void visit(MethodInvocation node) throws Exception {
        List<Type> argTypes = new ArrayList<Type>();
//        System.out.println("=================");
//        System.out.println((node.arglist != null? node.arglist.size():0));
        if (node.arglist != null) {
            for (Expression expr : node.arglist) {
                expr.accept(this);
                // save the parameter types to a list
                if (expr.getType() instanceof Void || expr.getType() == null) {
                	throw new TypeCheckingException("return type of parameter cannot be void.");
                }
                argTypes.add(expr.getType());
            }
        }
        
        if (node.id != null) {
        	// Primary.id(...)
        	node.expr.accept(this);	// there should always be an expression
        	TypeDeclaration prefixDecl = node.expr.getType().getDeclaration();
        	String methodName = NameHelper.mangle(node.id.toString(), argTypes);
        	MethodDeclaration mDecl = prefixDecl.getEnvironment().lookUpMethod(methodName);
        	if (mDecl == null)
        		throw new TypeCheckingException("Method invocation [Primary].[ID]() not recoginzed: " + node.expr + " " + node.id);
        	
        	if (mDecl.returnType != null) {
        		node.attachType(mDecl.returnType);
        	} else {
        		node.attachType(new Void());
        	}
        	
        	// resolve the declaration of id too
        	node.id.attachDeclaration(mDecl);
        	
        	// check protected method
        	if (mDecl.modifiers.contains(Modifier.PROTECTED)) {
        		checkInstanceProtected(prefixDecl, methodName);
        	}
        	// check that it is not a static method
        	checkNonStatic(mDecl);
        	
        } else if (node.expr instanceof Name) {
        	// Name(...)
        	Name mn = (Name) node.expr;
                // TODO: Check whether this check is right...
        	if (!checkThisInMethod(mn)) {
        	    throw new TypeCheckingException("Cannot implicitly call this expression in static method.");
        	}
        	resolveMethodName(mn, argTypes);
        	MethodDeclaration mDecl = (MethodDeclaration) mn.getDeclaration();
        	if (mDecl.returnType != null)
        		node.attachType(mDecl.returnType);
        	else {
        		node.attachType(new Void());
        	}
        } else {
        	throw new TypeCheckingException("Method invocation: " + node.expr + " " + node.id);
        }
//        System.out.println("~~~~~~~");
    }
    
    private boolean checkThisInMethod(Name name) {
        if (name instanceof SimpleName) {
            if (!checkThisInField()) {
                return false;
            }
            if (this.currentMethod != null) {
                if (this.currentMethod.modifiers.contains(Modifier.STATIC)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void visit(NullLiteral node) throws Exception {
        node.attachType(null);
    }

    @Override
    public void visit(PrefixExpression node) throws Exception {
        if (node.expr != null) {
            node.expr.accept(this);
        }
        Type expr = node.expr.getType();

        ast.PrefixExpression.Operator op = node.op;

        Type type = typeCheckPrefixExp(expr, op);
        node.attachType(type);
    }

    @Override
    public void visit(StringLiteral node) throws Exception {
        node.attachType(simpleTypeBuilder("java.lang.String"));
    }

    @Override
    public void visit(ThisExpression node) throws Exception {
        if (this.currentMethod != null) {
            if (this.currentMethod.modifiers.contains(Modifier.STATIC)) {
                throw new TypeCheckingException("Cannot call this expression in static method.");
            }
        }
        node.attachType(simpleTypeBuilder(this.currentTypeName));
    }

    @Override
    public void visit(VariableDeclarationExpression node) throws Exception {
        if (node.variableDeclaration != null) {
            node.variableDeclaration.accept(this);
        }

        Type initializerType = node.variableDeclaration.initializer.getType();
        if (TypeHelper.assignable(node.variableDeclaration.type, initializerType)) {
            node.attachType(node.variableDeclaration.type);
        } else {
            throw new TypeCheckingException(initializerType.toString()
                    + " is not assignable to "
                    + node.variableDeclaration.type.toString());
        }
    }
    
    @Override
    public void visit(SimpleType node) {
            // do nothing. Types have already been processed
    }

    @Override
    public void visit(MethodDeclaration node) throws Exception {
        this.currentMethod = node;
        last = curr;
        curr = node.getEnvironment();
        super.visit(node);
        curr = last;
        this.currentMethod = null;
    }

    @Override
    public void visit(ReturnStatement node) throws Exception {
        super.visit(node);
        Type returnType = null;
        if (node.returnExpression != null) {
            returnType = node.returnExpression.getType();
        }
        if ((this.currentMethod.returnType) == null) {
            if (returnType != null) {
                throw new TypeCheckingException("Void method cannot return a value");
            }
        } else {
            if (!TypeHelper.assignable(this.currentMethod.returnType, returnType)) {
                throw new TypeCheckingException(
                        "Type mismatch: cannot convert from "
                                + returnType.toString() + " to "
                                + this.currentMethod.returnType.toString());
            }
        }
    }

    @Override
    public void visit(VariableDeclarationStatement node) throws Exception {
        last = curr;
        curr = node.getEnvironment();
        super.visit(node);
        Type initializerType = node.varDeclar.initializer.getType();
        if (!TypeHelper.assignable(node.varDeclar.type, initializerType)) {
            throw new TypeCheckingException(initializerType + " is not assignable to " + node.varDeclar.type.toString());
        } 
        curr = last;
    }
    
    @Override
    public void visit(ForStatement node) throws Exception {
        last = curr;
        curr = node.getEnvironment();
        super.visit(node);

        Set<Value> values = new HashSet<Value>();
        values.add(Value.BOOLEAN);
        if (!CheckSinglePrimitive(node.forCondition.getType(), null, values)) {
            throw new TypeCheckingException("If condition has to be boolean");
        }
        curr = last;
    }

    @Override
    public void visit(IfStatement node) throws Exception {
        if (node.ifCondition != null) {
            node.ifCondition.accept(this);
        }
        if (node.ifStatement != null) {
            node.ifStatement.accept(this);
        }
        if (node.elseStatement != null) {
            node.elseStatement.accept(this);
        }
        Set<Value> values = new HashSet<Value>();
        values.add(Value.BOOLEAN);
        if (!CheckSinglePrimitive(node.ifCondition.getType(), null, values)) {
            throw new TypeCheckingException("If condition has to be boolean");
        }
        visitNextStatement(node);
    }

    @Override
    public void visit(WhileStatement node) throws Exception {
        if (node.whileCondition != null) {
            node.whileCondition.accept(this);
        }
        if (node.whileStatement != null) {
            node.whileStatement.accept(this);
        }
        Set<Value> values = new HashSet<Value>();
        values.add(Value.BOOLEAN);
        if (!CheckSinglePrimitive(node.whileCondition.getType(), null, values)) {
            throw new TypeCheckingException("While condition has to be boolean");
        }
        visitNextStatement(node);
    }

    @Override
    public void visit(ExpressionStatement node) throws Exception {
        if (node.statementExpression != null) {
            node.statementExpression.accept(this);
        }
        visitNextStatement(node);
    }
 
    
    @Override
	public void visit(TypeDeclaration node) throws Exception {
		currentTypeDecl = node;
		for (FieldDeclaration fd : node.getEnvironment().fields.values()) {
			unseenFields.add(fd);
		}
		super.visit(node);

		// Because there is no explicit super call in joos1W, so every super
                // class has to have a constructor without any parameters, otherwise
                // the implicit super call will fail.
		boolean hasValidConstructor = false;
		if ((node.superClass != null) && (node.getEnvironment().constructors.size() > 0)) {
		    Map<String, MethodDeclaration> superConstructors = node.superClass.getDeclaration().getEnvironment().constructors;
		    for (String s : superConstructors.keySet()) {
		        List<VariableDeclaration> parameters = superConstructors.get(s).parameters;
		        if (parameters.size() == 0) {
		            hasValidConstructor = true;
		        }
		    }
		} else {
		    //means implicit super call won't happen.
		    hasValidConstructor = true;
		}
		if (!hasValidConstructor) {
		    throw new TypeCheckingException("No constructor without parameters in super class");
		}
	}

    @Override
    public void visit(FieldDeclaration node) throws Exception {
        this.currentField = node;
        for (Modifier im : node.modifiers) {
            im.accept(this);
        }
            node.type.accept(this);
            
        if (node.initializer != null) {
            isFieldInit = true;
            node.initializer.accept(this);
            isFieldInit = false;
        }
        this.currentField = null;
        Type decType = node.type;
        if (node.initializer != null) {
            Type realType = node.initializer.getType();
            if (!TypeHelper.assignable(decType, realType)) {
                throw new TypeCheckingException(realType + " is not assignable to " + decType.toString());
            }
        }
        unseenFields.remove(node);
    }
    
    private Type typeCheckInfixExp(Type lhs, Type rhs, Operator op) throws TypeCheckingException {
        switch (op) {
        case PLUS:
            // Type checking for String concatenation.
            SimpleType type1 = checkeStringConcat(lhs, rhs);
            SimpleType type2 = checkeStringConcat(rhs, lhs);
            if (type1 != null) {
                return type1;
            } else if (type2 != null) {
                return type2;
            }
            if (checkPrimitive(lhs, rhs, false)) {
                return new PrimitiveType(Value.INT);
            } else {
                throw new TypeCheckingException("Invalid operation: + have to be used for PrimitiveType except boolean");
            }
        case AND:
        case LOR:
        case BITOR:
        case BITAND:
            if (checkPrimitive(lhs, rhs, true)) {
                return new PrimitiveType(Value.BOOLEAN);
            } else {
                throw new TypeCheckingException("Invalid comparison: & && | || have to be used for boolean");
            }
        case LANGLE:
        case RANGLE:
        case GEQ:
        case LEQ:
            if (checkPrimitive(lhs, rhs, false)) {
                return new PrimitiveType(Value.BOOLEAN);
            } else {
                throw new TypeCheckingException("Invalid comparison: < << > >> have to be used for PrimitiveType except boolean");
            }
        case NEQ:
        case EQUAL:
            if ((lhs instanceof PrimitiveType) != (rhs instanceof PrimitiveType)) {
                throw new TypeCheckingException("Invalid comparison: cannot compare primitive with non-primitive");
            }
            if (TypeHelper.assignable(lhs, rhs) || TypeHelper.assignable(rhs, lhs)) {
                return new PrimitiveType(Value.BOOLEAN);
            } else {
                throw new TypeCheckingException("Invalid comparison: = != have to be used for comparable types");
            }
        case MINUS:
        case STAR:
        case SLASH:
        case MOD:
            if (checkPrimitive(lhs, rhs, false)) {
                return new PrimitiveType(Value.INT);
            } else {
                throw new TypeCheckingException("Invalid operation: - * / % have to be used for PrimitiveType except boolean");
            }
        }
        return null;
    }
    
    /**
     * only search for variable or field name
     */
    public void visit(SimpleName name) throws TypeCheckingException {
        if (!checkThisInField()) {
            throw new TypeCheckingException("Cannot implicitly call this in static field.");
        }
        ASTNode decl = name.getDeclaration();
        if (decl instanceof FieldDeclaration) {
            FieldDeclaration fDecl = (FieldDeclaration) decl;
            if (!checkThisInMethod(name, fDecl)) {
                throw new TypeCheckingException("Cannot implicitly call this in static method.");
            }
        }
    	resolveNameType(name);
    }
    
    private boolean checkThisInField() {
        if (this.currentField != null) {
            if (this.currentField.modifiers.contains(Modifier.STATIC)) {
                return false;
            }
        }
        return true;
    }

    /**
     * only search for variable or field name
     */
    public void visit(QualifiedName name) throws TypeCheckingException {
    	checkProtected(name);
    	resolveNameType(name);
    	
    }
    
    private boolean checkThisInMethod(Name name, FieldDeclaration fDecl) {
        if (name instanceof SimpleName) {
            if (this.currentMethod != null) {
                if (this.currentMethod.modifiers.contains(Modifier.STATIC)) {
                    if ((!fDecl.modifiers.contains(Modifier.STATIC))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean checkStaticUse(Name name) {
        if (name instanceof QualifiedName) {
            QualifiedName qname = (QualifiedName) name;
            // means statically using
            if (qname.getQualifier().getDeclaration() instanceof TypeDeclaration) {
                return true;
            }
        }
        return false;
    }

    private void resolveNameType(Name name) throws TypeCheckingException {
//    	System.out.println("\t" + name + ":" + (name.getDeclaration() == null));
    	ASTNode decl = name.getDeclaration();
    	if (decl instanceof VariableDeclaration) {
    		VariableDeclaration vDecl = (VariableDeclaration) decl;
//    		System.out.println(name + ":" +vDecl.type +" " + vDecl.id );
    		name.attachType(vDecl.type);
    	} else if (decl instanceof FieldDeclaration) {
    		FieldDeclaration fDecl = (FieldDeclaration) decl;
            if (!fDecl.modifiers.contains(Modifier.STATIC)) {
                if (checkStaticUse(name)) {
                    throw new TypeCheckingException("Statically using a non-static field");
                }
            }
    		name.attachType(fDecl.type);
    	} else if (decl == null && name instanceof QualifiedName){
    		// array.length
    		QualifiedName qn = (QualifiedName) name;
    		if (qn.isArrayLength) {
    			
    		    //check that the qualifier is ArrayType.
    		    Name qualifier = qn.getQualifier();
    		    if (qualifier != null) {
    		        resolveNameType(qualifier);
    		    }
    		    if (qualifier.getType() instanceof ArrayType) {
    		        name.attachType(new PrimitiveType(Value.INT));
    		    } else {
    		        throw new TypeCheckingException("non-array type cannot call length: " + name);
    		    }
    		} else {
    			throw new TypeCheckingException("Declaration found for non-array fields.");
    		}
    	} else {
    		throw new TypeCheckingException("Field or variable name not recoginzed: " + name.toString());
    	}
    	
    }
    
    private void checkProtected(QualifiedName name) throws TypeCheckingException {
		List<Name> prefixList = name.getPrefixList();
		ASTNode previousDecl = prefixList.get(0).getDeclaration();
		TypeDeclaration previousTd = null;	// type declaration containing fDecl
		
		int i;
		ASTNode prefixDecl = null;
		for (i = 1; i < prefixList.size(); i++) {	// skip the first one
			Name prefix = prefixList.get(i);
			prefixDecl = prefix.getDeclaration();
			if (prefixDecl instanceof FieldDeclaration) {
				FieldDeclaration fDecl = (FieldDeclaration) prefixDecl;
				previousTd = getTypeDecl(previousDecl);
				if (fDecl.modifiers.contains(Modifier.PROTECTED) ) {
					// check preivous declaration
					checkProtectedField(previousTd, fDecl);
					
				}
			} 
			// if TypeDeclaration or null, just go on.
			previousDecl = prefixDecl;
		}
		// if the last part is a method
		if (prefixDecl instanceof MethodDeclaration) {
			previousTd = getTypeDecl(prefixList.get(prefixList.size() - 2).getDeclaration());
			MethodDeclaration md = (MethodDeclaration) prefixDecl;
			if (md.modifiers.contains(Modifier.PROTECTED))
				checkProtectedMethod(previousTd, md);
		}
	}
    
    private void checkProtectedField(TypeDeclaration previousTd, FieldDeclaration fDecl) throws TypeCheckingException {
		// if this type does not inherit from the type where field is declared, error
		TypeDeclaration fDeclType = (TypeDeclaration) fDecl.getParent();
		if (!TypeHelper.inheritsFrom(fDeclType, currentTypeDecl))
			throw new TypeCheckingException("Illegal access to protected field: " + fDecl.id);
		
		// in addition for instance field, if the prefix is not subclass of this class, error
		if (!fDecl.modifiers.contains(Modifier.STATIC))
			checkInstanceProtected(previousTd, fDecl.id);
    }
    
    private void checkProtectedMethod(TypeDeclaration previousTd, MethodDeclaration mDecl) throws TypeCheckingException {
		// if this type does not inherit from the type where field is declared, error
		TypeDeclaration fDeclType = (TypeDeclaration) mDecl.getParent();
		if (!TypeHelper.inheritsFrom(fDeclType, currentTypeDecl))
			throw new TypeCheckingException("Illegal access to protected field: " + mDecl.id);
		
		// in addition for instance field, if the prefix is not subclass of this class, error
		if (!mDecl.modifiers.contains(Modifier.STATIC))
			checkInstanceProtected(previousTd, mDecl.id);
    }
    
    private void checkInstanceProtected(TypeDeclaration prefixDecl, String name) throws TypeCheckingException {
		if (!(samePkg(prefixDecl, currentTypeDecl) || TypeHelper.inheritsFrom(currentTypeDecl, prefixDecl))) {
			// if not from the same package or subclass
			throw new TypeCheckingException("Illegal access to protected member: " + name);
		}
	}
    
    private TypeDeclaration getTypeDecl(ASTNode previousDecl) throws TypeCheckingException {
    	TypeDeclaration previousTd = null;
    	if (previousDecl instanceof TypeDeclaration) {
			// static access
			 previousTd = (TypeDeclaration) previousDecl;
		} else if (previousDecl instanceof FieldDeclaration || previousDecl instanceof VariableDeclaration) {
			
			Type previousType;
			if (previousDecl instanceof FieldDeclaration) {
				FieldDeclaration previousFd = (FieldDeclaration) previousDecl;
				previousType = previousFd.type;
			} else {
				VariableDeclaration previousFd = (VariableDeclaration) previousDecl;
				previousType = previousFd.type;
			}

			if (! (previousType instanceof SimpleType)) {
				throw new TypeCheckingException("unexpected type in qualified name " + previousType);
			}
			previousTd = previousType.getDeclaration();
		}
    	return previousTd;
    }
    
	private SimpleType checkeStringConcat(Type type1, Type type2)
            throws TypeCheckingException {
        if (type1 instanceof SimpleType) {
            if (type1.getDeclaration().getFullName().equals("java.lang.String")) {
                if (!(type2 instanceof Void)) {
                    return simpleTypeBuilder((SimpleType) type1);
                } else {
                    throw new TypeCheckingException("Cannot concat string with void");
                }
            }
        }
        return null;
    }

    private boolean checkPrimitive(Type type1, Type type2, boolean isBoolean) {
        if ((type1 instanceof PrimitiveType) && (type2 instanceof PrimitiveType)) {
            PrimitiveType ptype1 = (PrimitiveType) type1;
            PrimitiveType ptype2 = (PrimitiveType) type2;
            if (isBoolean) {
                if (ptype1.value.equals(Value.BOOLEAN) && ptype2.value.equals(Value.BOOLEAN)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (!ptype1.value.equals(Value.BOOLEAN) && !ptype2.value.equals(Value.BOOLEAN)) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    private Type typeCheckPrefixExp(Type expr, ast.PrefixExpression.Operator op) throws TypeCheckingException {
        Set<Value> values;
        switch (op) {
        // TODO: Check whether the type of -byte and -short is int.
        case MINUS:
            values = new HashSet<Value>();
            values.add(Value.CHAR);
            values.add(Value.BOOLEAN);
            if (CheckSinglePrimitive(expr, values, null)) {
                return new PrimitiveType(Value.INT);
            }
            break;
        case NOT:
            values = new HashSet<Value>();
            values.add(Value.BOOLEAN);
            if (CheckSinglePrimitive(expr, null, values)) {
                return new PrimitiveType(Value.BOOLEAN);
            }
            break;
        }
        throw new TypeCheckingException("Invalid prefix expression");
    }

    private boolean CheckSinglePrimitive(Type type, Set<Value> excludes, Set<Value> includes) {
        Set<Value> allTypes = new HashSet<Value>();
        allTypes.add(Value.BOOLEAN);
        allTypes.add(Value.INT);
        allTypes.add(Value.CHAR);
        allTypes.add(Value.BYTE);
        allTypes.add(Value.SHORT);
        if (excludes != null) {
            allTypes.removeAll(excludes);
        } else if (includes != null) {
            allTypes.retainAll(includes);
        }
        if (type instanceof PrimitiveType) {
            PrimitiveType ptype = (PrimitiveType) type;
            if (allTypes.contains(ptype.value)) {
                return true;
            }
        }
        return false;
    }

    private SimpleType simpleTypeBuilder(SimpleType simpleType) {
        Name name = simpleType.name;
        SimpleType type = new SimpleType(name);
        type.attachDeclaration(simpleType.getDeclaration());
        return type;
    }

    private ArrayType arrayTypeBuilder(Type type) throws TypeCheckingException {
        ArrayType arrayType = null;
        if (type instanceof SimpleType) {
            SimpleType stype = (SimpleType) type;
            arrayType = new ArrayType(stype);
        } else if (type instanceof PrimitiveType) {
            PrimitiveType ptype = (PrimitiveType) type;
            arrayType = new ArrayType(ptype);
        } else {
            throw new TypeCheckingException("ArrayBuilder error.");
        }
        return arrayType;
    }

    // Keep this for String Literal and this for now...
    private SimpleType simpleTypeBuilder(String typeName) {
        SimpleName name = new SimpleName(typeName);
        SimpleType type = new SimpleType(name);
        TypeDeclaration typeDec = global.get(typeName);
        type.attachDeclaration(typeDec);
        return type;
    }
    
	private void resolveMethodName(Name name, List<Type> paramTypes) throws Exception {
		if (name instanceof SimpleName)
			resolveMethodName((SimpleName) name, paramTypes);
		else {
			QualifiedName qn = (QualifiedName) name;
			resolveMethodName(qn, paramTypes);
			checkProtected(qn);
		}
	}
	
	private void resolveMethodName(SimpleName name, List<Type> paramTypes) throws NameException {
		MethodDeclaration mDecl = curr.lookUpMethod(NameHelper.mangle(name.toString(), paramTypes));
		if (mDecl == null)
			throw new NameException("Simple method name not recognized: " + name );
		
		name.attachDeclaration(mDecl);
	}
	
	private void resolveMethodName(QualifiedName name, List<Type> paramTypes) throws Exception {
		List<String> fn = name.getFullName();
		List<Name> prefixList = name.getPrefixList();
		ASTNode a1Decl;
		if ((a1Decl  = curr.lookUpVariable(fn.get(0))) != null || (a1Decl = curr.lookUpField(fn.get(0))) != null) {
			prefixList.get(0).attachDeclaration(a1Decl);
			
			// A1 is variable or a field, everything in the middle is an instance field
			TypeDeclaration prefixDecl;
			if (a1Decl instanceof VariableDeclaration)
				prefixDecl = ((VariableDeclaration) a1Decl).type.getDeclaration();
			else  {
				prefixDecl = ((FieldDeclaration) a1Decl).type.getDeclaration();
				if (isFieldInit && unseenFields.contains((FieldDeclaration) a1Decl)) {
					throw new TypeCheckingException("forward reference of fields in method invocation: " + prefixDecl.id);
				}
			}
			MethodDeclaration mDecl = searchMethod(name, prefixDecl, paramTypes);
			name.attachDeclaration(mDecl);
			return;
		}
		
		for (int i = 1; i < fn.size(); i++) {
			String prefix = String.join(".", fn.subList(0, i));
			TypeDeclaration prefixDecl = curr.lookUpType(prefix);
			if (prefixDecl != null) { // the prefix resolve to a type
				prefixList.get(i-1).attachDeclaration(prefixDecl);	// attach type declaration
				
				int j = i;
				while (j != fn.size() - 1) {	// everything in between is fields
					FieldDeclaration fDecl = prefixDecl.getEnvironment().lookUpField(fn.get(j));	
					if (j != i) {
						checkNonStatic(fDecl);
					}
					prefixList.get(j).attachDeclaration(fDecl);
					prefixDecl = fDecl.type.getDeclaration();
					j++;
				}
				MethodDeclaration mDecl = prefixDecl.getEnvironment().lookUpMethod(NameHelper.mangle(fn.get(j), paramTypes));
				if (i == j && !mDecl.modifiers.contains(Modifier.STATIC)) {
					// static method
					throw new TypeCheckingException("Nonstatic method accessed in a static manner: " + name);
				} 
				if (mDecl == null)
					throw new TypeCheckingException("Method unrecognized: " + name.toString() + " ");
				name.attachDeclaration(mDecl);
				return;
			}
		}
		
		throw new NameException("Qualified Method not recognized: " + name);
		
	}

	private MethodDeclaration searchMethod(QualifiedName name, TypeDeclaration prefixDecl, List<Type> paramTypes) throws NameException {
		List<String> fn = name.getFullName();
		List<Name> prefixList = name.getPrefixList();
		int i = 1;
		while (i < fn.size()-1) {
			FieldDeclaration fDecl = prefixDecl.getEnvironment().lookUpField(fn.get(i));
			if (fDecl == null) {
				throw new NameException("Method prefix not recognized: " + String.join(".", fn.subList(0, i)));
			}
			checkNonStatic(fDecl);
			prefixList.get(i).attachDeclaration(fDecl);
			prefixDecl = fDecl.type.getDeclaration();
			i++;
		}
		
		MethodDeclaration mDecl = prefixDecl.getEnvironment().lookUpMethod(NameHelper.mangle(fn.get(i), paramTypes));
		checkNonStatic(mDecl);
		return mDecl;
	}
	
	public static void typeCheck(List<AST> trees) throws Exception {
		for (AST t : trees) {
			Visitor tcv = new TypeCheckingVisitor();
			t.root.accept(tcv);
		}
	}
	
	private boolean samePkg(TypeDeclaration typeDecl1, TypeDeclaration typeDecl2) {
		String fn1 = typeDecl1.getFullName();
		String pkg1 = fn1.substring(0, fn1.length() - typeDecl1.id.length());
		String fn2 = typeDecl2.getFullName();
		String pkg2 = fn2.substring(0, fn2.length() - typeDecl2.id.length());
		return pkg1.equals(pkg2);
		
	}
	
	private void checkNonStatic(FieldDeclaration fd) throws NameException {
		if (fd.modifiers.contains(Modifier.STATIC)) {
			throw new NameException("Nonstatic access to static field. Static fields can only be accessed by type name in Joos");
		}
	}
	
	private void checkNonStatic(MethodDeclaration fd) throws NameException {
		if (fd.modifiers.contains(Modifier.STATIC)) {
			throw new NameException("Nonstatic access to static field. Static fields can only be accessed by type name in Joos");
		}
	}
}
