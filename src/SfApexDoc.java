import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Similar to ApexDoc, but the Eclipse plugin capabilities have been removed.
 *
 * @author Steve Cox
 */
public class SfApexDoc {
  public static void assertPrecondition(boolean condition) {
    if (!condition) {
      throw new NullPointerException();
    }
  }
  
  //---------------------------------------------------------------------------
  // Constants
  private static final String VERSION = "1.0.1";
  private static final String LOG_FILE_NAME = "SfApexDocLog.txt";
  private static final String DEFAULT_EXT = "cls";
  
  private static final String[] DEFAULT_SCOPE = new String[] { "global", "public" };
  private static final String SCOPE_SEP = ",";
  
  private static final String COMMENT_START = "/**";
  private static final String COMMENT_END = "*/";
  private static final String DEF_VISIBILITY = "private";
  
  // Supported tags
  public static final String DESC = "@description";
  public static final String AUTH = "@author";
  public static final String DATE = "@date";
  public static final String RET  = "@return";
  public static final String PARM = "@param";
  public static final String SEE  = "@see";
  
  
  //---------------------------------------------------------------------------
  // Properties
  private static PrintStream logFile;
  private static boolean debugOutput = false;

  
  //---------------------------------------------------------------------------
  // Methods
  /**
   * Entry point for SfApexDoc. Invoke this from a command line 
   * interface, ANT script, etc. Parameters are documented in the 'syntaxError'
   * method below.
   */
  public static void main(String[] args) {
    // create a log file
    try {
      logFile = new PrintStream(new FileOutputStream(new File(LOG_FILE_NAME)));
    } catch (Exception e) {
      System.err.println("Failed to create log file: " + e.getMessage());
    }
    
    // parse command line parameters
    String sourceDir="", destDir="", homeFile="", authorFile="", ext=DEFAULT_EXT;
    try {
      String[] scope = DEFAULT_SCOPE;
      for (int i = 0; i < args.length; ++i) {
        String argKey = args[i].substring(0, 2).toLowerCase();
        
        if ("-s".equals(argKey)) sourceDir = args[++i];
        else if ("-t".equals(argKey)) destDir = args[++i];
        else if ("-h".equals(argKey)) homeFile = args[++i];
        else if ("-a".equals(argKey)) authorFile = args[++i];
        else if ("-p".equals(argKey)) scope = args[++i].toLowerCase().split(SCOPE_SEP);
        else if ("-x".equals(argKey)) ext = args[++i];
        else if ("-d".equals(argKey)) debugOutput = true;
        else if ("-v".equals(argKey)) bail("SfApexDoc version " + VERSION);
        else syntaxError("Invalid option: " + argKey);
      }
      
      // make sure the source folder is valid
      File sourceFolder = new File(sourceDir);
      if (!sourceFolder.exists() || !sourceFolder.isDirectory()) {
        bail("Invalid source folder: " + sourceDir);
      }
      
      // get the list of files to process
      ArrayList<ClassModel> models = new ArrayList<ClassModel>();
      for (File f : sourceFolder.listFiles()) {
        if (f.isFile() && f.getAbsolutePath().toLowerCase().endsWith("." + ext)) {
          models.add(parseFileContents(f.getAbsolutePath(), scope));
        }
      }
      
      // Create the documentation
      FileManager m = new FileManager(destDir);
      m.createDocumentation(models, m.parseProjectDetail(authorFile), m.parseHtmlFile(homeFile));
    } catch (Exception e) {
      log(e);
      syntaxError(null);
    }
  }
  
  /** log the specified exception to the screen and a file */
  public static void log(Exception e) {
    e.printStackTrace();
    log(e.getMessage());
  }
  
