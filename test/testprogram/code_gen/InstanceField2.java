public class InstanceField2 {
    public int i = 100;
    
    public InstanceField2() {
    }
    public static int test() {
	InstanceField2 o = new InstanceField2();
	return o.i;
    }

}