package weeder;

import java.util.HashSet;
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

        } else if (parseTree.getTokenType().equals(Symbol.FieldDeclaration)) {
            weed(parseTree, Symbol.FieldDeclaration);
        }
        for (ParseTree child : parseTree.getChildren()) {
            visitParseTree(child);
        }
    }

    private void weed(ParseTree node, Symbol symbol) throws WeedException {
        switch (symbol) {
        case ClassDeclaration:
            // System.out.println(node.getTokenType());
            visitClass(node);
            for (ParseTree child : node.getChildren()) {
                if (child.getTokenType().equals(Symbol.Modifiers)) {
                    visitModifier(child, Symbol.ClassDeclaration);
                }
            }
            break;
        case MethodDeclaration:

            break;
        case InterfaceDeclaration:

            break;
        case FieldDeclaration:
            for (ParseTree child : node.getChildren()) {
                if (child.getTokenType().equals(Symbol.Modifiers)) {
                    visitModifier(child, Symbol.FieldDeclaration);
                }
            }
            break;
        }
    }

    private void visitClass(ParseTree classNode) throws WeedException {
        Stack st = new Stack();
        st.push(classNode);
        boolean constructorFlag = false;
        while (!st.isEmpty()) {
            ParseTree currentNode = (ParseTree) st.pop();
            for (ParseTree child : currentNode.getChildren()) {
                if (child.getTokenType().equals(Symbol.ConstructorDeclaration)) {
                    constructorFlag = true;
                }
                st.push(child);
            }
        }
        if (!constructorFlag) {
         // Check: every class must contain at least one explicit constructor.
            throw new WeedException(
                    "Every class must contain at least one explicit constructor.");
        }
    }

    private void visitModifier(ParseTree modifierNode, Symbol parent)
            throws WeedException {
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
                }
                if (!symbol.equals(Symbol.Modifiers) && !symbol.equals(Symbol.Modifier)) {
                    modifiersSet.add(symbol);
                    System.out.println(child.getTokenType());
                }
                st.push(child);
            }
        }
    }
}
