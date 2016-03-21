package code_generation;

import java.util.ArrayList;
import java.util.List;

import utility.StringUtility;

public class CodeGenUtil {
	
	/**
	 * follows thisdecl calling convention from c++, which is similar to cdecl except that at offset zero is the pointer to object
	 * Caller cleans up arguments after the call
	 * @return
	 */
	public static String fnCall(String subLabel, String obj, List<String> params) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = params.size() -1; i >= 0; i--) {
			String p = params.get(i);
			StringUtility.appendIndLn(sb, "push " + p + "\t; pushing parameter " + i);
		}
		
		StringUtility.appendIndLn(sb, "push " + obj + "\t; push object to offset zero");
		
		StringUtility.appendIndLn(sb, "call " + subLabel);
		StringUtility.appendIndLn(sb, "add esp " + (params.size()+1) + "*4" + "\t; caller cleanup arguments.");	
		
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
		StringUtility.appendIndLn(sb, "mov ebp esp \t; move ebp to top of stack");
		
		StringUtility.appendIndLn(sb, "sub esp " + maxOffSet + "\t; space for local variables");
		
		StringUtility.appendIndLn(sb, fnBody);
		
		StringUtility.appendIndLn(sb, "add esp " + maxOffSet + "\t; pop local variables");
		StringUtility.appendIndLn(sb, "pop ebp \t; restore to previosu frame.");
		
		StringUtility.appendIndLn(sb, "ret \t; end of subroutine");
		return sb.toString();
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
