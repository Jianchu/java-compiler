package environment;

import ast.ArrayAccess;
import ast.ArrayCreationExpression;
import ast.ArrayType;
import ast.AssignmentExpression;
import ast.Block;
import ast.BodyDeclaration;
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
import ast.Type;
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
        if (node.name != null) {
            node.name.accept(this);
        }
    }

    public void visit(MethodDeclaration node) throws Exception {
        for (Modifier mo : node.modifiers) {
            mo.accept(this);
        }
        if (!node.isConstructor) {
            // check for void
            if (node.returnType != null) {
                node.returnType.accept(this);
            }
        }
        for (VariableDeclaration va : node.parameters) {
            va.accept(this);
        }
        if (node.body != null) {
            node.body.accept(this);
        }
    }

    public void visit(PackageDeclaration node) throws Exception {
        if (node.name != null) {
            node.name.accept(this);
        }
    }

    public void visit(TypeDeclaration node) throws Exception {
        for (Modifier im : node.modifiers) {
            im.accept(this);
        }
        if (node.superClass != null) {
            node.superClass.accept(this);
        }
        for (Type ty : node.interfaces) {
            ty.accept(this);
        }
        for (BodyDeclaration im : node.members) {
            im.accept(this);
        }
    }

    public void visit(VariableDeclaration node) throws Exception {
        node.type.accept(this);
        if (node.initializer != null) {
            node.initializer.accept(this);
        }
    }

    /*
     * Statement
     */
    public void visit(Block node) throws Exception {
		if (node.statements.size() > 0) {
			Statement first = node.statements.get(0);
			first.accept(this);
		}
    }

    public void visit(ExpressionStatement node) throws Exception {
        if (node.statementExpression != null) {
            node.statementExpression.accept(this);
        }
        visitNextStatement(node);
    }

    public void visit(ForStatement node) throws Exception {
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
        visitNextStatement(node);
    }

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
        visitNextStatement(node);
    }

    public void visit(ReturnStatement node) throws Exception {
        node.returnExpression.accept(this);
        visitNextStatement(node);
    }

    public void visit(VariableDeclarationStatement node) throws Exception {
        if (node.varDeclar != null) {
            node.varDeclar.accept(this);
        }
        visitNextStatement(node);
    }

    public void visit(WhileStatement node) throws Exception {
        if (node.whileCondition != null) {
            node.whileCondition.accept(this);
        }
        if (node.whileStatement != null) {
            node.whileStatement.accept(this);
        }
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
        if (node.type != null) {
            node.type.accept(this);
        }
    }

    public void visit(PrimitiveType node) throws Exception {

    }

    public void visit(SimpleType node) throws Exception {
        node.name.accept(this);
    }

    /*
     * Expression
     */
    public void visit(ArrayAccess node) throws Exception {
        if (node.array != null) {
            node.array.accept(this);
        }
        if (node.index != null) {
            node.index.accept(this);
        }
    }

    public void visit(ArrayCreationExpression node) throws Exception {
        if (node.type != null) {
            node.type.accept(this);
        }
        if (node.expr != null) {
            node.expr.accept(this);
        }
    }

    public void visit(AssignmentExpression node) throws Exception {
        if (node.lhs != null) {
            node.lhs.accept(this);
        }
        if (node.expr != null) {
            node.expr.accept(this);
        }
    }

    public void visit(BooleanLiteral node) throws Exception {
    }

    public void visit(CastExpression node) throws Exception {
        if (node.type != null) {
            node.type.accept(this);
        }
        if (node.expr != null) {
            node.expr.accept(this);
        }
        if (node.unary != null) {
            node.unary.accept(this);
        }
    }

    public void visit(CharacterLiteral node) throws Exception {

    }

    public void visit(ClassInstanceCreationExpression node) throws Exception {
        if (node.type != null) {
            node.type.accept(this);
        }
        if (node.arglist != null) {
            for (Expression expr : node.arglist) {
                expr.accept(this);
            }
        }
    }

    public void visit(FieldAccess node) throws Exception {
        if (node.expr != null) {
            node.expr.accept(this);
        }
    }

    public void visit(InfixExpression node) throws Exception {
        if (node.lhs != null) {
            node.lhs.accept(this);
        }
        if (node.rhs != null) {
            node.rhs.accept(this);
        }
    }

    public void visit(InstanceofExpression node) throws Exception {
        if (node.expr != null) {
            node.expr.accept(this);
        }
        if (node.type != null) {
            node.type.accept(this);
        }
    }

    public void visit(IntegerLiteral node) throws Exception {
    }

    public void visit(MethodInvocation node) throws Exception {
        if (node.expr != null) {
            node.expr.accept(this);
        }
        if (node.arglist != null) {
            for (Expression expr : node.arglist) {
                expr.accept(this);
            }
        }
    }

    public void visit(NullLiteral node) throws Exception {
    }

    public void visit(PrefixExpression node) throws Exception {
        if (node.expr != null) {
            node.expr.accept(this);
        }
    }

    public void visit(StringLiteral node) throws Exception {
    }

    public void visit(ThisExpression node) throws Exception {
    }

    public void visit(VariableDeclarationExpression node) throws Exception {
        if (node.variableDeclaration != null) {
            node.variableDeclaration.accept(this);
        }
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
