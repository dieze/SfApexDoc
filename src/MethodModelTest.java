import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;

public class MethodModelTest {
  @Test
  public void testMethodModelInvalidParameters() {
    ArrayList<String> comments = new ArrayList<String>();
    try {
      new MethodModel(null, null);
      assertTrue("invalid parameters", false);
    } catch (NullPointerException e) {
    }
    
    try {
      new MethodModel("", null);
      assertTrue("invalid parameters", false);
    } catch (NullPointerException e) {
    }
    
    try {
      new MethodModel(null, comments);
      assertTrue("invalid parameters", false);
    } catch (NullPointerException e) {
    }
  }
  
  @Test
  public void testGetName() {
    ArrayList<String> comments = new ArrayList<String>();
    MethodModel m = new MethodModel("", comments);
    assertEquals("", m.getName());
    
    m = new MethodModel(" ", comments);
    assertEquals("", m.getName());
    
    m = new MethodModel("\t", comments);
    assertEquals("", m.getName());
    
    m = new MethodModel(" private static int whatever junk a() {", comments);
    assertEquals("a", m.getName());
    
    m = new MethodModel(" void b ( params... )", comments);
    assertEquals("b", m.getName());
    
    m = new MethodModel("String[] c(", comments);
    assertEquals("c", m.getName());
    
    m = new MethodModel("int d  ", comments);
    assertEquals("d", m.getName());
    
    m = new MethodModel(" int e", comments);
    assertEquals("e", m.getName());
    
    m = new MethodModel("f", comments);
    assertEquals("f", m.getName());
    
    m = new MethodModel(" g_2 ", comments);
    assertEquals("g_2", m.getName());
  }
}