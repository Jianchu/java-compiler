package code_gen_test;

import joosc.Joosc;
import utility.FileUtility;

public class CodeGenTestMain {
	public static void main(String[] args){
        String[] paths = new String[0];
        paths = FileUtility.getFileNames(System.getProperty("user.dir") + "/test/testprogram/TestCode.java").toArray(paths);
        Joosc.compileSTL(paths);
	}
 }
