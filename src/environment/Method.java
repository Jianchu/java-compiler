package environment;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ast.*;

public class Method {
	private String id;
	private List<Type> paramTypes;
	
	public Method(MethodDeclaration md) {
		id = md.id;
		paramTypes = new LinkedList<Type>();
		for (VariableDeclaration vd : md.parameters) {
			paramTypes.add(vd.type);
		}
	}
	
	
	/**
	 * Compares two method signatures
	 */
	@Override
	public boolean equals(Object o) {
		if (! (o instanceof Method)) {
			return false;
		}
		Method m = (Method) o;
		if (! (m.getName() == id)){
			return false;
		}
		if (m.getParamTypes().size() != paramTypes.size())
			return false;

		Iterator<Type> thisIter = paramTypes.iterator();
		Iterator<Type> otherIter = m.getParamTypes().iterator();
		
		while (thisIter.hasNext()) {
			
			Type thisNext = thisIter.next();
			Type otherNext = otherIter.next();
			
			if (thisNext instanceof PrimitiveType && otherNext instanceof PrimitiveType) {
				if (((PrimitiveType) thisNext).value != ((PrimitiveType) otherNext).value) {
					return false;
				}
			} else if (thisNext instanceof SimpleType && otherNext instanceof SimpleType) {
				//ASSUMPTION: that type visitor has been called at this point.
				if (thisIter.next().getDeclaration() != otherIter.next().getDeclaration()) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int h = id.hashCode();
		for (Type t : paramTypes) {
			h *= t.getDeclaration().hashCode();
		}
		return h;
	}
	
	public String getName() {
		return id;
	}
	
	public List<Type> getParamTypes() {
		return paramTypes;
	}
}
