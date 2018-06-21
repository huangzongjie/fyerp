package com.graly.erp.inv.transfer.hy.dpk;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class DpkTransferLineDialog extends ParentChildEntityBlockDialog {
	private boolean flag;

	public DpkTransferLineDialog(Shell parent) {
        super(parent);
    }
	
	public DpkTransferLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		this(parent, parentTable, whereClause, parentObject, childTable, false);
	}
	
	public DpkTransferLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable, boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new DpkTransferLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}
