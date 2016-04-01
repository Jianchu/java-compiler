
public class InstanceMethod {
    public InstanceMethod() {}
    public static int test() {
	InstanceMethod o = new InstanceMethod();
	return o.m(1,123);
    }

    public int m(int i, int j) {
	return j;
    }
    
}