package weeder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.WeedException;

public class Weeder {

    final private ParseTree parseTree;
    private String className;
    private String fileName;
    private boolean isAbstractClass = false;

    public Weeder(ParseTree parseTree, String fileName) throws Exception {
        this.parseTree = parseTree;
        this.fileName = fileName;
    }


    public void weed() throws WeedException {
        ParseTree ClassDecNode = findNode(parseTree, Symbol.ClassDeclaration);
        boolean noModifier = true;
        if (ClassDecNode != null) {
            for (ParseTree child: ClassDecNode.getChildren()) {
                if (checkNodeType(child, Symbol.Modifiers)) {
                    noModifier = false;
                    visitModifier(child, Symbol.ClassDeclaration);
                } else if (checkNodeType(child, Symbol.ClassBody)) {
                    visitClassBody(child);
                } else if (checkNodeType(child, Symbol.ID)) {
                    this.className = child.getLexeme();
                    if (!this.className.equals(this.fileName)) {
                        throw new WeedException(
                                "Class's name has to be same as file's name");
                    }
                }
            }
        } else {
            ParseTree InterfaceDecNode = findNode(parseTree,Symbol.InterfaceDeclaration);
            for (ParseTree child : InterfaceDecNode.getChildren()) {
                if (checkNodeType(child, Symbol.Modifiers)) {
                    noModifier = false;
                } else if (checkNodeType(child, Symbol.ID)) {
                    this.className = child.getLexeme();
                    if (!this.className.equals(this.fileName)) {
                        throw new WeedException(
                                "Interface's name has to be same as file's name");
                    }
                }
            }
            if (InterfaceDecNode != null) {
                ParseTree methodHeaderNode = findNode(InterfaceDecNode,
                        Symbol.MethodHeader);
                if (methodHeaderNode != null) {
                    ParseTree modifierNode = findNode(methodHeaderNode,Symbol.Modifiers);
                    if (modifierNode != null) {
                        visitModifier(modifierNode, Symbol.InterfaceDeclaration);
                    }
                }
            }
        }
        // Check: No package private classes.
        if (noModifier) {
            throw new WeedException("No package private classes");
        }
    }
    
    private void visitClassBody(ParseTree classBodyNode) throws WeedException {
        ParseTree ClassBodyDecNode = findNode(classBodyNode, Symbol.ClassBodyDeclarations);
        ParseTree modifierNode;
        ParseTree blockNode;
        List<ParseTree> constructorDecs = new ArrayList<ParseTree>();
        if (ClassBodyDecNode != null) {
            Queue<ParseTree> queue = new LinkedList<ParseTree>();
            queue.add(classBodyNode);
            while (!queue.isEmpty()) {
                ParseTree currentNode = (ParseTree) queue.remove();
                for (ParseTree child : currentNode.getChildren()) {
                    if (checkNodeType(child, Symbol.FieldDeclaration)) {
                        modifierNode = findNode(child, Symbol.Modifiers);
                        if (modifierNode != null) {

                            visitModifier(modifierNode, Symbol.FieldDeclaration);
                            // Check: No package private field.
                        } else if (modifierNode == null) {
                            throw new WeedException("No package private field");
                        }
                    } else if (checkNodeType(child, Symbol.MethodDeclaration)) {
                        modifierNode = findNode(child, Symbol.Modifiers);
                        if (modifierNode != null) {

                            visitModifier(modifierNode, Symbol.MethodHeader);
                            // Check: No package private method.
                        } else {
                            throw new WeedException("No package private method");
                        }
                        blockNode = findNode(child, Symbol.Block);
                        if (blockNode != null) {

                            visitModifier(modifierNode, Symbol.MethodDeclaration);
//                            ParseTree castNode = findNode(blockNode, Symbol.CastExpression);
//                            if (castNode != null) {
//                                visitCast(castNode);
//                            }
                            visitBlock(blockNode);
                        } else {
                            
                            visitModifier(modifierNode, Symbol.Block);
                        }
                    } else if (checkNodeType(child, Symbol.ConstructorDeclaration)) {
                        constructorDecs.add(child);
                    }
                    if (!checkNodeType(child, Symbol.FieldDeclaration)
                            && !checkNodeType(child, Symbol.MethodDeclaration)
                            && !checkNodeType(child, Symbol.ConstructorDeclaration)) {
                        queue.add(child);
                    }
                }
            }
        }
        // Check: Every class must contain at least one explicit constructor.
        if (constructorDecs.isEmpty() || ClassBodyDecNode == null) {
            throw new WeedException("Every class must contain at least one explicit constructor.");
        }
        visitConstructorDec(constructorDecs);
    }

