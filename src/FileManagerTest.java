import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class FileManagerTest extends FileManager {
  public void testNullPath() {
    new FileManager(null);
  }
  
  @Test
  public void testValidPath() {
    FileManager m = new FileManager("");
    assertEquals("current folder", ".", m.path);
    
    m = new FileManager("  \t");
    assertEquals("current folder", ".", m.path);
    
    m = new FileManager(".");
    assertEquals("current folder", ".", m.path);
    
    m = new FileManager("  .. ");
    assertEquals("current folder", "..", m.path);
    
    m = new FileManager("junk");
    assertEquals("current folder", "junk", m.path);
  }
  
  @Test (expected = NullPointerException.class)
  public void testCreateDocsWithNullModels() {
    new FileManager("").createDocs(null, "", "");
  }
  
  @Test (expected = NullPointerException.class)
  public void testCreateDocsWithNullDetail() {
    new FileManager("").createDocs(new ArrayList<ClassModel>(), null, "");
  }
  
  @Test (expected = NullPointerException.class)
  public void testCreateDocsWithNullContents() {
    new FileManager("").createDocs(new ArrayList<ClassModel>(), "", null);
  }
}