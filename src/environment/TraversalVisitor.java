package environment;

import ast.ArrayAccess;
import ast.ArrayCreationExpression;
import ast.ArrayType;
import ast.AssignmentExpression;
import ast.Block;
import ast.BooleanLiteral;
import ast.CastExpression;
import ast.CharacterLiteral;
import ast.ClassInstanceCreationExpression;
import ast.CompilationUnit;
import ast.ExpressionStatement;
import ast.FieldAccess;
import ast.FieldDeclaration;
import ast.ForStatement;
import ast.IfStatement;
import ast.ImportDeclaration;
import ast.InfixExpression;
import ast.InstanceofExpression;
import ast.IntegerLiteral;
import ast.MethodDeclaration;
import ast.MethodInvocation;
import ast.Modifier;
import ast.NullLiteral;
import ast.PackageDeclaration;
import ast.PrefixExpression;
import ast.PrimitiveType;
import ast.QualifiedName;
import ast.ReturnStatement;
import ast.SimpleName;
import ast.SimpleType;
import ast.Statement;
import ast.StringLiteral;
import ast.ThisExpression;
import ast.TypeDeclaration;
import ast.VariableDeclaration;
import ast.VariableDeclarationExpression;
import ast.VariableDeclarationStatement;
import ast.Visitor;
import ast.WhileStatement;

/**
 * can be extended to easily traverse AST
 * @author zanel
 *
 */
public class TraversalVisitor implements Visitor{
    public void visit(CompilationUnit node) throws Exception {
        for (TypeDeclaration typeDecl : node.types) {
            typeDecl.accept(this);
        }
    }

    /*
     * Declaration
     */
    public void visit(FieldDeclaration node) throws Exception {
        for (Modifier im : node.modifiers) {
            im.accept(this);
        }
        node.type.accept(this);
        if (node.initializer != null) {
            node.initializer.accept(this);
        }

    }

    public void visit(ImportDeclaration node) throws Exception {
    }

    public void visit(MethodDeclaration node) throws Exception {
    }

    public void visit(PackageDeclaration node) throws Exception {
    }

    public void visit(TypeDeclaration node) throws Exception {
    }

    public void visit(VariableDeclaration node) throws Exception {
    }

    /*
     * Statement
     */
    public void visit(Block node) throws Exception {
    }

    public void visit(ExpressionStatement node) throws Exception {
        visitNextStatement(node);
    }

    public void visit(ForStatement node) throws Exception {
        visitNextStatement(node);
    }

    public void visit(IfStatement node) throws Exception {
        visitNextStatement(node);
    }

    public void visit(ReturnStatement node) throws Exception {
        visitNextStatement(node);
    }

    public void visit(VariableDeclarationStatement node) throws Exception {
        visitNextStatement(node);
    }

    public void visit(WhileStatement node) throws Exception {
        visitNextStatement(node);
    }

    private void visitNextStatement(Statement node) throws Exception {
        if (node.hasNext()) {
            node.next().accept(this);
        }
    }

    /*
     * Type
     */
    public void visit(ArrayType node) throws Exception {
    }

    public void visit(PrimitiveType node) throws Exception {
    }

    public void visit(SimpleType node) throws Exception {
    }

    /*
     * Expression
     */
    public void visit(ArrayAccess node) throws Exception {
    }

    public void visit(ArrayCreationExpression node) throws Exception {
    }

    public void visit(AssignmentExpression node) throws Exception {
    }

    public void visit(BooleanLiteral node) throws Exception {
    }

    public void visit(CastExpression node) throws Exception {
    }

    public void visit(CharacterLiteral node) throws Exception {
    }

    public void visit(ClassInstanceCreationExpression node) throws Exception {
    }

    public void visit(FieldAccess node) throws Exception {
    }

    public void visit(InfixExpression node) throws Exception {
    }

    public void visit(InstanceofExpression node) throws Exception {
    }

    public void visit(IntegerLiteral node) throws Exception {
    }

    public void visit(MethodInvocation node) throws Exception {
    }

    public void visit(NullLiteral node) throws Exception {
    }

    public void visit(PrefixExpression node) throws Exception {
    }

    public void visit(StringLiteral node) throws Exception {
    }

    public void visit(ThisExpression node) throws Exception {
    }

    public void visit(VariableDeclarationExpression node) throws Exception {
    }

    /*
     * Name
     */
    public void visit(SimpleName node) throws Exception {
    }

    public void visit(QualifiedName node) throws Exception {
    }

    public void visit(Modifier node) throws Exception {
    }
}