    private void visitBlock(ParseTree blockNode) throws WeedException {
        Queue<ParseTree> queue = new LinkedList<ParseTree>();
        queue.add(blockNode);
        while (!queue.isEmpty()) {
            ParseTree currentNode = (ParseTree) queue.remove();
            for (ParseTree child : currentNode.getChildren()) {
                if (checkNodeType(child, Symbol.UnaryExpression)) {
                    visitUnary(child, false);
                } else {
                    queue.add(child);
                }
            }
        }
    }

    private void visitUnary(ParseTree unaryNode, boolean minusSibling) throws WeedException {
        boolean isNegative = false;
        for (ParseTree child : unaryNode.getChildren()) {
            if (checkNodeType(child, Symbol.MINUS)) {
                isNegative = true;
            }
        }
        Queue<ParseTree> queue = new LinkedList<ParseTree>();
        queue.add(unaryNode);
        while (!queue.isEmpty()) {
            ParseTree currentNode = (ParseTree) queue.remove();
            for (ParseTree child : currentNode.getChildren()) {
                if (checkNodeType(child, Symbol.UnaryExpression)) {
                    if (isNegative) {
                        visitUnary(child, true);
                    } else {
                        visitUnary(child, false);
                    }
                    
                } else if (checkNodeType(child, Symbol.DECIMAL)) {
                    String value = child.getLexeme();
                    if (minusSibling) {
                        value = "-" + value;
                    }
                    try {
                        Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw e;
                    }
                } else if (checkNodeType(child, Symbol.CastExpression)) {
                    visitCast(child);
                } else {
                    queue.add(child);
                }
            }
        }
    }

    private void visitCast(ParseTree castNode) throws WeedException {
        Stack<ParseTree> stack = new Stack<ParseTree>();
        ParseTree castTypeNode = null;
        for (ParseTree child : castNode.getChildren()) {
            if (checkNodeType(child, Symbol.PrimitiveType)
                    || checkNodeType(child, Symbol.Name)
                    || checkNodeType(child, Symbol.Expression)) {
                castTypeNode = child;
            }
        }
        if (castTypeNode != null) {
            stack.push(castTypeNode);
        }
        while (!stack.isEmpty()) {
            ParseTree currentNode = (ParseTree) stack.pop();
            for (ParseTree child : currentNode.getChildren()) {
                    // Check: Method invocation not allowed as type in cast.
                if (checkNodeType(child, Symbol.MethodInvocation)) {
                    throw new WeedException(
                                "Method invocation not allowed as type in cast.");
                    // Check: Cast to a nonstatic field is not allowed.
                } else if (checkNodeType(child, Symbol.NEW)) {
                    throw new WeedException(
                                "Cast to a nonstatic field is not allowed.");
                    // Check: Cast to array value is not allowed.
                } else if (checkNodeType(child, Symbol.ArrayAccess)) {
                    throw new WeedException(
                            "Cast to array value is not allowed.");
                    // Check: Cast to an expression is not allowed
                } else if (checkNodeType(child, Symbol.Primary)) {
                    throw new WeedException("Cast to an expression is not allowed");
                }
                stack.push(child);
            }
        }
    }

    private void visitConstructorDec(List<ParseTree> constructorDecs) throws WeedException {
        for (ParseTree constructorDec : constructorDecs) {
            // Check: Constructor's name has to be same as class's name.
             if (!findNode(constructorDec, Symbol.ID).getLexeme().equals(className)) {
                 throw new WeedException("Constructor's name has to be same as class's name.");
             }
        }
    }

