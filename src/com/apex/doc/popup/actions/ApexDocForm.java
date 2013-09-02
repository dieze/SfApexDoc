package com.apex.doc.popup.actions;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.*;
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
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import apex.com.main.FileManager;
import apex.com.main.SfApexDoc;

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
        	       		        	
        	new ProgressMonitorDialog(form.getShell()).run(true, false, new SfApexDoc()); 
        	
    			if (MessageDialog.openQuestion(form.getShell(), OPEN_PROMPT_TITLE,
    			  String.format(OPEN_PROMPT, new Object[] {target.getText()}))) {
    			  
    				String strUrl = "file:///" + target.getText() + "/" + FileManager.ROOT_DIRECTORY + "/index.html";
    				strUrl = strUrl.replaceAll(" ", "%20");
    				strUrl = strUrl.replace('\\', '/');
    				java.awt.Desktop.getDesktop().browse(new java.net.URI(strUrl));
    			}
			    
    			form.getShell().dispose();
      	} catch (Exception e) {
      		e.printStackTrace();
      		MessageDialog.openError(form.getShell(), "SfApexDoc Error", e.getMessage());
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
          new FileDialog(form.getShell(), SWT.SAVE).open());
      }
    });
  }
}