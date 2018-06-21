package com.graly.promisone.base.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.graly.promisone.base.application.Activator;

public class UI {
	
	public static final String APPLICATION_NAME = "MESwell";
	
	public static void showError(String message, Throwable t) {
        showError(message, t, APPLICATION_NAME);
    }
	
	public static void showError(String message, Throwable t, String title) {
        IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), IStatus.OK, message, t);
        ErrorDialog dlg = new ErrorDialog(getActiveShell(),
                title, message, status, IStatus.ERROR | IStatus.WARNING | IStatus.INFO | IStatus.OK | IStatus.CANCEL);
        dlg.open();
    }
	
	public static void showError(String message) {
		showError(message, APPLICATION_NAME);
    }

    public static void showError(String message, String title) {
    	MessageBox mb = new MessageBox(getActiveShell(), SWT.OK | SWT.ICON_ERROR );
    	mb.setMessage(message);
		mb.setText(title); 
		mb.open();
    }
    
	public static void showWarning(String message) {
        showWarning(message, APPLICATION_NAME);
    }

    public static void showWarning(String message, String title) {
    	MessageBox mb = new MessageBox(getActiveShell(), SWT.OK | SWT.ICON_WARNING );
    	mb.setMessage(message);
		mb.setText(title); 
		mb.open();
    }
    
    public static void showInfo(String message) {
        showInfo(message, APPLICATION_NAME);
    }

    public static void showInfo(String message, String title) {
    	MessageBox mb = new MessageBox(getActiveShell(), SWT.OK | SWT.ICON_INFORMATION );
    	mb.setMessage(message);
		mb.setText(title); 
		mb.open();
    }
    
    public static boolean showConfirm(String message) {
    	return showConfirm(message, APPLICATION_NAME);
    }
    
    public static boolean showConfirm(String message, String title) {
    	MessageBox mb = new MessageBox(getActiveShell(), SWT.OK | SWT.CANCEL | SWT.ICON_QUESTION );
    	mb.setMessage(message);
		mb.setText(title); 
		switch (mb.open()) {
			case SWT.OK:
				return true;
			case SWT.CANCEL:					
				return false;
		}
		return false;
	}
    
    public static Shell getActiveShell() {
		Shell shell = null;
		if (Display.getCurrent() != null)
			shell = Display.getCurrent().getActiveShell();
		if (shell == null)
			shell = Display.getDefault().getActiveShell();
		if (shell == null)
			shell = getActiveWorkbenchShell();

		return shell;
	}
    
    private static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		return window == null ? null : window.getShell();
	}
    
    private static IWorkbenchWindow getActiveWorkbenchWindow() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}


}
