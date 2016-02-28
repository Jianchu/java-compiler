package ast;

import java.util.LinkedList;
import java.util.List;

import parser.ParseTree;
import scanner.Symbol;
import exceptions.ASTException;

public class ASTHelper {
	
	public static String parseID(ParseTree pt) throws ASTException {
		if (pt.getTokenType() != Symbol.ID) 
			throw new ASTException("unexpected symbol " + pt.getTokenType());
		return pt.getLexeme();
		
	}
	
	@SuppressWarnings("unchecked")
	public static <E extends Next<E>> List<E> getList(E elem) {
		List<E> result = new LinkedList<E>();
		result.add(elem);
		while (elem.hasNext()) {
			elem = elem.next();
			result.add(elem);
		}
		return result;
	}
}