  /** log the specified message to the screen and a file */
  public static void log(String message) {
    if (null != message) {
      System.out.println("  " + message);
      if (null != logFile) {
        logFile.println(message);
      }
    }
  }
  
  
  //---------------------------------------------------------------------------
  // Helpers
  // Parse the specified file; see inline comments for specific rules
  private static ClassModel parseFileContents(String filePath, String[] scope) {
    SfApexDoc.assertPrecondition(null != filePath);
    SfApexDoc.assertPrecondition(!filePath.isEmpty());
    SfApexDoc.assertPrecondition(null != scope);
    
    ClassModel model = null;
    String line = "";
    try {
      boolean commentsStarted = false;
      ArrayList<String> comments = new ArrayList<String>();
       
      InputStreamReader in = new InputStreamReader(new DataInputStream(new FileInputStream(filePath)));
      BufferedReader reader = new BufferedReader(in);
      while (null != (line = reader.readLine())) {
        line = line.trim().replaceAll("\t", " ");
        if (0 == line.length()) continue;
        
        // ignore anything after // style comments. This allows hiding of tokens from ApexDoc
        int i = line.indexOf("//");
        if (i > -1) {
          // ignore segments that could be links (i.e. http://www...)
          if ((0 == i) || (':' != line.charAt(i - 1))) {
            line = line.substring(0, i);
          }
        }
        
        // gather up our comments
        if (line.startsWith(COMMENT_START)) {
          comments.clear();
          // check for single-line block comment
          if (line.endsWith(COMMENT_END)) {
            comments.add(line.substring(COMMENT_START.length(), line.length() - COMMENT_END.length()));
          } else {
            commentsStarted = true;
          }
        } else if (commentsStarted && line.endsWith(COMMENT_END)) {
          commentsStarted = false;
        } else if (commentsStarted) {
          comments.add(line);
        } else {
          // ignore anything after an '=' or '{'; this avoids confusing properties with methods
          if ((i = line.indexOf('=')) > -1) line = line.substring(0, i);
          if ((i = line.indexOf('{')) > -1) line = line.substring(0, i);
          
          boolean hasScope = lineContainsScope(line, scope);
          if (line.toLowerCase().matches("(^|.*\\s)(" + ClassModel.types + ")\\s+.*")) {
            if (null == model) {
              // top-level class
              if (!hasScope) break;  // must be a test class - skip it
              model = new ClassModel(line, comments);
              comments.clear();
            } else if (hasScope || lineContainsScope(DEF_VISIBILITY, scope)) {
              // nested class
              comments.clear();
            }
          } else if ((null != model) && line.contains("(")) {
            if (hasScope || model.isInterface) {
              // method
              model.methods.add(new MethodModel(line, comments));
            }
            comments.clear();
          } else if (null != model) {
            if (hasScope) {
              // property, enum
              model.properties.add(new PropertyModel(line, comments));
            }
            comments.clear();
          }
        }
      }
      
      debug(model);
      in.close();
    } catch (Exception e) {
      model = null;
      log("Exception parsing: " + line);
      log(e);
    }
    
    return model;
  }
  
  // return true if 'line' contains one of the visibility scopes we're looking for
  private static boolean lineContainsScope(String line, String[] scope) {
    SfApexDoc.assertPrecondition(null != line);
    SfApexDoc.assertPrecondition(null != scope);
    
    String l = line.toLowerCase();
    for (int i = 0; i < scope.length; i++) {
      if (l.matches("(^|.*\\s)" + scope[i] + "\\s+(?!get|set;|set\\s*\\{).*")) {
        return true;
      }
    }
    return false;
  }
  
  // spit out some debug info if -v was specified
  private static void debug(ClassModel model) {
    if ((null != model) && debugOutput) {
      log("Class: " + model.getName());
      
      if (!model.properties.isEmpty()) {
        String properties = "";
        for (PropertyModel property : model.properties) {
          properties += property.getName() + " ";
        }
        log("  properties: " + properties);
      }
      
      if (!model.methods.isEmpty()) {
        String methods = "";
        for (MethodModel method : model.methods) {
          methods += method.getName() + " ";
        }
        log("  methods: " + methods);
      }
     }
  }
  
  // log the message and display usage help
  private static void syntaxError(String message) {
    log(message);
    
    // display usage
    log("");
    log("SfApexDoc is a tool for generating documentation from Salesforce Apex code class files.\n");
    log("The syntax is:");
    log("  SfApexDoc [-v] -s <source_folder> [-t <target_folder>] [-h <homefile>] [-a <authorfile>] [-p <scope>] [-x <file extension>]\n");
    log("  (v)ersion       - Displays the SfApexDoc version number");
    log("  (s)ource_folder - The folder containing your apex .cls files");
    log("  (t)arget_folder - The folder where HTML files will be created");
    log("  (h)omefile      - Contents for the home page right panel");
    log("  (a)uthorfile    - File containing project information for the header");
    log("  sco(p)e         - Semicolon-seperated list of scopes to document. Defaults to 'global;public'");
    log("  e(x)tension     - Extension of files to parse. Defaults to 'cls'");
    
    bail(null);
  }
  
  // log the message and exit
  private static void bail(String message) {
    log(message);
    log("");
    System.exit(-1);        
  }
}