package apex.com.main;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;

public class PropertyModelTest {
  @Test
  public void testGetName() {
    ArrayList<String> comments = new ArrayList<String>();
    
    PropertyModel m = new PropertyModel("", comments);
    assertEquals("", m.getName());
    
    m = new PropertyModel(" ", comments);
    assertEquals("", m.getName());
    
    m = new PropertyModel("\t", comments);
    assertEquals("", m.getName());
    
    m = new PropertyModel(" private static int whatever junk a", comments);
    assertEquals("a", m.getName());
    
    m = new PropertyModel(" enum b", comments);
    assertEquals("b", m.getName());
    
    m = new PropertyModel("String[] c", comments);
    assertEquals("c", m.getName());
    
    m = new PropertyModel("int d", comments);
    assertEquals("d", m.getName());
    
    m = new PropertyModel(" int e ", comments);
    assertEquals("e", m.getName());
    
    m = new PropertyModel("f", comments);
    assertEquals("f", m.getName());
    
    m = new PropertyModel(" g", comments);
    assertEquals("g", m.getName());
    
    m = new PropertyModel("h   ", comments);
    assertEquals("h", m.getName());
  }
}