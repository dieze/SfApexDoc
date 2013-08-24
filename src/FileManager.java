import java.io.*;
import java.util.*;

/**
 * Utility class for parsing text files and constructing the HTML
 * documentation files.
 * 
 * @author Steve Cox
 */
public class FileManager {
  //---------------------------------------------------------------------------
  // Constants
  private static final String ROOT_DIRECTORY = "ApexDocumentation";
  private static final String BODY_START = "<body>";
  private static final String BODY_END = "</body>";
  
  
  //---------------------------------------------------------------------------
  // Properties
  private String path;
  
  
  //---------------------------------------------------------------------------
  // Methods
  public FileManager(String path) {
    assert(null != path);
    
    this.path = path.trim().isEmpty() ? "." : path;
  }
  
  public void createDocumentation(ArrayList<ClassModel> models, String projectDetail, String homeContents) {
    assert(null != models);
    assert(null != projectDetail);
    assert(null != homeContents);
    
    String links = "<table width='100%'><tr style='vertical-align:top;'>" + getPageLinks(models);
    if ((null == homeContents) || homeContents.trim().isEmpty()) {
      homeContents = HtmlConstants.DEFAULT_HOME_CONTENTS;
    }
    homeContents = links + "<td width='80%'>" + "<h2 class='section-title'>Home</h2>" + homeContents + "</td>";
    homeContents = getHeader(projectDetail) + homeContents + HtmlConstants.FOOTER;
    
    String fileName = "";
    Hashtable<String, String> classHashTable = new Hashtable<String, String>();
    classHashTable.put("index", homeContents);
    for (ClassModel model : models) {
      if (null == model) continue;
      
      String contents = links;
      if (!model.nameLine.isEmpty()) {
        fileName = model.getName();
        model.addLinks();
        contents += "<td width='80%'>";
        contents += 
          "<h2 class='section-title'>" + fileName + 
            "<span style='float:right;margin-top:-5px;'><input type='button' value='+/- all' onclick='ToggleAll();' /></span>" +
          "</h2>" +
          "<div class='toggle_container_subtitle'>" + model.nameLine + "</div>" +
          "<table class='details' rules='all' border='1' cellpadding='6'>" +
          (model.description.isEmpty() ? "" : "<tr><th>Description</th><td>" + model.description + "</td></tr>") +
          (model.author.isEmpty() ? "" : "<tr><th>Author</th><td>" + model.author + "</td></tr>") +
          (model.date.isEmpty() ? "" : "<tr><th>Date</th><td>" + model.date + "</td></tr>") +
          (model.see.isEmpty() ? "" : "<tr><th>See</th><td>" + model.see + "</td></tr>") +
          "</table>";
        
        if (!model.properties.isEmpty()) {
          contents += "<p></p>" +
            "<h2 class='trigger'><input type='button' value='+' style='width:24px' />&nbsp;&nbsp;<a href='#'>Properties</a></h2>" + 
            "<div class='toggle_container'> " +
              "<table class='properties' border='1' rules='all' cellpadding='6'> ";
          
          for (PropertyModel prop : model.properties) {
            String name = prop.getName();
            prop.addLinks();
            contents += "<tr><td class='clsPropertyName'>" + name + "</td>" +
              "<td><div class='clsPropertyDeclaration'>" + prop.nameLine + "</div>" +
              "<div class='clsPropertyDescription'>" + prop.description +  
                (prop.author.isEmpty() && prop.date.isEmpty()? "" : " (" + prop.author + " " + prop.date + ")") +
                (prop.see.isEmpty() ? "" : " see " + prop.see) +
              "</div></tr>";
          }
          
          contents += "</table></div>";
        }
        
        if (!model.methods.isEmpty()) {
          contents += "<h2 class='section-title'>Methods</h2>";
          for (MethodModel method : model.methods) {
            String name = method.getName();
            method.addLinks();
            contents += "<h2 class='trigger'><input type='button' value='+' style='width:24px' />&nbsp;&nbsp;<a href='#'>" + name + "</a></h2>" +
              "<div class='toggle_container'>" +
              "<div class='toggle_container_subtitle'>" + method.nameLine + "</div>" +
              "<table class='details' rules='all' border='1' cellpadding='6'>" + 
              (method.description != "" ? "<tr><th>Description</th><td>" + method.description + "</td></tr> " : "") +
              (method.author != "" ? "<tr><th>Author</th><td>" + method.author + "</td></tr> " : "") +
              (method.date != "" ? "<tr><th>Date</th><td>" + method.date + "</td></tr> " : "") +
              (method.returns != "" ? "<tr><th>Returns</th><td>" + method.returns + "</td></tr> " : "") +
              (method.params.size() > 0 ? "<tr><th colspan='2' class='paramHeader'>Parameters</th></tr> " : "");
    
            for (String param : method.params) {
              if ((null != param) && !param.trim().isEmpty()) {
                if (param.indexOf(' ') != -1) {
                  String list[] = param.split(" ");
                  if (list.length >= 1) {
                    contents += "<tr><th class='param'>" + list[0] + "</th>";
                    String val = "";
                    if (list.length >= 2) {
                      val = "";
                      for (int i = 1; i < list.length; i++) {
                        val += list[i] + " ";
                      }
                    }
                    contents += "<td>" + val + "</td></tr>";
                  }
                }
              }
            }
            
            contents += (method.see.isEmpty() ? "" : "<tr><th>See</th><td>" + method.see + "</td></tr>");
            contents += "</table></div>";
          }
        }
        contents += "</div>";
        
        contents = getHeader(projectDetail) + contents + HtmlConstants.FOOTER;
        classHashTable.put(fileName, contents);
      }
    }
    
    createDocumentationFiles(classHashTable);
  }
  
