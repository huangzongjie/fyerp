package com.graly.erp.internalorder;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class InternalOrderLineBlockDialog extends ParentChildEntityBlockDialog {
	protected boolean flag = false;

	public InternalOrderLineBlockDialog(Shell parent) {
        super(parent);
    }
	
	public InternalOrderLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		super(parent, parentTable, whereClause, parentObject, childTable);
	}
	
	public InternalOrderLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable, boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new InternalOrderLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}