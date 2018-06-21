package com.graly.erp.wip.workcenter.movement;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class WCTMovementLineDialog extends ParentChildEntityBlockDialog {
	protected boolean flag;
	
	public WCTMovementLineDialog(Shell parent) {
        super(parent);
    }
	
	public WCTMovementLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		super(parent, parentTable, whereClause, parentObject, childTable);
	}
	
	public WCTMovementLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable,boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag=flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new WCTMovementLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}

