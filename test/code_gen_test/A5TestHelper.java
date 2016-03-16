package code_gen_test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class A5TestHelper {

    private final List<String> fileNames;

    public A5TestHelper() {
        this.fileNames = getFileNames(new File(System.getProperty("user.dir") + "/assignment_testcases/a5"));
    }
    
    private ArrayList<String> getFileNames(File dir) {
        List<String> fileNames = new ArrayList<String>();
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                if (file.getName().contains(".class")) {
                    fileNames.add(file.getName());
                }
            }
        }
        return (ArrayList<String>) fileNames;
    }

    protected int invokeMethod() throws Exception {
        File root = new File(System.getProperty("user.dir")+ "/assignment_testcases/a5");
        for (String fileName : fileNames) {
            try {
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {root.toURI().toURL()});
            String className = fileName.substring(0, fileName.lastIndexOf('.'));
            Class<?> clazz = Class.forName(className, true, classLoader);
            Method method = clazz.getMethod("test", null);
            int result = (int) method.invoke(null, null);
            System.out.println("file: " + fileName + " result: " + result);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(fileName);
            }
        }
        return 0;
    }

    public static void main(String args[]) throws Exception {
        A5TestHelper a = new A5TestHelper();
        a.invokeMethod();
    }
}
