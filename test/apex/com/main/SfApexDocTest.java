package apex.com.main;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

import apex.com.main.SfApexDoc;

public class SfApexDocTest {
  @Test
  public void testNoClass() {
    assertNull(SfApexDoc.parse("", scopes(new String[]{})));
    assertNull(SfApexDoc.parse("", scopes(new String[]{ "public" })));
    assertNull(SfApexDoc.parse("", scopes(new String[]{ "private" })));
    assertNull(SfApexDoc.parse("public noclass A", scopes(new String[]{ "public" })));
    assertNull(SfApexDoc.parse("public classless A", scopes(new String[]{ "public" })));
    assertNull(SfApexDoc.parse("public class ", scopes(new String[]{ "public" })));
    assertNull(SfApexDoc.parse("public class // A", scopes(new String[]{ "public" })));
  }
  
  @Test
  public void testClassWithWrongScope() {
    assertNull(SfApexDoc.parse("class A", scopes(new String[]{ "private" })));
    assertNull(SfApexDoc.parse("class A", scopes(new String[]{ "public" })));
    assertNull(SfApexDoc.parse("private class A", scopes(new String[]{ "public,global,protected" })));
    assertNull(SfApexDoc.parse("public class A", scopes(new String[]{ "private,global,protected" })));
    assertNull(SfApexDoc.parse("public interface A", scopes(new String[]{ "private" })));
  }
  
  @Test
  public void testPropertyWithWrongScope() {
    assertTrue(SfApexDoc.parse("public class A {\n String s", scopes(new String[]{ "public" })).properties.isEmpty());
    assertTrue(SfApexDoc.parse("public class A {\n publicx String s", scopes(new String[]{ "public" })).properties.isEmpty());
    assertTrue(SfApexDoc.parse("public class A {\n xpublic String s", scopes(new String[]{ "public" })).properties.isEmpty());
  }
  
  @Test
  public void testMethodWithWrongScope() {
    assertTrue(SfApexDoc.parse("public class A {\n String s(", scopes(new String[]{ "public" })).properties.isEmpty());
    assertTrue(SfApexDoc.parse("public class A {\n publicx String s(", scopes(new String[]{ "public" })).properties.isEmpty());
    assertTrue(SfApexDoc.parse("public class A {\n xpublic String s(", scopes(new String[]{ "public" })).properties.isEmpty());
  }
  
  @Test
  public void testClass() {
    assertEquals("A", SfApexDoc.parse("PUBLIC interface A {", scopes(new String[]{ "public" })).getName());
    assertEquals("A", SfApexDoc.parse("  public  CLASS  A {", scopes(new String[]{ "public" })).getName());
    assertEquals("A", SfApexDoc.parse("  public class  A{ ", scopes(new String[]{ "public" })).getName());
    assertEquals("A", SfApexDoc.parse("  public class  A { ", scopes(new String[]{ "public" })).getName());
    assertEquals("A", SfApexDoc.parse("  public virtual class  A {", scopes(new String[]{ "public" })).getName());
    assertEquals("A", SfApexDoc.parse("  public abstract class  A {", scopes(new String[]{ "public" })).getName());
    assertEquals("A", SfApexDoc.parse("  public with sharing class A {", scopes(new String[]{ "public" })).getName());
    assertEquals("A", SfApexDoc.parse("  @future public without sharing class A {", scopes(new String[]{ "public" })).getName());
    assertEquals("A", SfApexDoc.parse("  public class A implements B, C extends D {", scopes(new String[]{ "public" })).getName());
    assertEquals("A", SfApexDoc.parse("  \n public\tclass \tA {", scopes(new String[]{ "public" })).getName());
    assertEquals("A", SfApexDoc.parse("  \n public \n class \n A \n{", scopes(new String[]{ "public" })).getName());
    assertEquals("A", SfApexDoc.parse("  \n public \n class \n A implements B, C extends D\n{", scopes(new String[]{ "public" })).getName());
    
    assertEquals("PUBLIC interface A", SfApexDoc.parse("PUBLIC interface A {", scopes(new String[]{ "public" })).getNameLine());
    assertEquals("public class  A", SfApexDoc.parse("  \n public\tclass \tA {", scopes(new String[]{ "public" })).getNameLine());
    assertEquals("public class A", SfApexDoc.parse("  \n public \n class \n A \n{", scopes(new String[]{ "public" })).getNameLine());
    assertEquals("public class A implements B, C extends D", SfApexDoc.parse("  \n public \n class \n A implements B, C extends D\n{", scopes(new String[]{ "public" })).getNameLine());
  }
  
