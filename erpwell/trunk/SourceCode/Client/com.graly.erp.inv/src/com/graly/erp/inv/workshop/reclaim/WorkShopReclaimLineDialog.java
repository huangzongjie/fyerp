package com.graly.erp.inv.workshop.reclaim;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class WorkShopReclaimLineDialog extends ParentChildEntityBlockDialog {
	private boolean flag;

	public WorkShopReclaimLineDialog(Shell parent) {
        super(parent);
    }
	
	public WorkShopReclaimLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		this(parent, parentTable, whereClause, parentObject, childTable, false);
	}
	
	public WorkShopReclaimLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable, boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new WorkShopReclaimLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}
