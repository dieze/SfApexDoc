package apex.com.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Similar to ApexDoc, but the Eclipse plugin capabilities have been removed.
 *
 * @author Steve Cox
 */
public class SfApexDoc {
  //---------------------------------------------------------------------------
  // Constants
  public static final String VERSION = "1.2.0";
  private static final String LOG_FILE_NAME = "SfApexDocLog.txt";
  private static final String DEFAULT_EXT = "cls";
  
  public static final Map<String,Boolean> SCOPES = new HashMap<String,Boolean>();
  static {
    SCOPES.put("global", true);
    SCOPES.put("public", true);
    SCOPES.put("protected", false);
    SCOPES.put("private", false);
  };
  public static final String SCOPE_SEP = ",";
  private static final String END_OF_SIGNATURE = "{}=;";
  
  private static final String COMMENT_START = "/**";
  private static final String COMMENT_END = "*/";
  private static final String DEF_VISIBILITY = "private";
  
  public static final String SRC_ARG   = "-s";
  public static final String TARG_ARG  = "-t";
  public static final String HOME_ARG  = "-h";
  public static final String AUTH_ARG  = "-a";
  public static final String SCOPE_ARG = "-p";
  public static final String EXT_ARG   = "-x";
  public static final String DEBUG_ARG = "-d";
  public static final String VERS_ARG  = "-v";
  
  
  //---------------------------------------------------------------------------
  // Properties
  public static ArrayList<String> args = new ArrayList<String>();
  public static SfApexDoc instance;
  
  private static PrintStream logFile;
  private static boolean debugOutput = false;

  
  //---------------------------------------------------------------------------
  // Constructor
  public SfApexDoc() {
    instance = this;
  }
  
  
  //---------------------------------------------------------------------------
  // Methods
  /**
   * Entry point for SfApexDoc. Invoke this from a command line
   * interface, ANT script, etc. Parameters are documented in the 'syntaxError'
   * method below.
   */
  public static void main(String[] args) {
    SfApexDoc.args = new ArrayList<String>(Arrays.asList(args));
    new SfApexDoc().doIt();
  }
  
  public void doIt() {
    log("SfApexDoc version " + VERSION + "\n");
    
    SfApexDoc.assertPrecondition(null != args);
    
    // create a log file
    try {
      logFile = new PrintStream(new FileOutputStream(new File(LOG_FILE_NAME)));
    } catch (Exception e) {
      System.err.println("Failed to create log file: " + e.getMessage());
    }
    
    // parse command line parameters
    String sourceDir="", destDir="", homeFile="", authorFile="", ext=DEFAULT_EXT;
    try {
      ArrayList<String> scope = new ArrayList<String>();
      for (String s : SCOPES.keySet()) {
        if (SCOPES.get(s)) {
          scope.add(s);
        }
      }
      
      for (int i = 0; i < args.size(); ++i) {
        String argKey = args.get(i).substring(0, 2).toLowerCase();
        
        if (SRC_ARG.equals(argKey)) sourceDir = args.get(++i);
        else if (TARG_ARG.equals(argKey)) destDir = args.get(++i);
        else if (HOME_ARG.equals(argKey)) homeFile = args.get(++i);
        else if (AUTH_ARG.equals(argKey)) authorFile = args.get(++i);
        else if (SCOPE_ARG.equals(argKey)) scope = new ArrayList<String>(Arrays.asList(args.get(++i).toLowerCase().split(SCOPE_SEP)));
        else if (EXT_ARG.equals(argKey)) ext = args.get(++i);
        else if (DEBUG_ARG.equals(argKey)) debugOutput = true;
        else if (VERS_ARG.equals(argKey)) bail(null);
        else syntaxError("Invalid option: " + argKey);
      }
      
      // make sure the source folder is valid
      File sourceFolder = new File(sourceDir);
      if (!sourceFolder.exists() || !sourceFolder.isDirectory()) {
        bail("Invalid source folder: " + sourceDir);
      }
      
      File[] files = sourceFolder.listFiles();
      initProgress(files.length * 2);
      
      // get the list of files to process
      ArrayList<ClassModel> models = new ArrayList<ClassModel>();
      for (File f : files) {
        if (f.isFile() && f.getAbsolutePath().toLowerCase().endsWith("." + ext)) {
          ClassModel m = parse(getFileContents(f.getAbsolutePath()), scope);
          if (null != m) {
            models.add(m);
          } else {
            showProgress(); // we won't be creating docs for this
          }
        } else {
          showProgress(); // we won't be creating docs for this
        }
        
        showProgress();
      }
      
      new FileManager(destDir).createDocs(models, authorFile, homeFile);
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
  
  public static void assertPrecondition(boolean condition) {
    if (!condition) {
      throw new NullPointerException();
    }
  }
  
  
  //---------------------------------------------------------------------------
  // Helpers
  public void initProgress(int units) {}
  public void showProgress() {}
  
  // return the specified file as a single string
  private static String getFileContents(String filePath) {
    String result = "", line = "";
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filePath));
      while (null != (line = reader.readLine())) {
        result += line + '\n';
      }
      reader.close();
    } catch (Exception e) {
      log("Exception loading file: " + filePath);
    }
    
