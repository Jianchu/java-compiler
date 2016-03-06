package testprogram;

public class TestCast {

    public static int f = 3;
    public int f1 = 3;
    public int[] f2 = new int[3];
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
    public TestCast m(int a) {
        TestCast t = new TestCast();
        a = t.f2.length;
        int q = t.m(3).f;
        return t;
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

