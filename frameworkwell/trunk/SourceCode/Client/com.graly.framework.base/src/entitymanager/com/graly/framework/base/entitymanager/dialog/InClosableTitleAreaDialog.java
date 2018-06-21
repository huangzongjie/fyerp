package com.graly.framework.base.entitymanager.dialog;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class InClosableTitleAreaDialog extends TitleAreaDialog {
	public boolean isOpen = false;
	
	public InClosableTitleAreaDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.TITLE);
	}
	
	@Override
	public int open() {
		int returnCode = super.open();
		isOpen = true;
		return returnCode;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
}
