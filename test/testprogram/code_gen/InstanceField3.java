public class InstanceField3 {
    public InstanceField3 x;
    public int a = 1;
    public InstanceField3() {}
    public static int test() {
	InstanceField3 o1 = new InstanceField3();
	InstanceField3 o2 = new InstanceField3();
	InstanceField3 o3 = new InstanceField3();
	o1.x = o2;
	o2.x = o3;

	return o1.x.x.a;
	
    }
}