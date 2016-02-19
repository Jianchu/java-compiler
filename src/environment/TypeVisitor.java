package environment;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ast.ArrayType;
import ast.ClassInstanceCreationExpression;
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
public class TypeVisitor extends TopDeclVisitor {
    
    // globalPackages is the map between package name and the type names
    // declared in that package.
    // May be useless here.
    private final Map<String, List<String>> globalPackages;
    // global is the map between full name of a type and it's AST node.
    // If global is just used for QualifiedName, change this to a local variable.
    private final Map<String, TypeDeclaration> global;
    
    public TypeVisitor(SymbolTable syms) {
        super(syms);
        this.globalPackages = SymbolTable.getAllPackages();
        this.global = SymbolTable.getGlobal();
    }

    @Override
    public void visit(SimpleName node) throws Exception {
        // How can I know the type of current scope? Using null checking for
        // now. See getCompUnitEnv.
        Environment env = getCompUnitEnv(table.curr);
        // Types are for enclosing class or interface? Key is full name?
        if (!checkSimpleName(env.types.keySet(), node.toString())
                && !checkSimpleName(env.singleImports.keySet(), node.toString())
                && !checkSimpleName(env.samePackage.keySet(), node.toString())
                && !checkSimpleName(env.importOnDemands.keySet(),node.toString())) {
            throw new TypeLinkException("The type name is not found");
        }
    }

    private boolean checkSimpleName(Set<String> fullNames, String simpleName) {
        for (String fullName : fullNames) {
            if (fullName.substring(fullName.lastIndexOf('.')).equals(simpleName)) {
                return true;
            }
        }
        return false;
    }

    private Environment getCompUnitEnv(Environment env) {
        if (env.types == null) {
            env = getCompUnitEnv(env.getEnclosing());
        }
        return env;
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
        if (node.type != null) {
            node.type.accept(this);
        }
    }

    @Override
    public void visit(SimpleType node) throws Exception {
        if (node.name != null) {
            node.name.accept(this);
        }
    }
    
    @Override
    public void visit(ClassInstanceCreationExpression node) throws Exception {
        if (node.name != null) {
            node.name.accept(this);
        }
    }
}
