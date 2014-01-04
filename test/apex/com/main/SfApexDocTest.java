package apex.com.main;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import apex.com.main.SfApexDoc;

public class SfApexDocTest {
  //---------------------------------------------------------------------------
  // Constants
  final ArrayList<String> publicScope = scopes(new String[]{ "public" });
  
  
  //---------------------------------------------------------------------------
  // Tests
  @Test
  public void testNoClass() {
    assertNull(SfApexDoc.parse("", scopes(new String[]{})));
    assertNull(SfApexDoc.parse("", publicScope));
    assertNull(SfApexDoc.parse("", scopes(new String[]{ "private" })));
    assertNull(SfApexDoc.parse("public noclass A", publicScope));
    assertNull(SfApexDoc.parse("public classless A", publicScope));
    assertNull(SfApexDoc.parse("public class ", publicScope));
    assertNull(SfApexDoc.parse("public class // A", publicScope));
  }
  
  @Test
  public void testClassWithWrongScope() {
    assertNull(SfApexDoc.parse("class A", scopes(new String[]{ "private" })));
    assertNull(SfApexDoc.parse("class A", publicScope));
    assertNull(SfApexDoc.parse("private class A", scopes(new String[]{ "public,global,protected" })));
    assertNull(SfApexDoc.parse("public class A", scopes(new String[]{ "private,global,protected" })));
    assertNull(SfApexDoc.parse("public interface A", scopes(new String[]{ "private" })));
  }
  
  @Test
  public void testPropertyWithWrongScope() {
    assertTrue(SfApexDoc.parse("public class A {\n String s", publicScope).properties.isEmpty());
    assertTrue(SfApexDoc.parse("public class A {\n publicx String s", publicScope).properties.isEmpty());
    assertTrue(SfApexDoc.parse("public class A {\n xpublic String s", publicScope).properties.isEmpty());
  }
  
  @Test
  public void testMethodWithWrongScope() {
    assertTrue(SfApexDoc.parse("public class A {\n String s(", publicScope).properties.isEmpty());
    assertTrue(SfApexDoc.parse("public class A {\n publicx String s(", publicScope).properties.isEmpty());
    assertTrue(SfApexDoc.parse("public class A {\n xpublic String s(", publicScope).properties.isEmpty());
  }
  
  @Test
  public void testClass() {
    assertEquals("A", SfApexDoc.parse("PUBLIC interface A {", publicScope).getName());
    assertEquals("A", SfApexDoc.parse("  public  CLASS  A {", publicScope).getName());
    assertEquals("A", SfApexDoc.parse("  public class  A{ ", publicScope).getName());
    assertEquals("A", SfApexDoc.parse("  public class  A { ", publicScope).getName());
    assertEquals("A", SfApexDoc.parse("  public virtual class  A {", publicScope).getName());
    assertEquals("A", SfApexDoc.parse("  public abstract class  A {", publicScope).getName());
    assertEquals("A", SfApexDoc.parse("  public with sharing class A {", publicScope).getName());
    assertEquals("A", SfApexDoc.parse("  @future public without sharing class A {", publicScope).getName());
    assertEquals("A", SfApexDoc.parse("  public class A implements B, C extends D {", publicScope).getName());
    assertEquals("A", SfApexDoc.parse("  \n public\tclass \tA {", publicScope).getName());
    assertEquals("A", SfApexDoc.parse("  \n public \n class \n A \n{", publicScope).getName());
    assertEquals("A", SfApexDoc.parse("  \n public \n class \n A implements B, C extends D\n{", publicScope).getName());
    
    assertEquals("PUBLIC interface A", SfApexDoc.parse("PUBLIC interface A {", publicScope).getNameLine());
    assertEquals("public class  A", SfApexDoc.parse("  \n public\tclass \tA {", publicScope).getNameLine());
    assertEquals("public class A", SfApexDoc.parse("  \n public \n class \n A \n{", publicScope).getNameLine());
    assertEquals("public class A implements B, C extends D", SfApexDoc.parse("  \n public \n class \n A implements B, C extends D\n{", publicScope).getNameLine());
  }
  
  @Test
  public void testProperty() {
    assertTrue(SfApexDoc.parse("public class A {\n PUBLIC String", publicScope).properties.isEmpty());
    assertEquals("p", SfApexDoc.parse("public class A {\n PUBLIC string p;", publicScope).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  public string  p\t=", publicScope).properties.get(0).getName());
    assertEquals("s", SfApexDoc.parse("public class A {\n public Integer s=5;", publicScope).properties.get(0).getName());
    assertEquals("s", SfApexDoc.parse("public class A {\n public Integer s = 5 ;", publicScope).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  public string  p ;", publicScope).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  static public string  p{", publicScope).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  public final string  p ;", publicScope).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  @testVisible public string p; ", publicScope).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  \n public\tstring \tp ;", publicScope).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  \n public \n string \n p; ", publicScope).properties.get(0).getName());
    assertEquals(2, SfApexDoc.parse("public class A {\n public String s; \n public String t;", publicScope).properties.size());
