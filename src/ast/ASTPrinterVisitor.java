package ast;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import parser.ParseTree;
import parser.Parser;
import scanner.Scanner;
import scanner.Token;

public class ASTPrinterVisitor implements Visitor{
    public int indent;
    public final int DISTANCE = 5;

    public ASTPrinterVisitor() {
        indent = 0;
    }

    private void printIndent(String s) {
        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }
        System.out.println(s);
    }

    public void visit(CompilationUnit node) throws Exception {
        printIndent(node.getClass().getSimpleName());

        indent += DISTANCE;
        if (node.pkg != null)
            node.pkg.accept(this);

        for (ImportDeclaration im : node.imports) {
            im.accept(this);
        }

        for (TypeDeclaration im : node.types) {
            im.accept(this);
        }

        indent -= DISTANCE;
    }

    public void visit(PackageDeclaration node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        // Check null?
        if (node.name != null) {
            node.name.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(ImportDeclaration node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.name != null) {
            node.name.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(ArrayAccess node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.array != null) {
            node.array.accept(this);
        }
        if (node.index != null) {
            node.index.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(ArrayCreationExpression node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.type != null) {
            node.type.accept(this);
        }
        if (node.expr != null) {
            node.expr.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(ArrayType node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.type != null) {
            node.type.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(AssignmentExpression node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.lhs != null) {
            node.lhs.accept(this);
        }

        if (node.expr != null) {
            node.expr.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(Block node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        for (Statement statement : node.statements) {
            statement.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(BooleanLiteral node) {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        printIndent(Boolean.toString(node.value));
        indent -= DISTANCE;
    }

    public void visit(CastExpression node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.type != null) {
            node.type.accept(this);
        }
        if (node.expr != null) {
            node.expr.accept(this);
        }
        if (node.unary != null) {
            node.unary.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(CharacterLiteral node) {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        printIndent(node.value);
        indent -= DISTANCE;
    }

    public void visit(ClassInstanceCreationExpression node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.type != null) {
            node.type.accept(this);
        }

        if (node.arglist != null) {
            for (Expression expr : node.arglist) {
                expr.accept(this);
            }
        }
        indent -= DISTANCE;
    }

    public void visit(ExpressionStatement node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.statementExpression != null) {
            node.statementExpression.accept(this);
        }
        indent -= DISTANCE;
    }

    // TODO
//    public void visit(FieldAccessExpression node) {
//        printIndent(node.getClass().getSimpleName());
//        indent += DISTANCE;
//        indent -= DISTANCE;
//    }

    public void visit(FieldAccess node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.expr != null) {
            node.expr.accept(this);
        }
        if (node.id != null) {
            printIndent(node.id.toString());
        }
        indent -= DISTANCE;
    }

    public void visit(FieldDeclaration node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        for (Modifier im : node.modifiers) {
            im.accept(this);
        }
        node.type.accept(this);
        printIndent(node.id);
        if (node.initializer != null) {
            node.initializer.accept(this);
        }
        indent -= DISTANCE;

    }

    public void visit(ForStatement node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.forInit != null) {
            node.forInit.accept(this);
        }
        if (node.forCondition != null) {
            node.forCondition.accept(this);
        }
        if (node.forUpdate != null) {
            node.forUpdate.accept(this);
        }
        if (node.forBody != null) {
            node.forBody.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(IfStatement node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.ifCondition != null) {
            node.ifCondition.accept(this);
        }
        if (node.ifStatement != null) {
            node.ifStatement.accept(this);
        }
        if (node.elseStatement != null) {
            node.elseStatement.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(InfixExpression node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.lhs != null) {
            node.lhs.accept(this);
        }
        if (node.op != null) {
            printIndent(node.op.toString());
        }
        if (node.rhs != null) {
            node.rhs.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(InstanceofExpression node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.expr != null) {
            node.expr.accept(this);
        }
        if (node.type != null) {
            node.type.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(IntegerLiteral node) {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        printIndent(node.value);
        indent -= DISTANCE;
    }

    public void visit(MethodDeclaration node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;

        for (Modifier mo : node.modifiers) {
            mo.accept(this);
        }

        if (!node.isConstructor) {
            // check for void
            if (node.returnType != null) {
                node.returnType.accept(this);
            }
        }

        printIndent(node.id);
        for (VariableDeclaration va : node.parameters) {
            va.accept(this);
        }
        if (node.body != null) {
            node.body.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(MethodInvocation node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.expr != null) {
            node.expr.accept(this);
        }
        if (node.id != null) {
            printIndent(node.id.toString());
        }
        if (node.arglist != null) {
            for (Expression expr : node.arglist) {
                expr.accept(this);
            }
        }
        indent -= DISTANCE;
    }

    public void visit(Modifier node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        printIndent(node.toString());
        indent -= DISTANCE;
    }

    public void visit(NullLiteral node) {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        // Should we print this?
        printIndent("null");
        indent -= DISTANCE;
    }

    public void visit(PrefixExpression node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.op != null) {
            printIndent(node.op.toString());
        }
        if (node.expr != null) {
            node.expr.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(PrimitiveType node) {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        printIndent(node.value.toString());
        indent -= DISTANCE;
    }

    public void visit(QualifiedName node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        String fullName = node.toString();
        printIndent(fullName);
        indent -= DISTANCE;
    }

    // qualified type class not used
    public void visit(ReturnStatement node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        node.returnExpression.accept(this);
        indent -= DISTANCE;
    }

    public void visit(SimpleName node) {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        printIndent(node.toString());
        indent -= DISTANCE;
    }

    public void visit(SimpleType node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        node.name.accept(this);
        indent -= DISTANCE;
    }

    public void visit(StringLiteral node) {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        printIndent(node.value);
        indent -= DISTANCE;

    }

    public void visit(ThisExpression node) {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        // Should we print this?
        printIndent("this");
        indent -= DISTANCE;
    }

    public void visit(TypeDeclaration node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        for (Modifier im : node.modifiers) {
            im.accept(this);
        }
        printIndent(node.id);
        if (node.superClass != null) {
            node.superClass.accept(this);
        }
        for (Type ty : node.interfaces) {
            ty.accept(this);
        }
            
        for (BodyDeclaration im : node.members) {
            im.accept(this);
        }

        indent -= DISTANCE;

    }

    public void visit(VariableDeclaration node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        node.type.accept(this);
        printIndent(node.id);
        if (node.initializer != null) {
            node.initializer.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(VariableDeclarationStatement node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.varDeclar != null) {
            node.varDeclar.accept(this);
        }
        indent -= DISTANCE;
    }

    // TODO
    public void visit(VariableDeclarationExpression node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.variableDeclaration != null) {
            node.variableDeclaration.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(WhileStatement node) throws Exception {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.whileCondition != null) {
            node.whileCondition.accept(this);
        }
        if (node.whileStatement != null) {
            node.whileStatement.accept(this);
        }
        indent -= DISTANCE;
    }

    public static void main(String[] args) throws Exception {
        File grammar = new File(System.getProperty("user.dir")
                + "/data/grammar.lr1");
        //File f = new File(System.getProperty("user.dir")+ "/test/testprogram/StringLiterals.java");
        //File f = new File(System.getProperty("user.dir")+ "/assignment_testcases/a2/J1_1_Cast_NamedTypeAsVariable.java");
        //File f = new File(System.getProperty("user.dir")+ "/assignment_testcases/a2/J1_4_MethodDeclare_DuplicateArrayTypes.java");
        //File f = new File(System.getProperty("user.dir")+ "/assignment_testcases/a1/J1_evalMethodInvocationFromParExp.java");
//        File f = new File(System.getProperty("user.dir")
//                + "/assignment_testcases/a1/J1_arbitrarylocaldeclaration.java");
        File f = new File(System.getProperty("user.dir")+ "/test/testprogram/CastExpr.java");
        
        
        Scanner scanner = new Scanner(new FileReader(f));
        List<Token> tokens = scanner.scan();
        Parser par = new Parser(tokens, grammar);
        ParseTree t = par.parse();
        // Weeder wee = new Weeder(t, "StringLiterals");
        // wee.weed();
        AST ast = new AST(t);
        Visitor v = new ASTPrinterVisitor();
        ast.root.accept(v);
        // t.pprint();
    }
    
    public static void print(String path) throws Exception {
        File grammar = new File(System.getProperty("user.dir")
                + "/data/grammar.lr1");
        File f = new File(path);
        
        
        Scanner scanner = new Scanner(new FileReader(f));
        List<Token> tokens = scanner.scan();
        Parser par = new Parser(tokens, grammar);
        ParseTree t = par.parse();
        // Weeder wee = new Weeder(t, "StringLiterals");
        // wee.weed();
        AST ast = new AST(t);
        Visitor v = new ASTPrinterVisitor();
        ast.root.accept(v);
        // t.pprint();
    }
}
