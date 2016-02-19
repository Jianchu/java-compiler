package environment;

import java.util.List;
import java.util.Map;

import ast.ArrayType;
import ast.PrimitiveType;
import ast.QualifiedName;
import ast.SimpleName;
import ast.SimpleType;
import ast.TypeDeclaration;
import exceptions.TypeLinkException;

/**
 * Responsible for type linking
 * @author zanel
 *
 */
public class TypeVisitor extends SemanticsVisitor {
    
    // globalPackages is the map between package name and the type names
    // declared in that package
    private final Map<String, List<String>> globalPackages;
    // global is the map between full name of a type and it's AST node
    private final Map<String, TypeDeclaration> global;
    
    public TypeVisitor() {
        this.globalPackages = SymbolTable.getAllPackages();
        this.global = SymbolTable.getGlobal();
    }

    @Override
    public void visit(SimpleName node) throws Exception {
    }

    // Fully qualified names are easy
    @Override
    public void visit(QualifiedName node) throws Exception {
        String fullName = node.toString();
        // Search qualified name in global
        if (!global.keySet().contains(fullName)) {
            throw new TypeLinkException(
                    "The full qualified type name is not found");
        }
    }

    @Override
    public void visit(ArrayType node) throws Exception {
    }

    @Override
    public void visit(PrimitiveType node) throws Exception {
    }

    @Override
    public void visit(SimpleType node) throws Exception {
    }
}
