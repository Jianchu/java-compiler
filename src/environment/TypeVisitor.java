package environment;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private TypeDeclaration typeDec = null;
    
    public TypeVisitor(SymbolTable syms) {
        super(syms);
        this.globalPackages = SymbolTable.getAllPackages();
        this.global = SymbolTable.getGlobal();
    }
    
    /**
     * TODO: 1. incorporate both visit(SimpleName) and visit(QualifiedName) into
     * this method. 2. and then attach the declaration to the type, using
     * type.attachDeclaration();
     * 
     * @throws Exception
     */
    public void visit(SimpleType type) throws Exception {
        if (type.name != null) {
            type.name.accept(this);
        }
        type.attachDeclaration(this.typeDec);
    }
    
    /**
     * Name must not be ambiguous. 
     * One thing I can think of now is that when using import on demands, 
     * check that the type name does not appear in two packages, 
     * e.g. java.util.* and java.awt.* both has a class named List.
     * This is not an error when the imports are declared, but the name List is ambiguous when used.
     * Could you read up add some checks of this sort?
     * 
     */
    @Override
    public void visit(SimpleName node) throws Exception {
        // How can I know the type of current scope? Using null checking for
        // now. See getCompUnitEnv.
        Environment env = getCompUnitEnv(table.curr);
        // Types are for enclosing class or interface? Key is full name?
        if (!checkSimpleName(env.types, node.toString())
                && !checkSimpleName(env.singleImports, node.toString())
                && !checkSimpleName(env.samePackage, node.toString())
                && !checkSimpleName(env.importOnDemands,node.toString())) {
            throw new TypeLinkException("The type name is not found");
        }
    }

    /**
     * @param fullNames
     * @param simpleName
     * @return
     */
    private boolean checkSimpleName(Map<String, TypeDeclaration> map, String simpleName) {
        Set<String> fullNames = map.keySet();
        for (String fullName : fullNames) {
            if (fullName.substring(fullName.lastIndexOf('.') + 1).equals(simpleName)) {
                this.typeDec = map.get(fullName);
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
}
