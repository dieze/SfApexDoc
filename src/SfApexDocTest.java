import static org.junit.Assert.*;

import org.junit.Test;

public class SfApexDocTest {
  @Test
  public void testNoClass() {
    assertNull(SfApexDoc.parse("", new String[]{}));
    assertNull(SfApexDoc.parse("", new String[]{ "public" }));
    assertNull(SfApexDoc.parse("", new String[]{ "private" }));
    assertNull(SfApexDoc.parse("public noclass A", new String[]{ "public" }));
    assertNull(SfApexDoc.parse("public classless A", new String[]{ "public" }));
    assertNull(SfApexDoc.parse("public class ", new String[]{ "public" }));
    assertNull(SfApexDoc.parse("public class // A", new String[]{ "public" }));
  }
  
  @Test
  public void testClassWithWrongScope() {
    assertNull(SfApexDoc.parse("class A", new String[]{ "private" }));
    assertNull(SfApexDoc.parse("class A", new String[]{ "public" }));
    assertNull(SfApexDoc.parse("private class A", new String[]{ "public,global,protected" }));
    assertNull(SfApexDoc.parse("public class A", new String[]{ "private,global,protected" }));
    assertNull(SfApexDoc.parse("public interface A", new String[]{ "private" }));
  }
  
  @Test
  public void testPropertyWithWrongScope() {
    assertTrue(SfApexDoc.parse("public class A {\n String s", new String[]{ "public" }).properties.isEmpty());
    assertTrue(SfApexDoc.parse("public class A {\n publicx String s", new String[]{ "public" }).properties.isEmpty());
    assertTrue(SfApexDoc.parse("public class A {\n xpublic String s", new String[]{ "public" }).properties.isEmpty());
  }
  
  @Test
  public void testMethodWithWrongScope() {
    assertTrue(SfApexDoc.parse("public class A {\n String s(", new String[]{ "public" }).properties.isEmpty());
    assertTrue(SfApexDoc.parse("public class A {\n publicx String s(", new String[]{ "public" }).properties.isEmpty());
    assertTrue(SfApexDoc.parse("public class A {\n xpublic String s(", new String[]{ "public" }).properties.isEmpty());
  }
  
  @Test
  public void testClass() {
    assertEquals("A", SfApexDoc.parse("PUBLIC interface A", new String[]{ "public" }).getName());
    assertEquals("A", SfApexDoc.parse("  public  CLASS  A ", new String[]{ "public" }).getName());
    assertEquals("A", SfApexDoc.parse("  public class  A{ ", new String[]{ "public" }).getName());
    assertEquals("A", SfApexDoc.parse("  public class  A { ", new String[]{ "public" }).getName());
    assertEquals("A", SfApexDoc.parse("  public virtual class  A ", new String[]{ "public" }).getName());
    assertEquals("A", SfApexDoc.parse("  public abstract class  A ", new String[]{ "public" }).getName());
    assertEquals("A", SfApexDoc.parse("  public with sharing class A ", new String[]{ "public" }).getName());
    assertEquals("A", SfApexDoc.parse("  @future public without sharing class A ", new String[]{ "public" }).getName());
    assertEquals("A", SfApexDoc.parse("  public class A implements B, C extends D ", new String[]{ "public" }).getName());
    assertEquals("A", SfApexDoc.parse("  \n public\tclass \tA ", new String[]{ "public" }).getName());
    // TODO assertEquals("A", SfApexDoc.parse("  \n public \n class \n A ", new String[]{ "public" }).getName());
  }
  
