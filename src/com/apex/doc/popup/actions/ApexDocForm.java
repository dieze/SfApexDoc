package com.apex.doc.popup.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import apex.com.main.FileManager;
import apex.com.main.SfApexDoc;
import apex.com.main.SfApexDocPlugin;

/**
 * Dialog to display when plugin dialog is displayed
 *
 * @author Steve Cox
 */
public class ApexDocForm extends ApplicationWindow {
  //---------------------------------------------------------------------------
  // Constants
  private static final String TITLE = "Force.com Apex Documentation Tool (v" + SfApexDoc.VERSION + ")\n";
  private static final Integer TITLE_FONT_SIZE = 16;
  
  private static final Integer FONT_SIZE = 12;
  
  private static final String SRC_LABEL = "Source Directory";
  private static final String SRC_TOOLTIP = "Location of your Apex classes (.CLS files)";
  private static final String SRC_SUBDIR = "/src/classes";
  
  private static final String TARG_LABEL = "Target Directory";
  private static final String TARG_TOOLTIP = "The " + FileManager.ROOT_DIRECTORY + " folder will be created here";
  
  private static final String HOME_LABEL = "Home HTML File";
  private static final String HOME_TOOLTIP = "";
  
  private static final String AUTH_LABEL = "Author File";
  private static final String AUTH_TOOLTIP = "";
  
  private static final String SCOPE_LABEL = "Scope to document";
  
  private static final String BROWSE_BUTTON_LABEL = "...";
  
  private static final String OPEN_PROMPT_TITLE = "SfApexDoc Generation Results";
  private static final String OPEN_PROMPT = "SfApexDoc has completed successfully.\n" +
    "The location of the documentation home page is: \n%s/" +
    FileManager.ROOT_DIRECTORY + "/index.html\n\nWould you like to open it now?";
  
  private static final String PREF_SRC   = "sourceDir=";
  private static final String PREF_DEST  = "destDir=";
  private static final String PREF_HOME  = "homeFile=";
  private static final String PREF_AUTH  = "authorFile=";
  private static final String PREF_SCOPE = "scope=";
  
  private static final String PREFS_FILE_NAME = "SfApexDocPrefs.txt";
  
  
  //---------------------------------------------------------------------------
  // Properties
  private String path;
  
  
  //---------------------------------------------------------------------------
  // Methods
  public ApexDocForm(Shell parentShell, String path) {
    super(parentShell);
    super.setShellStyle(SWT.CLOSE);
    this.path = path;
  }
  
