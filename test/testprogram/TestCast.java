package testprogram;

public class TestCast {

    public static int f = 3;
    public int f1 = 3;
    public TestCast() {
    }
    /**
     * field = local -
     * local = field -
     * field = method -
     * field = local -
     * local = method -
     * array = local -
     * array = field - 
     * array = method -
     * field = field -
     * local = local -
     * array = array -
     * local = array -
     * field = array -
     * 
     */
    public int m(int a) {
        TestCast t = new TestCast();
        int[] g = new int[2];
        TestCast.f = t.f1;
        
        return 1;
    }
}

/**
 * QualifiedName = SimpleName
 * QualifiedName = QualifiedName
 * QualifiedName = MethodInvocation
 * QualifiedName = ArrayAccess
 * 
 * SimpleName = ArrayAccess
 * SimpleName = SimpleName
 * SimpleName = QualifiedName
 * SimpleName = MethodInvocation
 * 
 * ArrayAccess SimpleName
 * ArrayAccess = QualifiedName
 * ArrayAccess = MethodInvocation
 * ArrayAccess = ArrayAccess
 * 
 * 
 */

