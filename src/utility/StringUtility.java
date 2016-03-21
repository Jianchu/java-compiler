package utility;

public class StringUtility {

    public static void appendLine(StringBuilder sb, String s) {
        sb.append(s);
        sb.append("\n");
    }
    
    public static void appendLine(StringBuilder sb, String s, int indent) {
    	String ind = "";
    	for (int i = 0; i < indent; i++) {
    		ind += "\t";
    	}
    	appendLine(sb, ind + s);
    }
    
    public static void appendIndLn(StringBuilder sb, String s) {
    	appendLine(sb, s, 1);
    }
}
