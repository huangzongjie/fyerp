package com.graly.erp.inv.workshop.requisition;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class WorkShopRequestionLineDialog extends ParentChildEntityBlockDialog {
	private boolean flag;

	public WorkShopRequestionLineDialog(Shell parent) {
        super(parent);
    }
	
	public WorkShopRequestionLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		this(parent, parentTable, whereClause, parentObject, childTable, false);
	}
	
	public WorkShopRequestionLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable, boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new WorkShopRequestionLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}
