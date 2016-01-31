package ast;

import java.util.List;

/**
 * Either a class declaration or interface.
 * @author zanel
 *
 */
public class TypeDeclaration {
	boolean isInterface;
	List<Modifier> modifiers;
	String id;
	Name superClass;
	Name superInterfaces;
}
