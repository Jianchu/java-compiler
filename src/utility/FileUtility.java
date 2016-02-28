package utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtility {
    public static ArrayList<String> getFileNames(File dir) {
        List<String> fileNames = new ArrayList<String>();
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                fileNames.addAll(getFileNames(file));
            } else {
                fileNames.add(file.getAbsolutePath());
            }
        }
        return (ArrayList<String>) fileNames;
    }
    
    public static ArrayList<String> getFileNames(String dirPath) {
    	File f = new File(dirPath);
    	return getFileNames(f);
    }
}
