public class HtmlConstants {
  //---------------------------------------------------------------------------
  // Constants
  public static final String HEADER_CLOSE = "</td></tr></table></div>";
  
  public static final String FOOTER = "</div></div></td></tr></table><hr/><center style='font-size:10px;'><a href='http://code.google.com/p/apexdoc/'>Powered By ApexDoc </a></center></body></html>";
  
  public static final String DEFAULT_HOME_CONTENTS = "<h1>Project Home</h2><p>(specify a -home parameter to override this)</p>";
  
  public static final String PROJECT_DETAIL = "<h2 style='margin:0px;'>Project Demo</h2>" +
    "(specify an -author parameter to override this)<br/>" +
    "<a href='http://code.google.com/p/apexdoc/'>(original Google Code project)</a><br/>";
  
  public static final String HEADER_OPEN = 
    "<html><head><script type='text/javascript' src='jquery-latest.js'></script>" +
    "<link rel='stylesheet' type='text/css' href='SfApexDoc.css' /> " + 
    "<script>" + 
      "$(document).ready(function() {" +
        "$('.toggle_container').hide();\n" + 
        "$('h2.trigger').click(function() {\n" +
          "$(this).toggleClass('active').next().slideToggle('fast');" +
          "ToggleBtnLabel(this.firstChild);" +
          "return false; " +
        "});" +
      "});\n" +  
      
      "function gotomenu(url) {" +
        "document.location.href = url;" +
      "}\n" +
      
      "function ToggleBtnLabel(ctrl) {" +
        "ctrl.value = (ctrl.value=='+' ? '-' : '+');" +
      "}\n" +
      
      "function IsExpanded(ctrl) {" +
        "return (ctrl.value == '-');" +
      "}\n" +
      
      "function ToggleAll() {" +
        "var cExpanded = 0;" +
        "$('h2.trigger').each(function() {" +
          "if (!IsExpanded(this.firstChild)) {" +
            "$(this).toggleClass('active').next().slideToggle('fast');" +
            "ToggleBtnLabel(this.firstChild);" +
            "cExpanded++;" +
          "}" +
        "});" +
        
        "if (cExpanded == 0) {" +
          "$('h2.trigger').each(function() {" +
            "$(this).toggleClass('active').next().slideToggle('fast');" +
            "ToggleBtnLabel(this.firstChild);" +
          "});" +
        "}" +
      "}\n" +  
    "</script>" +
  "</head>" +
  
  "<body>" +
  "<div class='topsection'>" +
    "<table>" +
      "<tr><td>" +
          "<img src='apex_doc_logo.png' style='border:1px solid #000;'/>" +
        "</td>" +
        "<td>";
}
