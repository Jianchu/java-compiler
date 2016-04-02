public class NullCheck1 {
    
    public int x = 123;
    public NullCheck1() {}
    public static int test() {
	NullCheck1 cn = null;
	return cn.x;
    }

}