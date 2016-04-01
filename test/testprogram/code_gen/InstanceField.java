public class InstanceField {
    public int i;
    public InstanceField() {
	i = 100;
    }
    public static int test() {
	InstanceField o = new InstanceField();
	InstanceField o2 = new InstanceField();
	return o.i;
    }

}