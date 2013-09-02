package apex.com.main;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;

import apex.com.main.ClassModel;

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
    
    assertEquals("a", new ClassModel(" private static int whatever class a", comments).getName());
    assertEquals("b", new ClassModel(" class    b { ... }", comments).getName());
    assertEquals("c", new ClassModel("class c extends something", comments).getName());
    assertFalse(new ClassModel(" class    b { ... }", comments).isInterface);
    assertEquals("d", new ClassModel(" interface d{", comments).getName());
    assertTrue(new ClassModel(" interface d{", comments).isInterface);
    assertEquals("e_2", new ClassModel("class e_2", comments).getName());
  }
}