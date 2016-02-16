package ast;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import parser.ParseTree;
import parser.Parser;
import scanner.Scanner;
import scanner.Token;
import exceptions.ASTException;

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

    public void visit(CompilationUnit node) throws ASTException {
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

    public void visit(PackageDeclaration node) throws ASTException {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        // Check null?
        node.name.accept(this);
        indent -= DISTANCE;
    }

    public void visit(ImportDeclaration node) {
        printIndent(node.getClass().getSimpleName());
    }

    public void visit(ArrayAccess node) {

    }

    public void visit(ArrayCreationExpression node) {

    }

    public void visit(ArrayType node) throws ASTException {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.type != null) {
            node.type.accept(this);
        }
        indent += DISTANCE;
    }

    public void visit(AssignmentExpression node) {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
    }

    public void visit(Block node) throws ASTException {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        for (Statement statement : node.statements) {
            statement.accept(this);
        }
    }

    public void visit(BooleanLiteral node) {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        printIndent(Boolean.toString(node.value));
        indent -= DISTANCE;
    }

    public void visit(CastExpression node) {

    }

    public void visit(CharacterLiteral node) {

    }

    public void visit(ClassInstanceCreationExpression node) {
        printIndent(node.getClass().getSimpleName());

    }

    public void visit(ExpressionStatement node) {

    }

    public void visit(FieldAccessExpression node) {

    }

    public void visit(FieldAccess node) {

    }

    public void visit(FieldDeclaration node) throws ASTException {
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

    public void visit(ForStatement node) {

    }

    public void visit(IfStatement node) {

    }

    public void visit(InfixExpression node) throws ASTException {
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

    public void visit(InstanceofExpression node) {

    }

    public void visit(IntegerLiteral node) {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        printIndent(node.value);
        indent -= DISTANCE;
    }

    public void visit(MethodDeclaration node) throws ASTException {
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

    public void visit(MethodInvocation node) {
        printIndent(node.getClass().getSimpleName());

    }

    public void visit(Modifier node) throws ASTException {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        // print string?
        printIndent(node.toString());
        indent -= DISTANCE;
    }

    public void visit(NullLiteral node) {

    }

    public void visit(PrefixExpression node) throws ASTException {
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
        printIndent(node.type.toString());
        indent -= DISTANCE;
    }

    public void visit(QualifiedName node) throws ASTException {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        String fullName = node.toString();
        printIndent(fullName);
        indent -= DISTANCE;
    }

    // qualified type class not used
    public void visit(ReturnStatement node) throws ASTException {
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

    public void visit(SimpleType node) throws ASTException {
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

    }

    public void visit(TypeDeclaration node) throws ASTException {
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

    public void visit(VariableDeclaration node) throws ASTException {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        node.type.accept(this);
        printIndent(node.id);
        if (node.initializer != null) {
            node.initializer.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(VariableDeclarationStatement node) throws ASTException {
        printIndent(node.getClass().getSimpleName());
        indent += DISTANCE;
        if (node.varDeclar != null) {
            node.varDeclar.accept(this);
        }
        indent -= DISTANCE;
    }

    public void visit(VariableDeclarationExpression node) {

    }

    public void visit(WhileStatement node) {

    }

    public static void main(String[] args) throws Exception {
        File grammar = new File(System.getProperty("user.dir")
                + "/data/grammar.lr1");
        //File f = new File(System.getProperty("user.dir")+ "/test/testprogram/StringLiterals.java");
        //File f = new File(System.getProperty("user.dir")+ "/assignment_testcases/a2/J1_1_Cast_NamedTypeAsVariable.java");
        //File f = new File(System.getProperty("user.dir")+ "/assignment_testcases/a2/J1_4_MethodDeclare_DuplicateArrayTypes.java");
        File f = new File(
                System.getProperty("user.dir")
                        + "/assignment_testcases/a1/J1_evalMethodInvocationFromParExp.java");
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
