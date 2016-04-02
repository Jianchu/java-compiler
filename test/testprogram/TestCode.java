public class TestCode {
    public TestCode() {}
//    public static int b = TestCode.a; // b = 0
//    public static int a = 22; // a = 22
    
    public static int test(){
        //return TestCode.a;
        
        boolean b = true;
        boolean e = false;

        if (b && e){
            return 123;
        }
        else {
            return 12;
        }
    }
}