  public String parseProjectDetail(String filePath) {
    assert(null != filePath);
    
    String contents = "";
    if (!filePath.trim().isEmpty()) {
      try {
        DataInputStream in = new DataInputStream(new FileInputStream(filePath));
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = br.readLine()) != null) {
          line = line.trim();
          int equalsPos = line.indexOf("=");
          String key = (equalsPos >= 0) ? line.substring(0, equalsPos).trim() : "";
          String value = (equalsPos >= 0) ? line.substring(equalsPos + 1).trim() : "";
          if (key.equalsIgnoreCase("projectname")) {
            contents += "<h2 style='margin:0px;'>" + value + "</h2>";
          } else if (!value.isEmpty()) {
            contents += value + "<br>";
          }
        }
      } catch (Exception e) {
        SfApexDoc.log(e);
      }
    }
      
    return contents;
  }

  // Parse the specified file and return the contents within a string.
  public String parseHtmlFile(String filePath) {
    assert(null != filePath);
    
    String contents = "";
    try {
      if (!filePath.trim().isEmpty()) {
        DataInputStream in = new DataInputStream(new FileInputStream(filePath));
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = br.readLine()) != null) {
          contents += line.trim();
        }
      }
    } catch (Exception e) {
      SfApexDoc.log(e);
    }
    
    int bodyStart = contents.indexOf(BODY_START);
    if (bodyStart != -1) {
      int bodyEnd = contents.indexOf(BODY_END);
      if (bodyEnd != -1) {
        contents = contents.substring(bodyStart + BODY_START.length(), bodyEnd);
      }
    }
    
    return contents;
  }
  
  
  //---------------------------------------------------------------------------
  // Helpers
  private static String getHeader(String detail) {
    return HtmlConstants.HEADER_OPEN + (!detail.trim().isEmpty() ? 
      detail : HtmlConstants.PROJECT_DETAIL) + HtmlConstants.HEADER_CLOSE;
  }
  
  private void copyFile(String source, String target) throws Exception {
    InputStream is = getClass().getResourceAsStream(source);
    FileOutputStream to = new FileOutputStream(target + "/" + source);
    byte[] buffer = new byte[4096];
    int bytesRead;
    while ((bytesRead = is.read(buffer)) != -1) {
      to.write(buffer, 0, bytesRead); // write
    }
    to.flush();
    to.close();
    is.close();
  }
  
  private void createDocumentationFiles(Hashtable<String, String> classHashTable){
    try {
      // create required folders for documentation files
      if (!path.endsWith("/") && !path.endsWith("\\")) {
        path += '/';
      }
      path += ROOT_DIRECTORY;
      (new File(path)).mkdirs();
      
      for (String fileName : classHashTable.keySet()) {        
        SfApexDoc.log("Processing: " + fileName);
        File file= new File(path + "/" + fileName + ".html");
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos = new DataOutputStream(fos);
        dos.writeBytes(classHashTable.get(fileName));
        dos.close();
        fos.close();
      }
      
      copyResources(path);
    } catch(Exception e) {
      SfApexDoc.log(e);
    }
  }
  
  private String getPageLinks(ArrayList<ClassModel> models){
    String links = "<td width='20%' class='leftmenus'><div onclick=\"gotomenu('index.html');\">Home</div>";
    for (ClassModel model : models) {
      String name;
      if ((null != model) && !(name = model.getName()).isEmpty()) {
        links += "<div onclick=\"gotomenu('" + name + ".html');\">" + name + "</div>";
      }
    }
    return links + "</td>";
  }
  
  private void copyResources(String toFileName) throws IOException, Exception {
    copyFile("apex_doc_logo.png", toFileName);
    copyFile("SfApexDoc.css", toFileName);
    copyFile("h2_trigger_a.gif", toFileName);
    copyFile("jquery-latest.js", toFileName);
    copyFile("toggle_block_btm.gif", toFileName);
    copyFile("toggle_block_stretch.gif", toFileName);
  }
}