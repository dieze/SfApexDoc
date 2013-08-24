import java.util.ArrayList;

/**
 * Apex property and enum information
 * 
 * @author Steve Cox
 */
public class PropertyModel extends Model {
  //---------------------------------------------------------------------------
  // Methods
  public PropertyModel(String nameLine, ArrayList<String> comments) {
    super(comments, null);
    assert(null != nameLine);
    
    // remove any trailing stuff after property name
    int i = nameLine.indexOf('{');
    if (i < 0) i = nameLine.indexOf('=');
    if (i < 0) i = nameLine.indexOf(';');
    if (i >= 0) nameLine = nameLine.substring(0, i);          
    this.nameLine = nameLine.trim();
  }
  
  public String getName() {
    int i = nameLine.lastIndexOf(' ');
    return (i >= 0) ? nameLine.substring(i + 1) : "";
  }
}