  public void testProperty() {
    assertEquals("p", SfApexDoc.parse("public class A {\n PUBLIC string p", new String[]{ "public" }).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  public string  p\t=", new String[]{ "public" }).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  public string  p ;", new String[]{ "public" }).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  static public string  p{", new String[]{ "public" }).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  public final string  p ", new String[]{ "public" }).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  @testVisible public string p ", new String[]{ "public" }).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  \n public\tstring \tp ", new String[]{ "public" }).properties.get(0).getName());
    // TODO assertEquals("p", SfApexDoc.parse("public class A {\n  \n public \n string \n p; ", new String[]{ "public" }).properties.get(0).getName());
  }
  
  public void testEnum() {
    assertEquals("e", SfApexDoc.parse("public class A {\n PUBLIC enum e", new String[]{ "public" }).properties.get(0).getName());
    assertEquals("e", SfApexDoc.parse("public class A {\n  public ENUM  e{", new String[]{ "public" }).properties.get(0).getName());
    assertEquals("e", SfApexDoc.parse("public class A {\n  public enum  e ", new String[]{ "public" }).properties.get(0).getName());
    assertEquals("e", SfApexDoc.parse("public class A {\n  static public enum  e{", new String[]{ "public" }).properties.get(0).getName());
    assertEquals("e", SfApexDoc.parse("public class A {\n  @testVisible public enum e ", new String[]{ "public" }).properties.get(0).getName());
    assertEquals("e", SfApexDoc.parse("public class A {\n  \n public\tenum \te ", new String[]{ "public" }).properties.get(0).getName());
    // TODO assertEquals("e", SfApexDoc.parse("public class A {\n  \n public \n enum \n e; ", new String[]{ "public" }).properties.get(0).getName());
  }
  
  public void testMethod() {
    assertEquals("p", SfApexDoc.parse("public class A {\n PUBLIC string m (", new String[]{ "public" }).methods.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  virtual public string  m\t(", new String[]{ "public" }).methods.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  public \t static string  m(", new String[]{ "public" }).methods.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  @future public string m(", new String[]{ "public" }).methods.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  \n public\tstring \tm (", new String[]{ "public" }).methods.get(0).getName());
    // TODO assertEquals("p", SfApexDoc.parse("public class A {\n  \n public \n string \n m; ", new String[]{ "public" }).methods.get(0).getName());
  }
  
  @Test
  public void testClassWithProperties() {
    // TODO assertTrue(SfApexDoc.parse("public class A {\n PUBLIC String", new String[]{ "public" }).properties.isEmpty());
    assertEquals("s", SfApexDoc.parse("public class A {\n PUBLIC String s", new String[]{ "public" }).properties.get(0).getName());
    assertEquals("s", SfApexDoc.parse("public class A {\n public String s;", new String[]{ "public" }).properties.get(0).getName());
    assertEquals("s", SfApexDoc.parse("public class A {\n public Integer s=5;", new String[]{ "public" }).properties.get(0).getName());
    // TODO assertEquals("s", SfApexDoc.parse("public class A {\n public Integer s, t;", new String[]{ "public" }).properties.get(0).getName());
    // TODO assertEquals(2, SfApexDoc.parse("public class A {\n public String s; public String t;", new String[]{ "public" }).properties.size());
    assertEquals(2, SfApexDoc.parse("public class A {\n public String s; \n public String t;", new String[]{ "public" }).properties.size());
  }
  
  @Test
  public void testClassWithMethods() {
    // TODO
  }

  @Test
  public void testNestedClass() {
    // TODO
  }

  @Test
  public void testClassWithComments() {
    assertCommentAndClass(SfApexDoc.parse("/** comment */ \n public class A", new String[]{ "public" }));
    assertCommentAndClass(SfApexDoc.parse("/** \n * comment \n */ \n \t\n public class A ", new String[]{ "public" }));
    assertCommentAndClass(SfApexDoc.parse("/** \n * comment \n */ \n \t\n public class A ", new String[]{ "public" }));
    // TODO assertEquals("A", SfApexDoc.parse(" /** comment */ \n @future \n public \n class \n A ", new String[]{ "public" }).getName());
  }
  
  @Test
  public void testPropertyWithComments() {
    // TODO
  }

  @Test
  public void testMethodWithComments() {
    // TODO
  }

  @Test
  public void testNestedClassWithComments() {
    // TODO
  }

  private void assertCommentAndClass(ClassModel m) {
    assertNotNull(m);
    assertEquals("A", m.getName());
    assertEquals("comment", m.getDescription());
  }
}