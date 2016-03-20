package code_generation;

import java.util.List;

import utility.StringUtility;

public class CodeGenUtil {
	
	/**
	 * follows cdecl call convention
	 * @return
	 */
	public static String fnCall(String subLabel, List<String> params) {
		StringBuilder sb = new StringBuilder();
		for (int i = params.size() -1; i >= 0; i--) {
			String p = params.get(i);
			StringUtility.appendLine(sb, "push " + p + "\t; pushing parameter " + i);
		}
		StringUtility.appendLine(sb, "call " + subLabel);
		StringUtility.appendLine(sb, "add esp " + params.size() + "\t; caller cleanup");	
		return sb.toString();
	}
}
