package code_generation;

import java.util.ArrayList;
import java.util.List;

import ast.FieldDeclaration;
import ast.MethodDeclaration;
import ast.TypeDeclaration;
import ast.VariableDeclaration;
import utility.StringUtility;

public class CodeGenUtil {
	
	/**
	 * follows thisdecl calling convention from c++, which is similar to cdecl except that at offset zero is the pointer to object
	 * Caller cleans up arguments after the call
	 * @return
	 */
	public static String fnCall(String subLabel, String obj, List<String> params) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < params.size(); i++) {
			String p = params.get(i);
			StringUtility.appendIndLn(sb, "push " + p + "\t; pushing parameter " + i);
		}
		
		StringUtility.appendIndLn(sb, "push " + obj + "\t; push object to offset zero");
		
		StringUtility.appendIndLn(sb, "call " + subLabel);
		StringUtility.appendIndLn(sb, "add esp, " + (params.size()+1) + "*4" + "\t; caller cleanup arguments.");	
		
		return sb.toString();
	}
	
	/**
	 * Template for the body of a subroutine.
	 * [ebp] is the old ebp address
	 * [ebp + 4] is the address automatically pushed by the call instruction 
	 * [ebp + 8] is the address for the object this method belongs to. similar to function(self) in python
	 * [ebp + 3 * 4] is the first parameter, [ebp + 4 * 4s] the second
	 * 
	 * @param fnName
	 * @param fnBody
	 * @return
	 */
	public static String fnDecl(String fnName, String fnBody, int maxOffSet) {
		StringBuilder sb = new StringBuilder();
		
		StringUtility.appendLine(sb, fnName + ": \t; label for subroutine");
		
		StringUtility.appendIndLn(sb, "push ebp \t; save old frame pointer");
		StringUtility.appendIndLn(sb, "mov ebp, esp \t; move ebp to top of stack");
		
		StringUtility.appendIndLn(sb, "sub esp " + maxOffSet + "\t; space for local variables");
		
		StringUtility.appendIndLn(sb, fnBody);
		
		StringUtility.appendIndLn(sb, "add esp, " + maxOffSet + "\t; pop local variables");
		StringUtility.appendIndLn(sb, "pop ebp \t; restore to previosu frame.");
		
		StringUtility.appendIndLn(sb, "ret \t; end of subroutine");
		return sb.toString();
	}
	
	/**
	 * template for variable and formal access
	 * @param base
	 * @param vDecl
	 * @return
	 * @throws Exception 
	 */
	public String varAccess(String base, MethodDeclaration mDecl, VariableDeclaration vDecl) throws Exception {
//		 TODO: get offset from map;
		int offset = mDecl.getVarOffSet(vDecl);
		if (offset < 0) {
			// e.g. offset = -1, return "[base + 3 & 4]"
			return "[" + base + "+" + ((-offset)+2) + "* 4]"; 
		} else {
			// e.g. offset = 1, return [base + 1*4]
			return "[" + base + "-" + offset + "*4]";
		}
	}
	
	public String fieldAccess(String base, TypeDeclaration tDecl, FieldDeclaration fDecl) throws Exception {
		return "[" + base + "+" + tDecl.getFieldOffSet(fDecl.id) + "*4]";
	}
	
	
	public static void main(String[] args) {
		String fn = "multiply";
		List<String> params = new ArrayList<String>();
		params.add("[a]");
		params.add("[b]");
		System.out.println(fnCall(fn, "eax", params));
		
		System.out.println();
		
		System.out.println(fnDecl(fn, "...body...", 8));
	}
}
