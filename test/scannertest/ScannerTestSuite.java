package scannertest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@RunWith(Suite.class)
@Suite.SuiteClasses({
   TestCombo.class,
   TestComments.class,
   TestIdentifier.class,
   TestLiteral.class,
   TestOperators.class,
   TestProgram.class
})
 	
public class ScannerTestSuite {

}
