import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
  ClassModelTest.class, 
  FileManagerTest.class, 
  MethodModelTest.class, 
  ModelTest.class, 
  PropertyModelTest.class, 
  SfApexDocTest.class
})
public class AllTests {
}