    return result;
  }
  
  // Parse the specified text; see inline comments for specific rules
  // public only for testing
  public static ClassModel parse(String text, ArrayList<String> scope) {
    ClassModel parentClass = null, model = null;
    String line = "", prevLine = null;
    int lineIndex = 0, nestedCurlyBraceDepth = 0;
    try {
      boolean commentsStarted = false;
      ArrayList<String> comments = new ArrayList<String>();
       
      BufferedReader reader = new BufferedReader(new StringReader(text));
      while (null != (line = reader.readLine())) {
        ++lineIndex;
        
        int i = line.indexOf("//");
        if (!commentsStarted) {
          // ignore anything after // style comments. This allows hiding of tokens from ApexDoc
          if (i > -1) {
            // ignore segments that could be links (i.e. http://www...)
            if ((0 == i) || (':' != line.charAt(i - 1))) {
              line = line.substring(0, i);
            }
          }
        }
        
        line = line.replaceAll("\t", " ").trim();
        if (line.length() > 0) {
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
            final int openCurlies = countChars(line, '{'), closeCurlies = countChars(line, '}');
            nestedCurlyBraceDepth += openCurlies;
            nestedCurlyBraceDepth -= closeCurlies;
            
            if (null != prevLine) {
              line = prevLine + ' ' + line;
              prevLine = null;
            }
            
            boolean endOfSignature = false;
            i = line.length();
            for (int j = 0, iEnd; j < END_OF_SIGNATURE.length(); ++j) {
              if ((iEnd = line.indexOf(END_OF_SIGNATURE.charAt(j))) >= 0) {
                endOfSignature = true;
                i = Math.min(i, iEnd);
              }
            }
            
            if (endOfSignature) {
              line = line.substring(0, i);
              boolean hasScope = lineContainsScope(line, scope);
              if (line.toLowerCase().matches("(^|.*\\s)(" + ClassModel.types + ")\\s+.*")) {
                if (null == model) {
                  // top-level class
                  if (!hasScope) break;  // must be a test class - skip it
                  model = new ClassModel(line, comments);
                  comments.clear();
                } else if (hasScope || lineContainsScope(DEF_VISIBILITY, scope)) {
                  // nested class
                  parentClass = model;
                  model = new ClassModel(parentClass, line, comments);
                  parentClass.children.add(model);
                  if ((openCurlies > 0) && (openCurlies == closeCurlies)) {
                    // this is a one-line class declaration; back to the parent
                    model = parentClass;
                    parentClass = null;
                  }
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
                } else if ((null != parentClass) && (1 == nestedCurlyBraceDepth)) {
                  // this is the end of the nested class; back to the parent
                  model = parentClass;
                  parentClass = null;
                }
                comments.clear();
              }
            } else {
              // append the next line and try again
              prevLine = line;
            }
          }
        }
      }
      
      if (null != model) {
        Collections.sort(model.properties, new ModelComparer());
        Collections.sort(model.methods, new ModelComparer());
        debug(model);
      }
    } catch (Exception e) {
      model = null;
      log("Exception parsing line "+ lineIndex + ": " + line + "; " + e.getMessage());
    }
    
    return model;
  }
  
  // return true if 'line' contains one of the visibility scopes we're looking for
  private static boolean lineContainsScope(String line, ArrayList<String> scope) {
    SfApexDoc.assertPrecondition(null != line);
    SfApexDoc.assertPrecondition(null != scope);
    
    String l = line.toLowerCase();
    for (int i = 0; i < scope.size(); i++) {
      if (l.matches("(^|.*\\s)" + scope.get(i) + "\\s+(?!get|set;|set\\s*\\{).*")) {
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
  
  /** Count the number of occurrences of 'needle' in 'haystack' */
  private static int countChars(String haystack, char needle) {
    int count = 0;
    for (int i = 0; i < haystack.length(); ++i) {
      if (haystack.charAt(i) == needle) {
        ++count;
      }
    }
    
    return count;
  }
}