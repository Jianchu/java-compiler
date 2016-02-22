package environment;

import ast.MethodDeclaration;
import ast.*;

public class NameHelper  {
	
	/**
	 * DONE: figure a name mangling scheme that combines all info of the signature.
	 * problem is with the types
	 * type name string might be qualified or simple and they might be the same thing.
	 * 
	 * ASSUMPTION: that type linking has been done on the parameters
	 * 
	 * Method name format: [namelength][methodname][typename1length][typename1][typename2length][typename2]
	 * @param md
	 * @return
	 */
	public static String mangle(MethodDeclaration md) {
		String mName = md.id.length() + md.id;
		for (VariableDeclaration pd : md.parameters) {
			String typeName; 
			if (pd.type instanceof PrimitiveType) {
				typeName = pd.type.toString();
			} else {
				typeName = pd.type.getDeclaration().getFullName();
			}
			mName += typeName.length() + typeName;
		}
		
		return mName;
	}
}
