import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class FileManagerTest extends FileManager {
  public void testNullPath() {
    new FileManager(null);
  }
  
  @Test
  public void testValidPath() {
    assertEquals("current folder", ".", new FileManager("").path);
    assertEquals("current folder", ".", new FileManager("  \t").path);
    assertEquals("current folder", ".", new FileManager(".").path);
    assertEquals("current folder", "..", new FileManager("  .. ").path);
    assertEquals("current folder", "junk", new FileManager("junk").path);
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