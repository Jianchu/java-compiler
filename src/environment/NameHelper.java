package environment;

import ast.MethodDeclaration;

public class NameHelper {
	
	/**
	 * TODO: figure a name mangling scheme that combines all info of the signature.
	 * @param md
	 * @return
	 */
	public static String mangle(MethodDeclaration md) {
		
		return md.id;
	}
}
