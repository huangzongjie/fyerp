package com.graly.erp.inv.out;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class OutLineBlockDialog extends ParentChildEntityBlockDialog {
	protected boolean flag = false;
	private boolean souFlag = false;
	public OutLineBlockDialog(Shell parent) {
        super(parent);
    }
	
	public OutLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		super(parent, parentTable, whereClause, parentObject, childTable);
	}
	
	public OutLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable, boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new OutLineEntryBlock(table, getParentObject(), whereClause, childTable, flag,souFlag);
	}
	
	public boolean isSouFlag() {
		return souFlag;
	}

	public void setSouFlag(boolean souFlag) {
		this.souFlag = souFlag;
	}
}