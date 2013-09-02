package com.apex.doc.popup.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
//import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
//import org.eclipse.swt.layout.FormData;
//import org.eclipse.swt.widgets.DirectoryDialog;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.jdt.core.IJavaElement;

public class AppStart implements IObjectActionDelegate {
	private IStructuredSelection select;
  public String path;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {}

	public void run(IAction action) {
		Object obj = null;
    try {
      obj = select.getFirstElement();
      path = ((IResource)obj).getLocation().toOSString();
    } catch (ClassCastException e) {
      try {
        IJavaElement element = (IJavaElement)obj;               
        path = element.getResource().getLocation().toOSString();
      } catch(Exception ef) {
      }
      
    	e.printStackTrace();
    }
    
		try {
		  ApexDocForm simpleForm = new ApexDocForm(null, path);
	    simpleForm.setBlockOnOpen(true);
	    simpleForm.open();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
		  select = (IStructuredSelection)selection;
		}
	}
}