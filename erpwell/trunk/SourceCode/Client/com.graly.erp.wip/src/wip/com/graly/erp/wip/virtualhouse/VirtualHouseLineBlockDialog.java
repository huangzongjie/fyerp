package com.graly.erp.wip.virtualhouse;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class VirtualHouseLineBlockDialog extends ParentChildEntityBlockDialog {
	public VirtualHouseLineBlockDialog(Shell parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}
	public VirtualHouseLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		super(parent, parentTable, whereClause, parentObject, childTable);
	}
	
	protected boolean flag = false;
	private boolean souFlag = false;
	

	protected void createBlock(ADTable adTable) {
		block = new VirtualHouseLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
	
	public int open() {
		int returnCode = super.open();
		isOpen = true;
		return returnCode;
	}
    protected Control createDialogArea(Composite parent){
		return super.createDialogArea(parent);
	}

	public boolean isSouFlag() {
		return souFlag;
	}

	public void setSouFlag(boolean souFlag) {
		this.souFlag = souFlag;
	}
}
