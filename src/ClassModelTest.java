import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;

public class ClassModelTest {
  @Test (expected = NullPointerException.class)
  public void testClassModelBlankLine() {
    new ClassModel("", new ArrayList<String>());
  }
    
  @Test (expected = NullPointerException.class)
  public void testClassModelNoClass() {
    new ClassModel("junk", new ArrayList<String>());
  }
    
  @Test (expected = NullPointerException.class)
  public void testClassModelNoClassName() {
    new ClassModel("class", new ArrayList<String>());
  }
    
  @Test (expected = NullPointerException.class)
  public void testClassModelPartialClassMatch() {
    new ClassModel("clas X", new ArrayList<String>());
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
    assertTrue(m.isInterface);
    
    m = new ClassModel("class e_2", comments);
    assertEquals("e_2", m.getName());
  }
}