package com.graly.erp.inv.receipt;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.EntityBlockDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;

public class ReceiptLineDialog extends EntityBlockDialog {
	protected String whereClause;
	protected Object parentObject;
	protected boolean flag;

	public ReceiptLineDialog(Shell parent) {
		super(parent);
	}

	public ReceiptLineDialog(Shell parent, ADTable table, String whereClause, Object parentObject) {
		super(parent, table);
		this.whereClause = whereClause;
		this.parentObject = parentObject;
	}

	public ReceiptLineDialog(Shell parent, ADTable table, String whereClause, Object parentObject, boolean flag) {
		super(parent, table);
		this.whereClause = whereClause;
		this.parentObject = parentObject;
		this.flag = flag;
	}

	protected void createBlock(ADTable adTable) {
		block = new ReceiptLineEntityBlock(new EntityTableManager(adTable), whereClause, parentObject, flag);
	}
}
