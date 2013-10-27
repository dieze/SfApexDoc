package apex.com.main;

import java.util.ArrayList;

/**
 * Apex class and interface information
 * 
 * @author Steve Cox
 */
public class ClassModel extends Model {
  //---------------------------------------------------------------------------
  // Constants
  public static final String types = "|class|interface|";
  
  
  //---------------------------------------------------------------------------
  // Properties
  public ArrayList<MethodModel> methods = new ArrayList<MethodModel>();
  public ArrayList<PropertyModel> properties = new ArrayList<PropertyModel>();
  public final boolean isInterface;
  public final ClassModel parent;
  
  
  //---------------------------------------------------------------------------
  // Methods
  public ClassModel(ClassModel parent, String nameLine, ArrayList<String> comments) {
    super(nameLine.trim(), comments);
    isInterface = getNameLine().matches("(^|.*\\s)interface\\s+.*");
    
    final String unqualifiedName = getName();
    this.parent = parent;
    
    // add this as a 'type' we can link to
    addType(getName(), unqualifiedName + ".html");
  }
  
  public ClassModel(String nameLine, ArrayList<String> comments) {
    this(null, nameLine, comments);
  }
  
  public String getName() {
    // the name is the word after "class" or "interface"
    String[] words = getNameLine().split("\\s+");
    for (int i = 0; i < words.length; ++i) {
      if (((i + 1) < words.length) && (types.contains('|' + words[i].toLowerCase() + '|'))) {
        return (null != parent ? (parent.getName() + ".") : "") + words[i + 1].replaceAll("\\W", "");
      }
    }
    
    SfApexDoc.assertPrecondition(false); // make sure nameLine contains one of the 'types' before calling this
    return null;
  }
}