package apex.com.main;

public class HtmlConstants {
  //---------------------------------------------------------------------------
  // Constants
  public static final String HEADER_CLOSE = "</td></tr></table></div>";
  
  public static final String FOOTER = "</div></div></td></tr></table><hr/><center id='footer'>" +
    "<a href='https://gitlab.com/StevenWCox/sfapexdoc/wikis/Home'>Powered By SfApexDoc version " +
    SfApexDoc.VERSION + "</a></center></body></html>";
  
  public static final String DEFAULT_HOME_CONTENTS = "<h1>Project Home</h2><p>(specify a -home parameter to override this)</p>";
  
  public static final String DEFAULT_PROJECT_DETAIL = "<h2>Project Demo</h2>" +
    "(specify an -author parameter to override this)<br/>" +
    "<a href='https://gitlab.com/StevenWCox/sfapexdoc/wikis/Home'>(GitLab project)</a><br/>";
  
  public static final String HEADER_TOGGLE = "$('.toggle_container').hide();";
  public static final String HEADER_OPEN = 
    "<html><head><script type='text/javascript' src='jquery-latest.js'></script>" +
    "<link rel='stylesheet' type='text/css' href='SfApexDoc.css' /> " + 
    "<script>" + 
      "$(document).ready(function() {" +
        HEADER_TOGGLE + "\n" + 
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
      
      "function ToggleAll(e) {" +
        "var cExpanded = 0;" +
        "$('h2.trigger', $(e).closest('tr')).each(function() {" +
          "if (!IsExpanded(this.firstChild)) {" +
            "$(this).toggleClass('active').next().slideToggle('fast');" +
            "ToggleBtnLabel(this.firstChild);" +
            "cExpanded++;" +
          "}" +
        "});" +
        
        "if (cExpanded == 0) {" +
          "$('h2.trigger', $(e).closest('tr')).each(function() {" +
            "$(this).toggleClass('active').next().slideToggle('fast');" +
            "ToggleBtnLabel(this.firstChild);" +
          "});" +
        "}" +
      "}\n" +  
    "</script>" +
  "</head>" +
  
  "<body>" +
  "<div class='topSection'>" +
    "<table>" +
      "<tr><td>" +
          "<img id='logo' src='logo.png'/>" +
        "</td>" +
        "<td>";
}
