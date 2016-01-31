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
    private boolean isAbstractClass = false;

    public Weeder(ParseTree parseTree) throws Exception {
        this.parseTree = parseTree;
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
                }
            }
        } else {
            ParseTree InterfaceDecNode = findNode(parseTree,Symbol.InterfaceDeclaration);
            for (ParseTree child : InterfaceDecNode.getChildren()) {
                if (checkNodeType(child, Symbol.Modifiers)) {
                    noModifier = false;
                }
            }
            if (InterfaceDecNode != null) {
                ParseTree methodHeaderNode = findNode(InterfaceDecNode,Symbol.MethodHeader);
                if (methodHeaderNode != null) {

                    visitModifier(methodHeaderNode, Symbol.InterfaceDeclaration);
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
                        if (findNode(child, Symbol.Block) != null) {
                            visitModifier(modifierNode, Symbol.MethodDeclaration);
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
                    throw new WeedException("Duplicated modifer.");
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