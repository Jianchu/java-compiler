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
import ast.FieldAccess;
import ast.FieldDeclaration;
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
import ast.Visitor;
import exceptions.NameException;
import exceptions.TypeCheckingException;

public class TypeCheckingVisitor extends EnvTraversalVisitor {
    private final Map<String, TypeDeclaration> global = SymbolTable.getGlobal();
    private final TypeHelper helper = new TypeHelper();
    private String currentTypeName;
    private MethodDeclaration currentMethod;
    // maybe need to add or delete some methods...

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

        try {
            Map<String, MethodDeclaration> constructors = node.type.getDeclaration().getEnvironment().constructors;
            for (String s : constructors.keySet()) {
                List<VariableDeclaration> DecParameters = constructors.get(s).parameters;
                if (DecParameters.size() == realParameters.size()) {
                    if (realParameters.size() == 0) {
                        node.attachType(instanceType);
                    } else {
                        boolean matches = checkParameters(realParameters, DecParameters);
                        if (matches) {
                            node.attachType(instanceType);
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new TypeCheckingException("Type environment not found");
        }
    }

    private boolean checkParameters(List<Expression> realParameters, List<VariableDeclaration> DecParameters) {
        int paraSize = realParameters.size();
        boolean matches = true;
        for (int i = 0; i < paraSize; i++) {
            Type realType = realParameters.get(i).getType();
            Type decType = DecParameters.get(i).type;
            if (!realType.equals(decType)) {
                matches = false;
            }
        }
        return matches;
    }

    /**
     * TODO: 
     * node.expr is the qualifier
     * node.id is field's name
     * the type of A.B.C.f is type of C or C.
     */
    @Override
    public void visit(FieldAccess node) throws Exception {
        if (node.expr != null) {
            node.expr.accept(this);
        }
        TypeDeclaration prefixDecl = node.expr.getType().getDeclaration();
        FieldDeclaration fDecl = prefixDecl.getEnvironment().lookUpField(node.id.toString());
        node.id.attachDeclaration(fDecl);
        node.attachType(fDecl.type);
        
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
        Type rhsType = node.lhs.getType();

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

        if (TypeHelper.assignable(exprType, node.type)
                || TypeHelper.assignable(node.type, exprType)) {
            node.attachType(new PrimitiveType(Value.BOOLEAN));
        } else {
            throw new TypeCheckingException("Uncomparable types in instanceof");
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
        	
        } else if (node.expr instanceof Name) {
        	// Name(...)
        	Name mn = (Name) node.expr;
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
    	resolveNameType(name);
    }
    
    /**
     * only search for variable or field name
     */
    public void visit(QualifiedName name) throws TypeCheckingException {
    	resolveNameType(name);
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
			resolveMethodName((QualifiedName) name, paramTypes);
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
		ASTNode a1Decl;
		if ((a1Decl  = curr.lookUpVariable(fn.get(0))) != null || (a1Decl = curr.lookUpField(fn.get(0))) != null) {
			// A1 is variable or a field, everything in the middle is an instance field
			TypeDeclaration prefixDecl;
			if (a1Decl instanceof VariableDeclaration)
				prefixDecl = ((VariableDeclaration) a1Decl).type.getDeclaration();
			else 
				prefixDecl = ((FieldDeclaration) a1Decl).type.getDeclaration();
			
			MethodDeclaration mDecl = searchMethod(name, prefixDecl, paramTypes);
			name.attachDeclaration(mDecl);
			return;
		}
		
		for (int i = 1; i < fn.size(); i++) {
			String prefix = String.join(".", fn.subList(0, i));
			TypeDeclaration prefixDecl = curr.lookUpType(prefix);
			if (prefixDecl != null) { // the prefix resolve to a type
				int j = i;
				while (j != fn.size() - 1) {	// everything in between is fields
					FieldDeclaration fDecl = prefixDecl.getEnvironment().lookUpField(fn.get(j++));	// increment j here
					prefixDecl = fDecl.type.getDeclaration();
				}
				MethodDeclaration mDecl = prefixDecl.getEnvironment().lookUpMethod(NameHelper.mangle(fn.get(i), paramTypes));
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
		
		
	}

	private MethodDeclaration searchMethod(QualifiedName name, TypeDeclaration prefixDecl, List<Type> paramTypes) throws NameException {
		List<String> fn = name.getFullName();
		int i = 1;
		while (i < fn.size()-1) {
			FieldDeclaration fDecl = prefixDecl.getEnvironment().lookUpField(fn.get(i));
			if (fDecl == null) {
				throw new NameException("Method prefix not recognized: " + String.join(".", fn.subList(0, i)));
			}
			prefixDecl = fDecl.type.getDeclaration();
			i++;
		}
		
		MethodDeclaration mDecl = prefixDecl.getEnvironment().lookUpMethod(NameHelper.mangle(fn.get(i), paramTypes));
		return mDecl;
	}
	
	public static void typeCheck(List<AST> trees) throws Exception {
		for (AST t : trees) {
			Visitor tcv = new TypeCheckingVisitor();
			t.root.accept(tcv);
		}
	}
}
