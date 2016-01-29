package weeder;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.WeedException;

public class Weeder {

    final private ParseTree parseTree;

    public Weeder(ParseTree parseTree) throws Exception {
        this.parseTree = parseTree;
        visitParseTree();
    }
    
    private void visitParseTree() throws WeedException {
        visitParseTree(parseTree);
    }

    private void visitParseTree(ParseTree parseTree) throws WeedException {
        if (parseTree.getTokenType().equals(Symbol.ClassDeclaration)) {
            weed(parseTree, Symbol.ClassDeclaration);
        } else if (parseTree.getTokenType().equals(Symbol.MethodDeclaration)) {
            weed(parseTree, Symbol.MethodDeclaration);
        } else if (parseTree.getTokenType().equals(Symbol.FieldDeclaration)) {
            weed(parseTree, Symbol.FieldDeclaration);
        } else if (parseTree.getTokenType().equals(Symbol.InterfaceDeclaration)) {
            weed(parseTree, Symbol.InterfaceDeclaration);
        }
        for (ParseTree child : parseTree.getChildren()) {
            visitParseTree(child);
        }
    }

    private void weed(ParseTree node, Symbol symbol) throws WeedException {
        ParseTree modifierNode;
        switch (symbol) {
        case ClassDeclaration:
            ParseTree ConstructorNode = findNode(parseTree,Symbol.ConstructorDeclaration);
            if (!ConstructorNode.getTokenType().equals(Symbol.ConstructorDeclaration)) {
                throw new WeedException(
                        "Every class must contain at least one explicit constructor.");
            }
            modifierNode = findNode(node, Symbol.Modifiers);
            visitModifier(modifierNode, Symbol.ClassDeclaration);
            break;
        case MethodDeclaration:
            ParseTree methodHeader = null;
            for (ParseTree child : node.getChildren()) {
                if (child.getTokenType().equals(Symbol.MethodHeader)) {
                    methodHeader = child;
                    visitModifier(child, Symbol.MethodHeader);
                } else if (child.getTokenType().equals(Symbol.MethodBody)) {
                    visitModifier(methodHeader, Symbol.MethodDeclaration);
                }
            }
            break;
        case InterfaceDeclaration:
            ParseTree methodHeaderNode = findNode(node, Symbol.MethodHeader);
            if (methodHeaderNode.getTokenType().equals(Symbol.MethodHeader)) {
                visitModifier(methodHeaderNode, Symbol.InterfaceDeclaration);
            }

            break;
        case FieldDeclaration:
            modifierNode = findNode(node, Symbol.Modifiers);
            visitModifier(modifierNode, Symbol.FieldDeclaration);
            break;
        }
    }

    private ParseTree findNode(ParseTree node, Symbol goal) {
        Queue queue = new LinkedList();
        queue.add(node);
        while (!queue.isEmpty()) {
            ParseTree currentNode = (ParseTree) queue.remove();
            for (ParseTree child : currentNode.getChildren()) {
                if (child.getTokenType().equals(goal)) {
                    return child;
                }
                queue.add(child);
            }
        }
        return node;
    }

    private void visitModifier(ParseTree modifierNode, Symbol parent)
            throws WeedException {
        System.out.println(modifierNode.getChildren());

        Stack st = new Stack();
        Set<Symbol> modifiersSet = new HashSet<Symbol>();
        st.push(modifierNode);
        while (!st.isEmpty()) {
            ParseTree currentNode = (ParseTree) st.pop();
            for (ParseTree child : currentNode.getChildren()) {
                Symbol symbol = child.getTokenType();
                if (modifiersSet.contains(symbol)) {
                    // Check: Duplicated modifer.
                    throw new WeedException("Duplicated modifer.");
                } else if (parent.equals(Symbol.ClassDeclaration)) {
                    // Check: A class cannot be both abstract and final.
                    if (symbol.equals(Symbol.ABSTRACT) && modifiersSet.contains(Symbol.FINAL)) {
                        throw new WeedException("A class cannot be both abstract and final.");
                    } else if(symbol.equals(Symbol.FINAL) && modifiersSet.contains(Symbol.ABSTRACT)) {
                        throw new WeedException("A class cannot be both abstract and final.");
                    }
                } else if (parent.equals(Symbol.FieldDeclaration) && symbol.equals(Symbol.FINAL)) {
                    // Check: No field can be final.
                    throw new WeedException("No field can be final.");
                } else if (parent.equals(Symbol.MethodHeader)) {
                    // Check: A static method cannot be final.
                    if (symbol.equals(Symbol.STATIC) && modifiersSet.contains(Symbol.FINAL)) {
                        throw new WeedException("A static method cannot be final.");
                    } else if (symbol.equals(Symbol.FINAL) && modifiersSet.contains(Symbol.STATIC)) {
                        throw new WeedException("A static method cannot be final.");
                    }
                    // Check: An interface method cannot be static, final, or native.
                } else if (parent.equals(Symbol.InterfaceDeclaration)) {
                    if (symbol.equals(Symbol.STATIC)
                            || symbol.equals(Symbol.FINAL)
                            || symbol.equals(Symbol.NATIVE)) {
                        throw new WeedException("An interface method cannot be static, final, or native.");
                    }
                }
                if (!symbol.equals(Symbol.Modifiers) && !symbol.equals(Symbol.Modifier)) {
                    modifiersSet.add(symbol);
                }
                st.push(child);
            }
        }
        // Check: An abstract method cannot be static or final.
        if (parent.equals(Symbol.MethodHeader)) {
            if (modifiersSet.contains(Symbol.ABSTRACT)
                    && (modifiersSet.contains(Symbol.STATIC))
                    || modifiersSet.contains(Symbol.FINAL)) {
                throw new WeedException("An abstract method cannot be static or final.");
                // Check: A native method must be static.
            } else if (modifiersSet.contains(Symbol.NATIVE)
                    && !modifiersSet.contains(Symbol.STATIC)) {
                throw new WeedException("A native method must be static.");
            }
            // Check: A method has a body if and only if it is neither abstract nor native.
        } else if (parent.equals(Symbol.MethodDeclaration)) {
            if (modifiersSet.contains(Symbol.ABSTRACT) || modifiersSet.contains(Symbol.NATIVE)) {
                throw new WeedException("A method has a body if and only if it is neither abstract nor native.");
            }
        }
    }
}
