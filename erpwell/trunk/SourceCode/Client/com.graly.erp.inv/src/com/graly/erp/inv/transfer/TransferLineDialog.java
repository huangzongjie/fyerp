package com.graly.erp.inv.transfer;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class TransferLineDialog extends ParentChildEntityBlockDialog {
	private boolean flag;

	public TransferLineDialog(Shell parent) {
        super(parent);
    }
	
	public TransferLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		this(parent, parentTable, whereClause, parentObject, childTable, false);
	}
	
	public TransferLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable, boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new TransferLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}
