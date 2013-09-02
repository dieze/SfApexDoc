package apex.com.main;

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
    super(nameLine.trim(), comments);
  }
  
  public String getName() {
    final String line = getNameLine();
    int i = line.lastIndexOf(' ');
    return (i >= 0) ? line.substring(i + 1) : line;
  }
}