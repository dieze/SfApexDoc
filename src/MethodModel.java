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
    super("", comments);
    
    // truncate to just the signature
    int i = nameLine.indexOf('{');
    setNameLine(((i >= 0) ? nameLine.substring(0, i) : nameLine).trim());
  }
  
  public String getName() {
    // the word just before the '(' is the method name
    final String line = getNameLine();
    int end = line.indexOf('(');
    String[] words = ((end < 0) ? line : line.substring(0, end)).split("\\s+");
    return (words.length > 0) ? words[words.length - 1] : words[0];
  }
}