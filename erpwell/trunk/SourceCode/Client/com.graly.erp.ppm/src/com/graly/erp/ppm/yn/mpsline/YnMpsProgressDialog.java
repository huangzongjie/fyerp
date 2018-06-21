package com.graly.erp.ppm.yn.mpsline;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.base.ui.util.Message;

public class YnMpsProgressDialog extends ProgressMonitorDialog {
	private String title = "";

	public YnMpsProgressDialog(Shell parent) {
		super(parent);
	}
	
	public YnMpsProgressDialog(Shell parent, String title) {
		super(parent);
		this.title = title;
	}
	
	// 使取消按钮可以中英文显示
	protected void createCancelButton(Composite parent) {
		cancel = createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
		if (arrowCursor == null) {
			arrowCursor = new Cursor(cancel.getDisplay(), SWT.CURSOR_ARROW);
		}
		cancel.setCursor(arrowCursor);
		setOperationCancelButtonEnabled(enableCancelButton);
	}
	
	// 使对话框标题可以中英文显示
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText(title);
	}

}
