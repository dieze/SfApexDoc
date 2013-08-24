import java.util.ArrayList;

/**
 * Apex class and interface information
 * 
 * @author Steve Cox
 */
public class ClassModel extends Model {
  //---------------------------------------------------------------------------
  // Constants
  public static final String types = "class|interface";
  
  
  //---------------------------------------------------------------------------
  // Properties
  public ArrayList<MethodModel> methods = new ArrayList<MethodModel>();
  public ArrayList<PropertyModel> properties = new ArrayList<PropertyModel>();
  public final boolean isInterface;
  
  
  //---------------------------------------------------------------------------
  // Methods
  public ClassModel(String nameLine, ArrayList<String> comments) {
    super(comments, nameLine);
    assert(null != nameLine);
    
    String name = getName().toLowerCase();
    typeLinks.put(name, name + ".html");
    
    isInterface = nameLine.matches("(^|.*\\s)interface\\s+.*");
  }
  
  public String getName() {
    for (String t : types.split("\\|")) {
      t += ' ';
      int start = nameLine.indexOf(t);
      if (start >= 0) {
        start += t.length();
        int i = nameLine.indexOf(' ', start);
        return ((i > 0) ? nameLine.substring(start, i) : nameLine.substring(start)).trim();
      }
    }
    
    return "";
  }
}