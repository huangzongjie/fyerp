package com.graly.erp.internalorder.po;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class InternalLinePoBlockDialog extends ParentChildEntityBlockDialog {
	protected boolean flag = false;

	public InternalLinePoBlockDialog(Shell parent) {
        super(parent);
    }
	
	public InternalLinePoBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		super(parent, parentTable, whereClause, parentObject, childTable);
	}
	
	public InternalLinePoBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable, boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new InternalLinePoEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}