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
  
  
  //---------------------------------------------------------------------------
  // Methods
  public ClassModel(String nameLine, ArrayList<String> comments) {
    super(nameLine.trim(), comments);
    
    // add this as a 'type' we can link to
    String name = getName();
    addType(name, name + ".html");
    
    isInterface = getNameLine().matches("(^|.*\\s)interface\\s+.*");
  }
  
  public String getName() {
    // the name is the word after "class" or "interface"
    String[] words = getNameLine().split("\\s+");
    for (int i = 0; i < words.length; ++i) {
      if (((i + 1) < words.length) && (types.contains('|' + words[i].toLowerCase() + '|'))) {
        return words[i + 1].replaceAll("\\W", "");
      }
    }
    
    SfApexDoc.assertPrecondition(false); // make sure nameLine contains one of the 'types' before calling this
    return null;
  }
}