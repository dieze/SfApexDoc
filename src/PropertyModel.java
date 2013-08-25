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
    super(comments, "");
    
    // remove any trailing stuff after property name
    int i = nameLine.indexOf('{');
    if (i < 0) i = nameLine.indexOf('=');
    if (i < 0) i = nameLine.indexOf(';');
    if (i >= 0) nameLine = nameLine.substring(0, i);          
    setNameLine(nameLine.trim());
  }
  
  public String getName() {
    final String line = getNameLine();
    int i = line.lastIndexOf(' ');
    return (i >= 0) ? line.substring(i + 1) : line;
  }
}