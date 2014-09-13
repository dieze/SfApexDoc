package apex.com.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class used for all entity types (class, interface, method, property, etc.)
 * <p>
 * <pre>
 * void foo() {
 *    printf("Hello");
 * }
 * </pre>
 *
 *
 * @author Steve Cox
 */
public class Model {
  //---------------------------------------------------------------------------
  // Constants
  private static final String PRIMITIVES_LINK = "http://www.salesforce.com/us/developer/docs/apexcode/Content/langCon_apex_primitives.htm";
  private static final String SOBJECT_LINK = "http://www.salesforce.com/us/developer/docs/apexcode/Content/apex_dynamic_describe_objects_understanding.htm";
  private static final Map<String,String> typeLinks = new HashMap<String,String>();
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
    typeLinks.put("sobjecttype", SOBJECT_LINK);
    typeLinks.put("sobjectfield", SOBJECT_LINK);
    typeLinks.put("schedulable", "http://www.salesforce.com/us/developer/docs/apexcode/Content/apex_scheduler.htm");
    typeLinks.put("database.batchable", "http://www.salesforce.com/us/developer/docs/apexcode/Content/apex_batch_interface.htm");
  };
  
  // Supported tags
  private static final String DESC   = "@description";
  private static final String AUTH   = "@author";
  private static final String DATE   = "@date";
  private static final String SEE    = "@see";
  private static final String RET    = "@return";
  private static final String PARM   = "@param";
  private static final String THROWS = "@throws";
  private static final String SINCE  = "@since";
  private static final String VERSION = "@version";
  private static final String HISTORY = "@history";
  
  //---------------------------------------------------------------------------
  // Properties
  private String nameLine = "";
  private String description = "";
  private String author = "";
  private String date = "";
  private String returns = "";
  private String see = "";
  private String since = "";
  private String version = "";
  private String history = "";
  private ArrayList<String> params = new ArrayList<String>();
  private ArrayList<String> except = new ArrayList<String>();
  
  private boolean linksAdded = false;
  
  
  //---------------------------------------------------------------------------
  // Methods
  /**
   * Construct our model using a list of comments and the 'nameLine'
   * @author Steve Cox
   */
  protected Model(String nameLine, ArrayList<String> comments) {
    SfApexDoc.assertPrecondition(null != comments);
    
    setNameLine(nameLine);
    parseComments(comments);
  }
  
  /** Override this method to provide the name of your model */
  public String getName() {
    SfApexDoc.assertPrecondition(false);
    return null;
  }
  
  public String getNameLine() {
    return nameLine;
  }
  
  public void setNameLine(String nameLine) {
    SfApexDoc.assertPrecondition(null != nameLine);
    SfApexDoc.assertPrecondition(nameLine.isEmpty() ||
      (!Character.isWhitespace(nameLine.charAt(0)) &&
      !Character.isWhitespace(nameLine.charAt(nameLine.length()-1))));
    
    this.nameLine = nameLine;
  }
  
  public String getDescription() {
    return description;
  }
  
  public String getAuthor() {
    return author;
  }
  
  public String getDate() {
    return date;
  }
  
  public String getReturns() {
    return returns;
  }
  
  public String getSee() {
    return see;
  }
  
  public String getSince() {
	  return since;
  }
  
  public String getVersion() {
	  return version;
  }
  
  public String getHistory() {
	  return history;
  }
  
  public ArrayList<String> getParams() {
    return params;
  }
  
  public ArrayList<String> getThrows() {
    return except;
  }
  
  public String getThrowsAsString() {
    String result = "";
    for (String x : except) {
      result += "<br/>throws " + x;
    }
    
    return result;
  }
  
  /** HTML encode and add type links to the model */
  public void addLinks() {
    SfApexDoc.assertPrecondition(!linksAdded);
    
    setNameLine(addLinks(nameLine));
    see = addLinks(see);
    for (Integer i = 0; i < except.size(); ++i) {
      except.set(i, addLinks(except.get(i)));
    }
    
    linksAdded = true;
  }
  
  protected void addType(String name, String link) {
    typeLinks.put(name.toLowerCase(), link.toLowerCase());
  }
  
  
  //---------------------------------------------------------------------------
  // Helpers
  private String addLinks(String text) {
    String result = text;
    if (!result.isEmpty()) {
      // HTML-encode the string
      result = result.replace("<", "&lt;").replace(">", "&gt;");
      
      // replace words with their links, if we know them.
      // splitting into words breaks on '.', so replace them temporarily, so that
      // we can process class.nestedClass names
      for (String w : result.replace(".","___").split("[^\\w]")) {
        w = w.replace("___",".");
        String link = typeLinks.get(w.toLowerCase());
        if (null != link) {
          link = "<a href='" + link + "'>" + w + "</a>";
          result = result.replaceAll("\\b" + w + "\\b", link);
        }
      }
    }
    
    return result;
  }
  
  private void parseComments(ArrayList<String> comments) {
    String curBlock=null, block=null;
    boolean inPre=false;
    for (String comment : comments) {
      boolean newBlock = false;
      String lowerComment = comment.toLowerCase();
      int i;
      // if we find a tag, start a new block
      if (((i = lowerComment.indexOf(block = DESC)) >= 0) ||
        ((i = lowerComment.indexOf(block = AUTH)) >= 0) ||
        ((i = lowerComment.indexOf(block = DATE)) >= 0) ||
        ((i = lowerComment.indexOf(block = SEE)) >= 0) ||
        ((i = lowerComment.indexOf(block = SINCE)) >= 0) ||
        ((i = lowerComment.indexOf(block = VERSION)) >= 0) ||
        ((i = lowerComment.indexOf(block = RET)) >= 0) ||
        ((i = lowerComment.indexOf(block = PARM)) >= 0) ||
        ((i = lowerComment.indexOf(block = HISTORY)) >= 0) ||
        ((i = lowerComment.indexOf(block = THROWS)) >= 0)) {
        
        comment = comment.substring(i + block.length());
        curBlock = block;
        newBlock = true;
      }
      
      String line = "";
      comment = comment.trim();
      for (int j = 0; j < comment.length(); ++j) {
        char ch = comment.charAt(j);
        if (ch != '*') {
          line = comment.substring(j);
          break;
        }
      }
      
      if (!line.trim().isEmpty()) {
        if (AUTH == curBlock) {
          author += (!author.isEmpty() ? " " : "") + line.trim();
        } else if (DATE == curBlock) {
          date += (!date.isEmpty() ? " " : "") + line.trim();
        } else if (SEE == curBlock) {
          see += (!see.isEmpty() ? " " : "") + line.trim();
        } else if (SINCE == curBlock) {
          since += (!since.isEmpty() ? " " : "") + line.trim();
        } else if (VERSION == curBlock) {
          version += (!version.isEmpty() ? " " : "") + line.trim();
        } else if (RET == curBlock) {
          returns += (!returns.isEmpty() ? " " : "") + line.trim();
        } else if (PARM == curBlock) {
          String p = (newBlock ? "" : params.remove(params.size()-1));
          params.add(p + (!p.isEmpty() ? " " : "") + line.trim());
        } else if (THROWS == curBlock) {
          String p = (newBlock ? "" : except.remove(except.size()-1));
          except.add(p + (!p.isEmpty() ? " " : "") + line.trim());
        } else if (HISTORY == curBlock) {
              history += (!history.isEmpty() ? "\n" : "") + line.trim();
        } else {
          // not in a recognized tag - assume it's the description
          curBlock = block = DESC;
          
          // are we in a <pre> or <code> tag?
          if (inPre) {
            description += "\n" + line;
          } else {
            inPre = ((line.indexOf("<pre") >= 0) || (line.indexOf("<code") >= 0));
            description += (!description.isEmpty() ? " " : "") +
              (inPre ? line : line.trim());
          }
          
          if (inPre) {
            inPre = !((line.indexOf("</pre>") >= 0) || (line.indexOf("</code>") >= 0));
          }
        }
      }
    }
  }
}