    private void visitModifier(ParseTree modifierNode, Symbol parent)
            throws WeedException {

        Stack<ParseTree> stack = new Stack<ParseTree>();
        Set<Symbol> modifiersSet = new HashSet<Symbol>();
        stack.push(modifierNode);
        while (!stack.isEmpty()) {
            ParseTree currentNode = (ParseTree) stack.pop();
            // System.out.println(currentNode);
            for (ParseTree child : currentNode.getChildren()) {
                Symbol symbol = child.getTokenType();
                // Check: Duplicated modifer.
                if (modifiersSet.contains(symbol)) {
                    // for (Symbol s : modifiersSet) {
                    // System.err.println(s);
                    // }
                    throw new WeedException("Duplicate modifer: " + symbol);
                }
                if (!symbol.equals(Symbol.Modifiers) && !symbol.equals(Symbol.Modifier)) {
                    modifiersSet.add(symbol);
                }
                // System.out.println(child);
                stack.push(child);
            }
        }

        if (parent.equals(Symbol.MethodHeader)) {
            // Check: An abstract method cannot be static or final.
            if (modifiersSet.contains(Symbol.ABSTRACT)
                    && ((modifiersSet.contains(Symbol.STATIC)) || modifiersSet
                            .contains(Symbol.FINAL))) {
                throw new WeedException("An abstract method cannot be static or final.");
                // Check: A native method must be static.
            } else if (modifiersSet.contains(Symbol.NATIVE)
                    && !modifiersSet.contains(Symbol.STATIC)) {
                throw new WeedException("A native method must be static.");
                // Check: A static method cannot be final.
            } else if (modifiersSet.contains(Symbol.STATIC) && modifiersSet.contains(Symbol.FINAL)) {
                throw new WeedException("A static method cannot be final.");
                // Check: Class contains abstract Method must be abstract.
            } else if (modifiersSet.contains(Symbol.ABSTRACT) && !this.isAbstractClass) {
                throw new WeedException("class contains abstract Method must be abstract");
                // Check: No package private method.
            } else if (!modifiersSet.contains(Symbol.PUBLIC)
                    && !modifiersSet.contains(Symbol.PROTECTED)) {
                throw new WeedException("No package private method");
            }
        } else if (parent.equals(Symbol.MethodDeclaration)) {
         // Check: A method has a body if and only if it is neither abstract nor native.
            if (modifiersSet.contains(Symbol.ABSTRACT) || modifiersSet.contains(Symbol.NATIVE)) {
                throw new WeedException("A method has a body if and only if it is neither abstract nor native.");
            }
        } else if (parent.equals(Symbol.FieldDeclaration)) {
            if (modifiersSet.contains(Symbol.FINAL)) {
                // Check: No field can be final.
                throw new WeedException("No field can be final.");
                // Check: No package private field.
            } else if (!modifiersSet.contains(Symbol.PROTECTED)
                    && !modifiersSet.contains(Symbol.PUBLIC)) {
                throw new WeedException("No package private field");
            }

        } else if (parent.equals(Symbol.InterfaceDeclaration)) {
            if (modifiersSet.contains(Symbol.STATIC)
                    || modifiersSet.contains(Symbol.FINAL)
                    || modifiersSet.contains(Symbol.NATIVE)) {
             // Check: An interface method cannot be static, final, or native.
                throw new WeedException("An interface method cannot be static, final, or native.");
            }
        } else if (parent.equals(Symbol.ClassDeclaration)) {
            if (modifiersSet.contains(Symbol.ABSTRACT)) {
                this.isAbstractClass = true;
            }
            // Check: A class cannot be both abstract and final.
            if (modifiersSet.contains(Symbol.FINAL) && modifiersSet.contains(Symbol.ABSTRACT)) {
                throw new WeedException("A class cannot be both abstract and final.");
                // Check: No package private classes.
            } else if (!modifiersSet.contains(Symbol.PUBLIC)
                    && !modifiersSet.contains(Symbol.PROTECTED)) {
                throw new WeedException("No package private classes");
            }
        } else if (parent.equals(Symbol.Block)) {
            // Check: A non-abstract method must have a body.
            if (!modifiersSet.contains(Symbol.ABSTRACT)
                    && !modifiersSet.contains(Symbol.NATIVE)) {
                throw new WeedException("A non-abstract method must have a body.");
            }
        }
    }

    private ParseTree findNode(ParseTree node, Symbol goal) {
        Queue<ParseTree> queue = new LinkedList<ParseTree>();
        queue.add(node);
        while (!queue.isEmpty()) {
            ParseTree currentNode = (ParseTree) queue.remove();
            for (ParseTree child : currentNode.getChildren()) {
                if ((checkNodeType(child, goal))) {
                    return child;
                }
                queue.add(child);
            }
        }
        return null;
    }

    private boolean checkNodeType(ParseTree node, Symbol symbol) {
        if (node.getTokenType().equals(symbol)) {
            return true;
        }
        return false;
    }
}
