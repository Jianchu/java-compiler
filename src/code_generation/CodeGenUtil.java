package code_generation;

import java.util.ArrayList;
import java.util.List;

import utility.StringUtility;

public class CodeGenUtil {
	
	/**
	 * follows cdecl call convention
	 * @return
	 */
	public static String fnCall(String subLabel, List<String> params) {
		StringBuilder sb = new StringBuilder();
		StringUtility.appendLine(sb, "push ebp");
		StringUtility.appendLine(sb, "mov ebp esp");
		
		for (int i = params.size() -1; i >= 0; i--) {
			String p = params.get(i);
			StringUtility.appendLine(sb, "push " + p + "\t; pushing parameter " + i);
		}
		StringUtility.appendLine(sb, "call " + subLabel);
		//StringUtility.appendLine(sb, "add esp " + params.size() + "*4" + "\t; caller cleanup");	
		StringUtility.appendLine(sb, "mov esp ebp \t; caller cleanup");
		StringUtility.appendLine(sb, "pop ebp ; restore to previosu frame.");
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String fn = "multiply";
		List<String> params = new ArrayList<String>();
		params.add("[a]");
		params.add("[b]");
		System.out.println(fnCall(fn, params));
	}
}
