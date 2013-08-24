import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class used for all entity types (class, interface, method, property, etc.)
 * 
 * @author Steve Cox
 */
public abstract class Model {
  //---------------------------------------------------------------------------
  // Properties
  public String nameLine = "";
  public String description = "";
  public String author = "";
  public String date = "";
  public String returns = "";
  public String see = "";
  public ArrayList<String> params = new ArrayList<String>();
  
  private static final String PRIMITIVES_LINK = "http://www.salesforce.com/us/developer/docs/apexcode/Content/langCon_apex_primitives.htm";
  private static final String SOBJECT_LINK = "http://www.salesforce.com/us/developer/docs/apexcode/Content/apex_dynamic_describe_objects_understanding.htm";
  
  public static Map<String,String> typeLinks = new HashMap<String,String>();
  static {
    typeLinks.put("blob", PRIMITIVES_LINK);
    typeLinks.put("boolean", PRIMITIVES_LINK);
    typeLinks.put("date", PRIMITIVES_LINK);
    typeLinks.put("datetime", PRIMITIVES_LINK);
    typeLinks.put("decimal", PRIMITIVES_LINK);
    typeLinks.put("double", PRIMITIVES_LINK);
    typeLinks.put("id", PRIMITIVES_LINK);
    typeLinks.put("integer", PRIMITIVES_LINK);
    typeLinks.put("long", PRIMITIVES_LINK);
    typeLinks.put("string", PRIMITIVES_LINK);
    typeLinks.put("time", PRIMITIVES_LINK);
    typeLinks.put("sobject", PRIMITIVES_LINK);
    typeLinks.put("enum", PRIMITIVES_LINK);
    typeLinks.put("type", "http://www.salesforce.com/us/developer/docs/apexcode/Content/apex_methods_system_type.htm");
    typeLinks.put("sobjecttype", SOBJECT_LINK);
    typeLinks.put("sobjectfield", SOBJECT_LINK);
  };
  
  
  //---------------------------------------------------------------------------
  // Methods
  /**
   * Construct our model using a list of comments and the 'nameLine'
   * @author Steve Cox
   */
  protected Model(ArrayList<String> comments, String nameLine) {
    assert(null != comments);
    
    this.nameLine = nameLine;

    String curBlock=null, block=null;
    for (String comment : comments) {
      comment = comment.trim();
      String lowerComment = comment.toLowerCase();
      int i;
      // if we find a tag, start a new block
      if ((i = lowerComment.indexOf(block = SfApexDoc.DESC)) >= 0) {
        description = comment.substring(i + block.length()).trim();
        curBlock = block;
      } else if ((i = lowerComment.indexOf(block = SfApexDoc.AUTH)) >= 0) {
        author = comment.substring(i + block.length()).trim();
        curBlock = block;
      } else if ((i = lowerComment.indexOf(block = SfApexDoc.DATE)) >= 0) {
        date = comment.substring(i + block.length()).trim();
        curBlock = block;
      } else if ((i = lowerComment.indexOf(block = SfApexDoc.SEE)) >= 0) {
        see = comment.substring(i + block.length()).trim();
        curBlock = block;
      } else if ((i = lowerComment.indexOf(block = SfApexDoc.RET)) >= 0) {
        returns = comment.substring(i + block.length()).trim();
        curBlock = block;
      } else if ((i = lowerComment.indexOf(block = SfApexDoc.PARM)) >= 0) {
        params.add(comment.substring(i + block.length()).trim());
        curBlock = block;
      // no tag - treat this as an additional line in the block 
      } else if (SfApexDoc.DESC == curBlock) {
        description += ' ' + getCommentLine(comment);
      } else if (SfApexDoc.AUTH == curBlock) {
        author += ' ' + getCommentLine(comment);
      } else if (SfApexDoc.DATE == curBlock) {
        date += ' ' + getCommentLine(comment);
      } else if (SfApexDoc.SEE == curBlock) {
        see += ' ' + getCommentLine(comment);
      } else if (SfApexDoc.RET == curBlock) {
        returns += ' ' + getCommentLine(comment);
      } else if (SfApexDoc.PARM == curBlock) {
        String p = params.remove(params.size()-1);
        params.add(p + ' ' + getCommentLine(comment));
      // not in a recognized tag - assume it's the description
      } else {
        curBlock = block = SfApexDoc.DESC;
        description = getCommentLine(comment);
      }
    }
  }
  
  /** Override this method to provide the name of your model */
  public abstract String getName();
  
  /** HTML encode and add type links to the model */
  public void addLinks() {
    nameLine = addLinks(nameLine);
    see = addLinks(see);
  }
  
  
  //---------------------------------------------------------------------------
  // Helpers
  private String addLinks(String text) {
    String result = text;
    if (!result.isEmpty()) {
      // HTML-encode the string
      result = result.replace("<", "&lt;").replace(">", "&gt;");
      
      // replace words with their links, if we know them
      for (String w : result.split("[^\\w]")) {
        String link = typeLinks.get(w.toLowerCase());
        if (null != link) {
          link = "<a href='" + link + "'>" + w + "</a>";
          result = result.replaceAll("\\b" + w + "\\b", link);
        }
      }
    }
    
    return result;
  }
  
  private static String getCommentLine(String comment) {
    for (int i = 0; i < comment.length(); i++) {
      char ch = comment.charAt(i);
      if (ch != '*' && !Character.isWhitespace(ch)) {
        return comment.substring(i);
      }
    }
    return "";
  }
}