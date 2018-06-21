package com.graly.framework.base.entitymanager.dialog;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;

public class ExtendDialog extends InClosableTitleAreaDialog {
	
	private static final Logger logger = Logger.getLogger(ExtendDialog.class);

	private String tableId;
	private Object parent;
	
	public ExtendDialog() {
		this(UI.getActiveShell());
	}
	
	public ExtendDialog(Shell parent) {
		super(parent);
	}
	
	public ExtendDialog(String tableId, Object parent) {
		this();
		this.setTableId(tableId);
		this.setParent(parent);
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getTableId() {
		return tableId;
	}

	public void setParent(Object parent) {
		this.parent = parent;
	}

	public Object getParent() {
		return parent;
	}
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				Message.getString("common.ok"), false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
	}
}
