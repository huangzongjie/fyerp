package com.graly.erp.wip.workcenter.schedule.pmczzj;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.base.ui.util.Message;

public class PmcZzjImportDialog extends ProgressMonitorDialog {
	private String title = "";

	public PmcZzjImportDialog(Shell parent) {
		super(parent);
	}
	
	public PmcZzjImportDialog(Shell parent, String title) {
		super(parent);
		this.title = title;
	}
	
	// ʹȡ����ť������Ӣ����ʾ
	protected void createCancelButton(Composite parent) {
		cancel = createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
		if (arrowCursor == null) {
			arrowCursor = new Cursor(cancel.getDisplay(), SWT.CURSOR_ARROW);
		}
		cancel.setCursor(arrowCursor);
		setOperationCancelButtonEnabled(enableCancelButton);
	}
	
	// ʹ�Ի�����������Ӣ����ʾ
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText(title);
	}

}
