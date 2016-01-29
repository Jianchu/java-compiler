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
        weed();
    }
    
    private void weed() throws WeedException {
        visitParseTree(parseTree);
    }

    private void visitParseTree(ParseTree parseTree) throws WeedException {
        ParseTree modifierNode;
        if (parseTree.getTokenType().equals(Symbol.ClassDeclaration)) {
            ParseTree ConstructorNode = findNode(parseTree,Symbol.ConstructorDeclaration);
            if (!ConstructorNode.getTokenType().equals(Symbol.ConstructorDeclaration)) {
                throw new WeedException("Every class must contain at least one explicit constructor.");
            }
            modifierNode = findNode(parseTree, Symbol.Modifiers);
            visitModifier(modifierNode, Symbol.ClassDeclaration);
            // weed(parseTree, Symbol.ClassDeclaration);
        } else if (parseTree.getTokenType().equals(Symbol.MethodDeclaration)) {
            modifierNode = findNode(parseTree, Symbol.MethodHeader);
            visitModifier(modifierNode, Symbol.MethodHeader);
            ParseTree methodHeader = modifierNode;
            if (findNode(parseTree, Symbol.MethodBody).getTokenType().equals(Symbol.MethodBody)) {
                visitModifier(methodHeader, Symbol.MethodDeclaration);
            }
            //weed(parseTree, Symbol.MethodDeclaration);
        } else if (parseTree.getTokenType().equals(Symbol.FieldDeclaration)) {
            modifierNode = findNode(parseTree, Symbol.Modifiers);
            visitModifier(modifierNode, Symbol.FieldDeclaration);
        } else if (parseTree.getTokenType().equals(Symbol.InterfaceDeclaration)) {
            ParseTree methodHeaderNode = findNode(parseTree,Symbol.MethodHeader);
            if (methodHeaderNode.getTokenType().equals(Symbol.MethodHeader)) {
                visitModifier(methodHeaderNode, Symbol.InterfaceDeclaration);
            }
        }
        for (ParseTree child : parseTree.getChildren()) {
            visitParseTree(child);
        }
    }

    private ParseTree findNode(ParseTree node, Symbol goal) {
        Queue<ParseTree> queue = new LinkedList<ParseTree>();
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

        Stack<ParseTree> st = new Stack<ParseTree>();
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
