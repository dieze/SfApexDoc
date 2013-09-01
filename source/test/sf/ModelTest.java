package sf;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;

public class ModelTest {
  @Test (expected = NullPointerException.class)
  public void testModelWithNullNameLine() {
    new Model(null, new ArrayList<String>());
  }

  @Test (expected = NullPointerException.class)
  public void testUntrimmedNameLine() {
    new Model("  ", new ArrayList<String>());
  }

  @Test (expected = NullPointerException.class)
  public void testNameLineWithLeadingSpace() {
    new Model(" test", new ArrayList<String>());
  }

  @Test (expected = NullPointerException.class)
  public void testNameLineWithTrailingSpace() {
    new Model("test ", new ArrayList<String>());
  }

  @Test (expected = NullPointerException.class)
  public void testModelWithNullComments() {
    new Model("", null);
  }

  @Test (expected = NullPointerException.class)
  public void testGetNameWithoutOverride() {
    new Model("", new ArrayList<String>()).getName();
  }

  @Test
  public void testNoLinks() {
    // use a derived type since 'getName' isn't provided for the base class
    MethodModel m = new MethodModel("Name1 Name_0 Name2", new ArrayList<String>());
    m.addLinks();
    assertEquals("no links", "Name1 Name_0 Name2", m.getNameLine());
  }

  @Test
  public void testNoMatchingLinks() {
    // use a derived type since 'getName' isn't provided for the base class
    MethodModel m = new MethodModel("Name1 Name_0 Name2", new ArrayList<String>());
    m.addType("Nam", "Link");
    m.addType("Name3", "Link");
    m.addLinks();
    assertEquals("no links", "Name1 Name_0 Name2", m.getNameLine());
  }

  @Test
  public void testCaseInsensitiveLink() {
    // use a derived type since 'getName' isn't provided for the base class
    MethodModel m = new MethodModel("Name1 Name_0 Name2", new ArrayList<String>());
    m.addType("name_0", "Link");
    m.addLinks();
    assertEquals("link was not applied", "Name1 <a href='link'>Name_0</a> Name2", m.getNameLine());
  }

  @Test
  public void testLinkInCollection() {
    // use a derived type since 'getName' isn't provided for the base class
    MethodModel m = new MethodModel("Set<Name1> List<Name_0> Name2[]", new ArrayList<String>());
    m.addType("Name_0", "Link");
    m.addLinks();
    assertEquals("link was not applied", "Set&lt;Name1&gt; List&lt;<a href='link'>Name_0</a>&gt; Name2[]", m.getNameLine());
  }

  @Test
  public void testLink() {
    // use a derived type since 'getName' isn't provided for the base class
    MethodModel m = new MethodModel("Name1 Name Name2", new ArrayList<String>());
    m.addType("Name", "Link");
    m.addLinks();
    assertEquals("link was not applied", "Name1 <a href='link'>Name</a> Name2", m.getNameLine());
  }

  @Test (expected = NullPointerException.class)
  public void testAddLinksMultipleCalls() {
    // use a derived type since 'getName' isn't provided for the base class
    MethodModel m = new MethodModel("", new ArrayList<String>());
    m.addLinks();
    m.addLinks();
  }

  @Test
  public void testNoComments() {
    ArrayList<String> comments = new ArrayList<String>();
    Model m = new Model("", comments);
    assertBlankComment(m);
  }
  
  @Test
  public void testBlankComment() {
    ArrayList<String> comments = new ArrayList<String>();
    comments.add("");
    Model m = new Model("", comments);
    assertBlankComment(m);
  }
  
  @Test
  public void testEmptyComments() {
    ArrayList<String> comments = new ArrayList<String>();
    comments.add(" \t");
    comments.add("       ");
    comments.add(" *  ");
    comments.add(" *");
    comments.add("*    ");
    comments.add("");
    Model m = new Model("", comments);
    assertBlankComment(m);
  }
  
  @Test
  public void testCommentWhitespace() {
    ArrayList<String> comments = new ArrayList<String>();
    comments.add(" \ta");
    comments.add("  b     ");
    comments.add(" *c  ");
    comments.add(" *");
    comments.add("*  d  ");
    comments.add("");
    comments.add("\t @see   e  ");
    Model m = new Model("", comments);
    assertEquals("description", "a b c d", m.getDescription());
    assertEquals("see", "e", m.getSee());
  }
  
  @Test
  public void testCommentCaseSensitivity() {
    ArrayList<String> comments = new ArrayList<String>();
    comments.add("@return a");
    comments.add("@AUTHOR a");
    comments.add("@dATea");
    Model m = new Model("", comments);
    assertEquals("return", "a", m.getReturns());
    assertEquals("author", "a", m.getAuthor());
    assertEquals("date", "a", m.getDate());
  }
  
  @Test
  public void testCommentInvalidKeys() {
    ArrayList<String> comments = new ArrayList<String>();
    comments.add("@ return b");
    comments.add("@auth b");
    comments.add("date b");
    Model m = new Model("", comments);
    assertEquals("return", "", m.getReturns());
    assertEquals("author", "", m.getAuthor());
    assertEquals("date", "", m.getDate());
  }
  
  @Test
  public void testMultiline() {
    ArrayList<String> comments = new ArrayList<String>();
    comments.add("@see Hey ");
    comments.add(" *   ");
    comments.add(" *there   ");
    comments.add("\tBob ");
    Model m = new Model("", comments);
    assertEquals("see", "Hey there Bob", m.getSee());
    assertEquals("blank description", "", m.getDescription());
  }
  
  @Test
  public void testAllSingle() {
    ArrayList<String> comments = new ArrayList<String>();
    comments.add(" this is a description\t  ");
    comments.add("@author  Me");
    comments.add("@date  8/25/2013 12:07pm ");
    comments.add("@see Bob\t");
    comments.add("@return\tvoid");
    comments.add("@param 1 a");
    comments.add("@param 2 b");
    Model m = new Model("", comments);
    assertEquals("description", "this is a description", m.getDescription());
    assertEquals("author", "Me", m.getAuthor());
    assertEquals("date", "8/25/2013 12:07pm", m.getDate());
    assertEquals("see", "Bob", m.getSee());
    assertEquals("return", "void", m.getReturns());
    assertEquals("param 1", "1 a", m.getParams().get(0));
    assertEquals("param 2", "2 b", m.getParams().get(1));
  }
  
  @Test
  public void testMultipleTags() {
    ArrayList<String> comments = new ArrayList<String>();
    comments.add("first");
    comments.add("one");
    comments.add("@author me");
    comments.add("@description second");
    comments.add("one");
    comments.add("@author you");
    Model m = new Model("", comments);
    assertEquals("author", "me you", m.getAuthor());
    assertEquals("description", "first one second one", m.getDescription());
  }
  
  private void assertBlankComment(Model m) {
    assertEquals("blank description", "", m.getDescription());
    assertEquals("blank author", "", m.getAuthor());
    assertEquals("blank date", "", m.getDate());
    assertEquals("blank returns", "", m.getReturns());
    assertEquals("blank see", "", m.getSee());
    assertEquals("blank params", 0, m.getParams().size());
  }
}