  public void testProperty() {
    assertTrue(SfApexDoc.parse("public class A {\n PUBLIC String", scopes(new String[]{ "public" })).properties.isEmpty());
    assertEquals("p", SfApexDoc.parse("public class A {\n PUBLIC string p", scopes(new String[]{ "public" })).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  public string  p\t=", scopes(new String[]{ "public" })).properties.get(0).getName());
    assertEquals("s", SfApexDoc.parse("public class A {\n public Integer s=5;", scopes(new String[]{ "public" })).properties.get(0).getName());
    assertEquals("s", SfApexDoc.parse("public class A {\n public Integer s = 5 ;", scopes(new String[]{ "public" })).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  public string  p ;", scopes(new String[]{ "public" })).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  static public string  p{", scopes(new String[]{ "public" })).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  public final string  p ", scopes(new String[]{ "public" })).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  @testVisible public string p ", scopes(new String[]{ "public" })).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  \n public\tstring \tp ", scopes(new String[]{ "public" })).properties.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  \n public \n string \n p; ", scopes(new String[]{ "public" })).properties.get(0).getName());
    assertEquals(2, SfApexDoc.parse("public class A {\n public String s; \n public String t;", scopes(new String[]{ "public" })).properties.size());
    // TODO assertEquals("s", SfApexDoc.parse("public class A {\n public Integer s, t;", scopes(new String[]{ "public" })).properties.get(0).getName());
    // TODO assertEquals(2, SfApexDoc.parse("public class A {\n public String s; public String t;", scopes(new String[]{ "public" })).properties.size());
    
    assertEquals("PUBLIC string p", SfApexDoc.parse("public class A {\n PUBLIC string p", scopes(new String[]{ "public" })).properties.get(0).getNameLine());
    assertEquals("public string  p", SfApexDoc.parse("public class A {\n  \n public\tstring \tp ", scopes(new String[]{ "public" })).properties.get(0).getName());
    assertEquals("public string p", SfApexDoc.parse("public class A {\n  \n public \n string \n p; ", scopes(new String[]{ "public" })).properties.get(0).getName());
  }
  
  public void testEnum() {
    assertEquals("e", SfApexDoc.parse("public class A {\n PUBLIC enum e", scopes(new String[]{ "public" })).properties.get(0).getName());
    assertEquals("e", SfApexDoc.parse("public class A {\n  public ENUM  e{", scopes(new String[]{ "public" })).properties.get(0).getName());
    assertEquals("e", SfApexDoc.parse("public class A {\n  public enum  e ", scopes(new String[]{ "public" })).properties.get(0).getName());
    assertEquals("e", SfApexDoc.parse("public class A {\n  static public enum  e{", scopes(new String[]{ "public" })).properties.get(0).getName());
    assertEquals("e", SfApexDoc.parse("public class A {\n  @testVisible public enum e ", scopes(new String[]{ "public" })).properties.get(0).getName());
    assertEquals("e", SfApexDoc.parse("public class A {\n  \n public\tenum \te ", scopes(new String[]{ "public" })).properties.get(0).getName());
    assertEquals("e", SfApexDoc.parse("public class A {\n  \n public \n enum \n e; ", scopes(new String[]{ "public" })).properties.get(0).getName());
  }
  
  public void testMethod() {
    assertEquals("p", SfApexDoc.parse("public class A {\n PUBLIC string m (", scopes(new String[]{ "public" })).methods.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  virtual public string  m\t(", scopes(new String[]{ "public" })).methods.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  public \t static string  m(", scopes(new String[]{ "public" })).methods.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  @future public string m(", scopes(new String[]{ "public" })).methods.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  \n public\tstring \tm (", scopes(new String[]{ "public" })).methods.get(0).getName());
    assertEquals("p", SfApexDoc.parse("public class A {\n  \n public \n string \n m; ", scopes(new String[]{ "public" })).methods.get(0).getName());
    
    assertEquals("PUBLIC string m", SfApexDoc.parse("public class A {\n PUBLIC string m (", scopes(new String[]{ "public" })).methods.get(0).getNameLine());
    assertEquals(" public string m", SfApexDoc.parse("public class A {\n  \n public\tstring \tm (", scopes(new String[]{ "public" })).methods.get(0).getName());
    assertEquals("public string m", SfApexDoc.parse("public class A {\n  \n public \n string \n m; ", scopes(new String[]{ "public" })).methods.get(0).getName());
  }
  
  @Test
  public void testNestedClass() {
    // TODO
  }

  @Test
  public void testClassWithComments() {
    assertCommentAndClass(SfApexDoc.parse("/** comment */ \n public class A{", scopes(new String[]{ "public" })));
    assertCommentAndClass(SfApexDoc.parse("/** \n * comment \n */ \n \t\n public class A {", scopes(new String[]{ "public" })));
    assertCommentAndClass(SfApexDoc.parse("/** \n * comment \n */ \n \t\n public class A { ", scopes(new String[]{ "public" })));
    assertCommentAndClass(SfApexDoc.parse(" /** comment */ \n @future \n public \n class \n A \n{", scopes(new String[]{ "public" })));
  }
  
  @Test
  public void testPropertyWithComments() {
    assertCommentAndProperty(SfApexDoc.parse("public class A {\n /** comment */ \n public \n Id \n p \n {", scopes(new String[]{ "public" })));
  }

  @Test
  public void testMethodWithComments() {
    String test = "/**\n"+
    "* This is the parent class for models. Model classes should contain... \n"+
    "*/\n"+
      "public abstract with sharing class SF_Base {\n"+
      " //--------------------------------------------------------------------------\n"+
      " // Properties\n"+
      " /** SF Id for the current object. When changed, aObj is reset as well */\n"+
      " static\n"+
      "public Id id {\n"+
      " }\n"+
      "\n"+
      " /** The object with ID = id */\n"+
      " public SObject aObj \n"+
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
    ClassModel m = SfApexDoc.parse(test, scopes(new String[]{ "public" }));
    assertEquals(2, m.properties.size());
    assertEquals("static public Id id", m.properties.get(0).getNameLine());
    assertEquals("public SObject aObj", m.properties.get(1).getNameLine());
    Collections.sort(m.properties, new ModelComparer());
    assertEquals("aObj", m.properties.get(0).getName());
    assertEquals(1, m.methods.size());
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
  
  private void assertCommentAndProperty(ClassModel m) {
    assertNotNull(m);
    assertEquals("A", m.getName());
    assertEquals("comment", m.properties.get(0).getDescription());
    assertEquals("p", m.properties.get(0).getName());
  }
  
  private ArrayList<String> scopes(String[] s) {
    return new ArrayList<String>(Arrays.asList(s));
  }
}