  protected Control createContents(Composite parent) {
    Color bg = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
    
    // set up the form
    final Composite composite = new Composite(parent, SWT.APPLICATION_MODAL);
    composite.setLayout(new GridLayout());
    composite.setBackground(bg);
    FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
    final Form form = toolkit.createForm(composite);
    form.setLayoutData(new GridData(GridData.FILL_BOTH));
    form.setFont(new Font(null, "", FONT_SIZE, 1));
    form.setBackground(bg);
    
    // version information
    CLabel l = new CLabel(form.getBody(), SWT.LEFT);
    l.setFont(new Font(null, "", TITLE_FONT_SIZE, 1));
    l.setText(TITLE);
    l.setBackground(bg);
    GridData gd1 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    gd1.horizontalSpan = 3;
    l.setLayoutData(gd1);
    
    form.getBody().setLayout(new GridLayout(3, false));
    
    // create the text controls and lookup buttons
    final Text source = createFileLookup(toolkit, form, SRC_LABEL, path + SRC_SUBDIR,
      GridData.FILL_HORIZONTAL, SRC_TOOLTIP, bg);
    Button sourceButton = createLookupButton(toolkit, form);
    
    final Text target = createFileLookup(toolkit, form, TARG_LABEL, path,
      GridData.FILL_HORIZONTAL, TARG_TOOLTIP, bg);
    Button targetButton = createLookupButton(toolkit, form);
    
    final Text htmlFile = createFileLookup(toolkit, form, HOME_LABEL, "",
      GridData.FILL_HORIZONTAL, HOME_TOOLTIP, bg);
    Button homeFileButton = createLookupButton(toolkit, form);
    
    final Text authorFile = createFileLookup(toolkit, form, AUTH_LABEL, "",
      GridData.FILL_HORIZONTAL, AUTH_TOOLTIP, bg);
    Button authorFileButton = createLookupButton(toolkit, form);
    
    // scope buttons
    toolkit.createLabel(form.getBody(), SCOPE_LABEL, SWT.NULL).setBackground(bg);
    GridData gd = new GridData(GridData.BEGINNING);
    gd.horizontalSpan = 2;
    final ArrayList<Button> scopeButtons = new ArrayList<Button>();
    for (String s : SfApexDoc.SCOPES.keySet()) {
      scopeButtons.add(createScopeButton(toolkit, form, s, SfApexDoc.SCOPES.get(s), gd, bg));
      toolkit.createLabel(form.getBody(), "", SWT.NULL).setBackground(bg);
    }
  
    toolkit.createLabel(form.getBody(), "", SWT.NULL).setBackground(bg);
    toolkit.createLabel(form.getBody(), "", SWT.NULL).setBackground(bg);
    Button buttonGenerate = toolkit.createButton(form.getBody(), " Generate ", SWT.PUSH);
    buttonGenerate.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
    
    // handlers for directory and file lookups
    mapButtonToText(sourceButton, source, form, true);
    mapButtonToText(targetButton, target, form, true);
    mapButtonToText(homeFileButton, htmlFile, form, false);
    mapButtonToText(authorFileButton, authorFile, form, false);
    
    // load saved preferences
    try {
      String line;
      File f = new File(new File(getSelectedProjectPath()), PREFS_FILE_NAME);
      BufferedReader reader = new BufferedReader(new FileReader(f));
      while (null != (line = reader.readLine())) {
        if (line.startsWith(PREF_SRC)) {
          source.setText(line.substring(PREF_SRC.length()));
        } else if (line.startsWith(PREF_DEST)) {
          target.setText(line.substring(PREF_DEST.length()));
        } else if (line.startsWith(PREF_HOME)) {
          htmlFile.setText(line.substring(PREF_HOME.length()));
        } else if (line.startsWith(PREF_AUTH)) {
          authorFile.setText(line.substring(PREF_AUTH.length()));
        } else if (line.startsWith(PREF_SCOPE)) {
          final String[] scopes = line.substring(PREF_SCOPE.length()).split(SfApexDoc.SCOPE_SEP);
          for (Button b : scopeButtons) {
            b.setSelection(Arrays.asList(scopes).contains(b.getText()));
          }
        }
      }
      reader.close();
    } catch (Exception e) {
    }
    
    // close when <ESC> is hit
    this.getShell().addListener(SWT.Traverse, new Listener() {
      public void handleEvent(Event event) {
        if (event.detail == SWT.TRAVERSE_ESCAPE) {
          form.getShell().dispose();
        }
      }
    });
    
    buttonGenerate.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent event) {}
      public void widgetSelected(SelectionEvent event) {
        try {
          if (source.getText() != null) {
            SfApexDoc.args.add(SfApexDoc.SRC_ARG);
            SfApexDoc.args.add(source.getText());
          }
          if (target.getText() != null) {
            SfApexDoc.args.add(SfApexDoc.TARG_ARG);
            SfApexDoc.args.add(target.getText());
          }
          if (authorFile.getText() != null) {
            SfApexDoc.args.add(SfApexDoc.AUTH_ARG);
            SfApexDoc.args.add(authorFile.getText());
          }
          if (htmlFile.getText() != null) {
            SfApexDoc.args.add(SfApexDoc.HOME_ARG);
            SfApexDoc.args.add(htmlFile.getText());
          }
          
          String scope = "";
          for (Button b : scopeButtons) {
            if (b.getSelection()) {
              scope += b.getText() + SfApexDoc.SCOPE_SEP;
            }
          }
          if ("" != scope) {
            SfApexDoc.args.add(SfApexDoc.SCOPE_ARG);
            SfApexDoc.args.add(scope);
          }
          
          // save preferences for next time
          try {
            File f = new File(new File(getSelectedProjectPath()), PREFS_FILE_NAME);
            f.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            writer.write(PREF_SRC + source.getText());      writer.newLine();
            writer.write(PREF_DEST + target.getText());     writer.newLine();
            writer.write(PREF_HOME + htmlFile.getText());   writer.newLine();
            writer.write(PREF_AUTH + authorFile.getText()); writer.newLine();
            String scopes = "";
            for (Button b : scopeButtons) {
              if (b.getSelection()) {
                scopes += (scopes.isEmpty()? "" : SfApexDoc.SCOPE_SEP) + b.getText();
              }
            }
            writer.write(PREF_SCOPE + scopes);
            writer.close();
          } catch (Exception e) {
          }
          
          new ProgressMonitorDialog(form.getShell()).run(true, false, new SfApexDocPlugin());
          
          if (MessageDialog.openQuestion(form.getShell(), OPEN_PROMPT_TITLE,
            String.format(OPEN_PROMPT, new Object[] {target.getText()}))) {
            
            String strUrl = "file:///" + target.getText() + "/" + FileManager.ROOT_DIRECTORY + "/index.html";
            strUrl = strUrl.replaceAll(" ", "%20");
            strUrl = strUrl.replace('\\', '/');
            java.awt.Desktop.getDesktop().browse(new java.net.URI(strUrl));
          }
          
          form.getShell().dispose();
        } catch (Exception e) {
          StringWriter trace = new StringWriter();
          e.printStackTrace(new PrintWriter(trace));
          MessageDialog.openError(form.getShell(), "SfApexDoc Error", e.getMessage() + trace.toString());
        }
      }
    });
    
    return composite;
  }
  
  
  //---------------------------------------------------------------------------
  // Helpers
  private static Text createFileLookup(FormToolkit toolkit, Form form, String label, String file, int layout, String tooltip, Color bg) {
    toolkit.createLabel(form.getBody(), label, SWT.NULL).setBackground(bg);
    final Text t = toolkit.createText(form.getBody(), file);
    t.setLayoutData(new GridData(layout));
    t.setToolTipText(tooltip);
    return t;
  }
  
  private static Button createLookupButton(FormToolkit toolkit, Form form) {
    Button b = toolkit.createButton(form.getBody(), BROWSE_BUTTON_LABEL, SWT.PUSH);
    b.setLayoutData(new GridData(GridData.END));
    return b;
  }
  
  private static Button createScopeButton(FormToolkit toolkit, Form form, String label, boolean sel, GridData gd, Color bg) {
    final Button b = toolkit.createButton(form.getBody(), label, SWT.CHECK);
    b.setSelection(sel);
    b.setLayoutData(gd);
    b.setBackground(bg);
    return b;
  }
  
  private static void mapButtonToText(Button b, final Text t, final Form form, final boolean isDir) {
    b.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent event) {}
      public void widgetSelected(SelectionEvent event) {
        t.setText(isDir ? new DirectoryDialog(form.getShell()).open() :
          new FileDialog(form.getShell(), SWT.OPEN).open());
      }
    });
  }
  
  private static String getSelectedProjectPath() {
    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (null != window) {
        IStructuredSelection selection = (IStructuredSelection)window.getSelectionService().getSelection();
        Object firstElement = selection.getFirstElement();
        if (firstElement instanceof IAdaptable) {
            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
            return project.getLocation().toString();
        }
    }
    
    return ".";
  }
}