//TODO    assertEquals("s", SfApexDoc.parse("public class A {\n public Integer s, t;", publicScope).properties.get(0).getName());
//TODO    assertEquals(2, SfApexDoc.parse("public class A {\n public String s; public String t;", publicScope).properties.size());
    
    assertEquals("PUBLIC string p", SfApexDoc.parse("public class A {\n PUBLIC string p;", publicScope).properties.get(0).getNameLine());
  }
  
  @Test
  public void testSortProperties() {
    ClassModel m = SfApexDoc.parse("public class A {\n public string p2; \n public string p1;", publicScope);
    assertEquals(2, m.properties.size());
    assertEquals("p1", m.properties.get(0).getName());
    assertEquals("p2", m.properties.get(1).getName());
  }
  
  @Test
  public void testEnum() {
    assertEquals("e", SfApexDoc.parse("public class A {\n PUBLIC enum e;", publicScope).properties.get(0).getName());
    assertEquals("e", SfApexDoc.parse("public class A {\n  public ENUM  e{", publicScope).properties.get(0).getName());
    assertEquals("e", SfApexDoc.parse("public class A {\n  public enum  e ;", publicScope).properties.get(0).getName());
    assertEquals("e", SfApexDoc.parse("public class A {\n  static public enum  e{", publicScope).properties.get(0).getName());
    assertEquals("e", SfApexDoc.parse("public class A {\n  @testVisible public enum e ;", publicScope).properties.get(0).getName());
    assertEquals("e", SfApexDoc.parse("public class A {\n  \n public\tenum \te ;", publicScope).properties.get(0).getName());
    assertEquals("e", SfApexDoc.parse("public class A {\n  \n public \n enum \n e; ", publicScope).properties.get(0).getName());
  }
  
  @Test
  public void testMethod() {
    assertEquals("m", SfApexDoc.parse("public class A {\n PUBLIC string m ();", publicScope).methods.get(0).getName());
    assertEquals("m", SfApexDoc.parse("public class A {\n  virtual public string  m\t();", publicScope).methods.get(0).getName());
    assertEquals("m", SfApexDoc.parse("public class A {\npublic \t static string  m();", publicScope).methods.get(0).getName());
    assertEquals("m", SfApexDoc.parse("public class A {\n  @future public string m();", publicScope).methods.get(0).getName());
    assertEquals("m", SfApexDoc.parse("public class A {\n  \n public\tstring \tm ();", publicScope).methods.get(0).getName());
    assertEquals("m", SfApexDoc.parse("public class A {\n  \n public \n string \n m(); ", publicScope).methods.get(0).getName());
    
    assertEquals("PUBLIC string m ()", SfApexDoc.parse("public class A {\n PUBLIC string m ();", publicScope).methods.get(0).getNameLine());
  }
  
  @Test
  public void testSortMethods() {
    ClassModel m = SfApexDoc.parse("public class A {\n public string p2(); \n public string p1();", publicScope);
    assertEquals(2, m.methods.size());
    assertEquals("p1", m.methods.get(0).getName());
    assertEquals("p2", m.methods.get(1).getName());
  }
  
  @Test
  public void testNestedClass() {
    ClassModel m = SfApexDoc.parse("public class A {\n public class B {} \n}", publicScope);
    assertEquals("A", m.getName());
    assertEquals("A.B", m.children.get(0).getName());
    
    m = SfApexDoc.parse("public class A {\n public class B \n{} \n}", publicScope);
    assertEquals("A", m.getName());
    assertEquals("A.B", m.children.get(0).getName());
    
    m = SfApexDoc.parse("public class A {\n public class B \n{\n} \n}", publicScope);
    assertEquals("A", m.getName());
    assertEquals("A.B", m.children.get(0).getName());
    
    // ignore in-scope members of an out-of-scope nested class
    m = SfApexDoc.parse("public class A {\n private class B \n{\n public int i;\n } \n}", publicScope);
    assertEquals(0, m.children.size());
    assertEquals(0, m.properties.size());
  }
  
  @Test
  public void testNestedClassSortProperties() {
    ClassModel m = SfApexDoc.parse("public class A {\n public class B {\n public string p2; \n public string p1;\n }\n }", publicScope);
    assertEquals(2, m.children.get(0).properties.size());
    assertEquals("p1", m.children.get(0).properties.get(0).getName());
    assertEquals("p2", m.children.get(0).properties.get(1).getName());
  }
  
  @Test
  public void testNestedClassSortMethods() {
    ClassModel m = SfApexDoc.parse("public class A {\n public class B {\n public string p2(); \n public string p1();\n }\n }", publicScope);
    assertEquals(2, m.children.get(0).methods.size());
    assertEquals("p1", m.children.get(0).methods.get(0).getName());
    assertEquals("p2", m.children.get(0).methods.get(1).getName());
  }
  
  @Test
  public void testClassWithComments() {
    assertCommentAndClass("comment", "A", SfApexDoc.parse("/** comment */ \n public class A{", publicScope));
    assertCommentAndClass("comment", "A", SfApexDoc.parse("/** \n * comment \n */ \n \t\n public class A {", publicScope));
    assertCommentAndClass("comment", "A", SfApexDoc.parse("/** \n * comment \n */ \n \t\n public class A { ", publicScope));
    assertCommentAndClass("comment", "A", SfApexDoc.parse(" /** comment */ \n @future \n public \n class \n A \n{", publicScope));
  }
  
  @Test
  public void testClassWithNonDocComments() {
    assertCommentAndClass("", "A", SfApexDoc.parse("/* comment */ \n public class A{", publicScope));
    assertCommentAndClass("", "A", SfApexDoc.parse("/* \n * comment \n */ \n \t\n public class A {", publicScope));
    assertCommentAndClass("", "A", SfApexDoc.parse(" /* comment */ \n @future \n public \n class \n A \n{", publicScope));
    //TODO handle comments within a line e.g. "public /*class*/ interface A"
  }
  
  @Test
  public void testClassWithSingleLineComments() {
    assertCommentAndClass("", "A", SfApexDoc.parse("// comment \n public class A{", publicScope));
    assertCommentAndClass("", "A", SfApexDoc.parse("// \n * comment \n \t\n public class A {", publicScope));
    assertCommentAndClass("", "A", SfApexDoc.parse(" // comment \n @future \n public \n class \n A \n{", publicScope));
  }
  
  @Test
  public void testPropertyWithComments() {
    assertCommentClassAndProperty("comment", "A", "p", SfApexDoc.parse("public class A {\n /** comment */ \n public \n Id \n p \n {", publicScope));
  }
  
  @Test
  public void testPropertyWithNonDocComments() {
    assertCommentClassAndProperty("", "A", "p", SfApexDoc.parse("public class A {\n /* comment */ \n public \n Id \n p \n {", publicScope));
  }
  
  @Test
  public void testPropertyWithSingleLineComments() {
    assertCommentClassAndProperty("", "A", "p", SfApexDoc.parse("public class A {\n // comment \n public \n Id \n p \n {", publicScope));
  }

  @Test
  public void testMethodWithComments() {
    String test =
   "/**\n"+
    "* This is the parent class for models. Model classes should contain... \n"+
    "*/\n"+
      "public abstract with sharing class SF_Base {\n"+
      " //--------------------------------------------------------------------------\n"+
      " // Properties\n"+
      " /** SF Id for the current object. When changed, zObj is reset as well */\n"+
      " static\n"+
      "public Id id {\n"+
      " }\n"+
      "\n"+
      " /** The object with ID = id */\n"+
      " public SObject zObj \n"+
      "{\n"+
      " }\n"+
      " \n"+
      " //--------------------------------------------------------------------------\n"+
      " // Constructor\n"+
      " /**\n"+
      "  * @param theType required\n"+
      "  */\n"+
      " public SF_Base(SObjectType theType, Id id) {\n"+
      " }\n"+
      "}";
    ClassModel m = SfApexDoc.parse(test, publicScope);
    assertEquals(2, m.properties.size());
    assertEquals("static public Id id", m.properties.get(0).getNameLine());
    assertEquals("public SObject zObj", m.properties.get(1).getNameLine());
    Collections.sort(m.properties, new ModelComparer());
    assertEquals("id", m.properties.get(0).getName());
    assertEquals(1, m.methods.size());
  }
  
  
  //---------------------------------------------------------------------------
  // Helpers
  private void assertCommentAndClass(String comment, String theClass, ClassModel m) {
    assertNotNull(m);
    assertEquals(comment, m.getDescription());
    assertEquals(theClass, m.getName());
  }
  
  private void assertCommentClassAndProperty(String comment, String theClass, String property, ClassModel m) {
    assertNotNull(m);
    assertEquals(comment, m.properties.get(0).getDescription());
    assertEquals(theClass, m.getName());
    assertEquals(property, m.properties.get(0).getName());
  }
  
  private ArrayList<String> scopes(String[] s) {
    return new ArrayList<String>(Arrays.asList(s));
  }
}