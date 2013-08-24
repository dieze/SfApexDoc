import java.util.ArrayList;

/**
 * Apex method information
 * 
 * @author Steve Cox
 */
public class MethodModel extends Model {
  //---------------------------------------------------------------------------
  // Methods
  public MethodModel(String nameLine, ArrayList<String> comments) {
    super(comments, null);
    assert(null != nameLine);
    
    int i = nameLine.lastIndexOf(')');
    this.nameLine = (i >= 0) ? nameLine.substring(0, i + 1) : nameLine;
  }
  
  public String getName() {
    assert(-1 != nameLine.indexOf('('));
    
    int end = nameLine.indexOf('(');
    int begin = nameLine.lastIndexOf(' ', end);
    while ((begin > 0) && nameLine.substring(begin, end).trim().isEmpty()) { // space(s) between name & paren
      begin = nameLine.lastIndexOf(' ', begin - 1);
    }
    
    return ((begin > 0) ? nameLine.substring(begin, end) : nameLine.substring(0, end)).trim();
  }
}