package ast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.ASTException;

/**
 * Either a class declaration or interface.
 * @author zanel
 *
 */
public class TypeDeclaration extends BodyDeclaration{
    // interface or class
    public boolean isInterface = false;

    public List<Modifier> modifiers = new LinkedList<Modifier>();
    public String id = null;

    // extends
    public Type superClass = null;
    // implements
    public List<Type> interfaces = new LinkedList<Type>();
    // field or method declarations, but no type delcarations
    public List<BodyDeclaration> members = new LinkedList<BodyDeclaration>();

    public TypeDeclaration next = null;

    private String fullName = null;

    private List<String> fOffSet = new ArrayList<String>();
    private List<String> mOffSet = new ArrayList<String>();

    public TypeDeclaration(ParseTree pt) throws ASTException {
        for (ParseTree child : pt.getChildren()) {
            switch (child.getTokenType()) {
            case TypeDeclarations:
                next = new TypeDeclaration(child);
                break;
            case TypeDeclaration:
                parseSingleType(child);
                break;
            default:
                throw new ASTException("Unexpected node type.");
            }
        }
        for (BodyDeclaration mem : members) {
            mem.setParent(this);
        }
    }

    public boolean hasNext() {
        return next != null;
    }

    public TypeDeclaration next() {
        return next;
    }

    private void parseSingleType(ParseTree pt) throws ASTException {
        ParseTree child = pt.getChildren().get(0);
        switch (child.getTokenType()) {
        case InterfaceDeclaration:
            isInterface = true;
            // intentional fall through
        case ClassDeclaration:
            parseClassDeclaration(child);
            break;
        default:
            throw new ASTException("Unexpected node type."
                    + child.getTokenType());
        }
    }

    /**
     * works for both Class and Interface
     * 
     * @param pt
     * @throws ASTException
     */
    private void parseClassDeclaration(ParseTree pt) throws ASTException {
        for (ParseTree child : pt.getChildren()) {
            switch (child.getTokenType()) {
            case Modifiers:
                // parse modifiers
                Modifier nextMod = new Modifier(child);
                modifiers.add(nextMod);
                while (nextMod.hasNext()) {
                    nextMod = nextMod.next();
                    modifiers.add(nextMod);
                }
                break;
            case ID:
                // parse name
                id = ASTHelper.parseID(child);
                break;

            /*
             * class specific
             */
            case Super:
                // parse extends
                superClass = Type.parseType(child.findChild(Symbol.ClassType));
                break;
            case Interfaces:
                // problem
                interfaces.addAll(Type.parseInterfaceTypeList(child
                        .findChild(Symbol.InterfaceTypeList)));
                break;
            case ClassBody:
                // TODO: parse class body
                parseClassBody(child);
                break;

            /*
             * Interface specific
             */
            case ExtendsInterfaces:
                interfaces.addAll(Type.parseInterfaceTypeList(child
                        .findChild(Symbol.InterfaceTypeList)));
                break;
            case InterfaceBody:
                parseInterfaceBody(child);
                break;
            }
        }
    }

    private void parseInterfaceBody(ParseTree pt) throws ASTException {
        ParseTree declarations = pt
                .findChild(Symbol.InterfaceMemberDeclarations);
        if (declarations != null) {
            parseInterfaceMemberDeclarations(declarations);
        }
    }

    private void parseInterfaceMemberDeclarations(ParseTree pt)
            throws ASTException {
        for (ParseTree child : pt.getChildren()) {
            switch (child.getTokenType()) {
            case InterfaceMemberDeclarations:
                parseInterfaceMemberDeclarations(child);
                break;
            case InterfaceMemberDeclaration:
                // no interface constants
                ParseTree amDecl = child
                        .findChild(Symbol.AbstractMethodDeclaration);
                if (amDecl != null)
                    members.add(new MethodDeclaration(amDecl));
                break;
            }
        }
    }

    private void parseClassBody(ParseTree pt) throws ASTException {
        ParseTree declarations = pt.findChild(Symbol.ClassBodyDeclarations);
        if (declarations != null) {
            parseClassBodyDeclarations(declarations);
        }
    }

    private void parseClassBodyDeclarations(ParseTree pt) throws ASTException {
        for (ParseTree child : pt.getChildren()) {
            switch (child.getTokenType()) {
            case ClassBodyDeclarations:
                parseClassBodyDeclarations(child);
                break;
            case ClassBodyDeclaration:
                ParseTree member = child.getFirstChild();
                if (member.getTokenType() == Symbol.ClassMemberDeclaration) {
                    ParseTree fieldOrMethod = member.getFirstChild();
                    switch (fieldOrMethod.getTokenType()) {
                    case FieldDeclaration:
                        members.add(new FieldDeclaration(fieldOrMethod));
                        break;
                    case MethodDeclaration:
                        members.add(new MethodDeclaration(fieldOrMethod));
                        break;
                    default:
                        break;
                    }
                } else if (member.getTokenType() == Symbol.ConstructorDeclaration) {
                    members.add(new MethodDeclaration(member));
                }
                break;
            }
        }
    }

    public void setFullName(String name) {
        fullName = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void accept(Visitor v) throws Exception {
        v.visit(this);
    }

    public void addFieldOffSet(String fn) {
        fOffSet.add(fn);
    }

    public void addMethodOffSet(String mn) {
        mOffSet.add(mn);
    }

    public void cloneFieldOffSet(List<String> fo) {
        fOffSet = new ArrayList<String>(fo);
    }

    public void cloneMethodOffSet(List<String> mo) {
        mOffSet = new ArrayList<String>(mo);
    }

    public int getFieldOffSet(String fd) throws Exception {
        int offset = fOffSet.indexOf(fd);
        if (offset < 0) {
            throw new Exception("no offset information for the field: " + fd);
        }
        return offset;
    }

    public int getMethodOffSet(String md) throws Exception {
        int offset = mOffSet.indexOf(md);
        if (offset < 0) {
            throw new Exception("no offset information for the method: " + md);
        }
        return offset;
    }

    public List<String> getFieldOffSetList() {
        return fOffSet;
    }

    public List<String> getMethodOffSetList() {
        return mOffSet;
    }
}
