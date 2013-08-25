import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;

public class ClassModelTest {
  @Test
  public void testClassModelInvalidParameters() {
    ArrayList<String> comments = new ArrayList<String>();
    try {
      new ClassModel(null, null);
      assertTrue("invalid parameters", false);
    } catch (NullPointerException e) {
    }
    
    try {
      new ClassModel("", null);
      assertTrue("invalid parameters", false);
    } catch (NullPointerException e) {
    }
    
    try {
      new ClassModel(null, comments);
      assertTrue("invalid parameters", false);
    } catch (NullPointerException e) {
    }
    
    try {
      new ClassModel("", comments);
      assertTrue("invalid parameters", false);
    } catch (NullPointerException e) {
    }
    
    try {
      new ClassModel("junk", comments);
      assertTrue("invalid parameters", false);
    } catch (NullPointerException e) {
    }
    
    try {
      new ClassModel("class", comments);
      assertTrue("invalid parameters", false);
    } catch (NullPointerException e) {
    }
    
    try {
      new ClassModel("lass X", comments);
      assertTrue("invalid parameters", false);
    } catch (NullPointerException e) {
    }
  }
  
  @Test
  public void testGetName() {
    ArrayList<String> comments = new ArrayList<String>();
    
    ClassModel m = new ClassModel(" private static int whatever class a", comments);
    assertEquals("a", m.getName());
    
    m = new ClassModel(" class    b { ... }", comments);
    assertEquals("b", m.getName());
    
    m = new ClassModel("class c extends something", comments);
    assertEquals("c", m.getName());
    assertEquals(false, m.isInterface);
    
    m = new ClassModel(" interface d{", comments);
    assertEquals("d", m.getName());
    assertEquals(true, m.isInterface);
    
    m = new ClassModel("class e_2", comments);
    assertEquals("e_2", m.getName());
  }
}