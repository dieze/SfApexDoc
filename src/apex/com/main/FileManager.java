package apex.com.main;

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
  public static final String ROOT_DIRECTORY = "SfApexDocs";
  private static final String BODY_START = "<body>";
  private static final String BODY_END = "</body>";
  
  
  //---------------------------------------------------------------------------
  // Properties
  protected String path;
  
  
  //---------------------------------------------------------------------------
  // Methods
  public FileManager() {
    this("");
  }
  
  public FileManager(String path) {
    SfApexDoc.assertPrecondition(null != path);
    
    this.path = path.trim();
    if (this.path.isEmpty()) this.path = ".";
  }
  
  public void createDocs(ArrayList<ClassModel> models, String detailFile, String homeFile) {
    SfApexDoc.assertPrecondition(null != models);
    SfApexDoc.assertPrecondition(null != detailFile);
    SfApexDoc.assertPrecondition(null != homeFile);
    
    String projectDetail = parseProjectDetail(detailFile.trim());
    if (projectDetail.isEmpty()) projectDetail = HtmlConstants.DEFAULT_PROJECT_DETAIL;
    
    String homeContents = parseHtmlFile(homeFile.trim());
    if (homeContents.isEmpty()) homeContents = HtmlConstants.DEFAULT_HOME_CONTENTS;
    
    String links = "<table width='100%'><tr>" + getPageLinks(models);
    homeContents = links + "<td><h2 class='section-title'>Home</h2>" + homeContents + "</td>";
    homeContents = HtmlConstants.HEADER_OPEN + projectDetail + HtmlConstants.HEADER_CLOSE +
      homeContents + HtmlConstants.FOOTER;
    
    Hashtable<String, String> classHashTable = new Hashtable<String, String>();
    classHashTable.put("index", homeContents);
    
    for (ClassModel model : models) {
      String contents = links;
      if (!model.getNameLine().isEmpty()) {
        String children = "";
        for (ClassModel child : model.children) {
          children += "<tr>" + createClassDoc(child, child.getName()) + "</tr>";
        }
        
        final String fileName = model.getName();
        contents += createClassDoc(model, fileName) + children;
        
        classHashTable.put(fileName.toLowerCase(), HtmlConstants.HEADER_OPEN + projectDetail +
          HtmlConstants.HEADER_CLOSE + contents + HtmlConstants.FOOTER);
      }
    }
    
    createDocFiles(classHashTable);
  }
  
  private String createClassDoc(ClassModel model, String fileName) {
    model.addLinks();
    String contents = "<td class='classCell'>";
    contents +=
      "<h2 class='section-title'>" + fileName +
        "<span><input type='button' value='+/- all' onclick='ToggleAll(this);' /></span>" +
      "</h2>" +
      "<div class='toggle_container_subtitle'>" + model.getNameLine() + "</div>" +
      "<table class='details' rules='all' border='1' cellpadding='6'>" +
      (model.getDescription().isEmpty() ? "" : "<tr><th>Description</th><td>" + model.getDescription() + "</td></tr>") +
      (model.getAuthor().isEmpty() ? "" : "<tr><th>Author</th><td>" + model.getAuthor() + "</td></tr>") +
      (model.getDate().isEmpty() ? "" : "<tr><th>Date</th><td>" + model.getDate() + "</td></tr>") +
      (model.getSee().isEmpty() ? "" : "<tr><th>See</th><td>" + model.getSee() + "</td></tr>") +
      "</table>";
    
    if (!model.properties.isEmpty()) {
      contents += "<p></p>" +
        "<h2 class='trigger'><input type='button' value='+'/>&nbsp;&nbsp;<a href='#'>Properties</a></h2>" +
        "<div class='toggle_container'> " +
          "<table class='properties' border='1' rules='all' cellpadding='6'> ";
      
      for (PropertyModel prop : model.properties) {
        String name = prop.getName();
        prop.addLinks();
        contents += "<tr><td class='clsPropertyName'>" + name + "</td>" +
          "<td><div class='clsPropertyDeclaration'>" + prop.getNameLine() + "</div>" +
          "<div class='clsPropertyDescription'>" + prop.getDescription() +
            (prop.getAuthor().isEmpty() && prop.getDate().isEmpty()? "" : " (" + prop.getAuthor() + " " + prop.getDate() + ")") +
            (prop.getSee().isEmpty() ? "" : " see " + prop.getSee()) +
          "</div></tr>";
      }
      
      contents += "</table></div>";
    }
    
    if (!model.methods.isEmpty()) {
      contents += "<h2 class='section-title methods'>Methods</h2>";
      for (MethodModel method : model.methods) {
        String name = method.getName();
        method.addLinks();
        contents += "<h2 class='trigger'><input type='button' value='+'/>&nbsp;&nbsp;<a href='#'>" + name + "</a></h2>" +
          "<div class='toggle_container'>" +
          "<div class='toggle_container_subtitle'>" + method.getNameLine() + "</div>" +
          "<table class='details' rules='all' border='1' cellpadding='6'>" +
          (method.getDescription() != "" ? "<tr><th>Description</th><td>" + method.getDescription() + "</td></tr> " : "") +
          (method.getAuthor() != "" ? "<tr><th>Author</th><td>" + method.getAuthor() + "</td></tr> " : "") +
          (method.getDate() != "" ? "<tr><th>Date</th><td>" + method.getDate() + "</td></tr> " : "") +
          (method.getReturns() != "" ? "<tr><th>Returns</th><td>" + method.getReturns() + "</td></tr> " : "") +
          (method.getParams().size() > 0 ? "<tr><th colspan='2' class='paramHeader'>Parameters</th></tr> " : "");
        
        for (String param : method.getParams()) {
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
        
        contents += (method.getSee().isEmpty() ? "" : "<tr><th>See</th><td>" + method.getSee() + "</td></tr>");
        contents += "</table></div>";
      }
    }
    return contents + "</div>";
  }
  
  private String parseProjectDetail(String filePath) {
    String contents = "";
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filePath));
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        int equalsPos = line.indexOf("=");
        String key = (equalsPos >= 0) ? line.substring(0, equalsPos).trim() : "";
        String value = (equalsPos >= 0) ? line.substring(equalsPos + 1).trim() : "";
        if (key.equalsIgnoreCase("projectname")) {
          contents += "<h2>" + value + "</h2>";
        } else if (!value.isEmpty()) {
          contents += value + "<br>";
        }
      }
      reader.close();
    } catch (Exception e) {
      SfApexDoc.log("parseProjectDetail(" + filePath + "): " + e.getMessage());
    }
      
    return contents.trim();
  }

  private String parseHtmlFile(String filePath) {
    String contents = "";
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filePath));
      String line;
      while ((line = reader.readLine()) != null) {
        contents += line.trim();
      }
      reader.close();
    } catch (Exception e) {
      SfApexDoc.log("parseHtmlFile(" + filePath + "): " + e.getMessage());
    }
    
    int bodyStart = contents.indexOf(BODY_START);
    if (bodyStart >= 0) {
      int bodyEnd = contents.indexOf(BODY_END);
      if (bodyEnd >= 0) {
        contents = contents.substring(bodyStart + BODY_START.length(), bodyEnd);
      }
    }
    
    return contents.trim();
  }
  
  
  //---------------------------------------------------------------------------
  // Helpers
  private void copyFile(String source, String target) throws Exception {
    target += "/" + source;
    if (!(new File(target).exists())) {
      InputStream is = getClass().getResourceAsStream(source);
      FileOutputStream to = new FileOutputStream(target);
      byte[] buffer = new byte[4096];
      int bytesRead;
      while ((bytesRead = is.read(buffer)) >= 0) {
        to.write(buffer, 0, bytesRead);
      }
      to.flush();
      to.close();
      is.close();
    }
  }
  
  private void createDocFiles(Hashtable<String, String> classHashTable) {
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
        
        SfApexDoc.instance.showProgress();
      }
      
      copyResources(path);
    } catch(Exception e) {
      SfApexDoc.log(e);
    }
  }
  
  private String getPageLinks(ArrayList<ClassModel> models){
    String links = "<td class='leftmenus' rowspan='100'><div onclick=\"gotomenu('index.html');\">Home</div>";
    for (ClassModel model : models) {
      String name;
      if (!(name = model.getName()).isEmpty()) {
        links += "<div onclick=\"gotomenu('" + name.toLowerCase() + ".html');\">" + name + "</div>";
      }
    }
    return links + "</td>";
  }
  
  private void copyResources(String toPath) throws IOException, Exception {
    copyFile("logo.png", toPath);
    copyFile("SfApexDoc.css", toPath);
    copyFile("h2_trigger_a.gif", toPath);
    copyFile("jquery-latest.js", toPath);
    copyFile("toggle_block_btm.gif", toPath);
    copyFile("toggle_block_stretch.gif", toPath);
  }
}