public class TestArray {
    public TestArray() {
        
    }
    
    public static int test() {
        int[] a = new int[100]; 
        int[] b = new int[100];
        
        for (int i=0; i<100; i=i+1) a[i] = (3*i)%100;
        for (int i=0; i<100; i=i+1) b[i] = 99-i;
        int[] x = a;
	
	int y =  x[(x = b)[30]];
	
	return y; // should be 99
	//return a[b[30]];
    }
}