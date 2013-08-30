import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;

public class MethodModelTest {
  @Test
  public void testGetName() {
    ArrayList<String> comments = new ArrayList<String>();
    assertEquals("", new MethodModel("", comments).getName());
    assertEquals("", new MethodModel(" ", comments).getName());
    assertEquals("", new MethodModel("\t", comments).getName());
    assertEquals("a", new MethodModel(" private static int whatever junk a() {", comments).getName());
    assertEquals("b", new MethodModel(" void b ( params... )", comments).getName());
    assertEquals("c", new MethodModel("String[] c(", comments).getName());
    assertEquals("d", new MethodModel("int d  ", comments).getName());
    assertEquals("e", new MethodModel(" int e", comments).getName());
    assertEquals("f", new MethodModel("f", comments).getName());
    assertEquals("g_2", new MethodModel(" g_2 ", comments).getName());
  }
}