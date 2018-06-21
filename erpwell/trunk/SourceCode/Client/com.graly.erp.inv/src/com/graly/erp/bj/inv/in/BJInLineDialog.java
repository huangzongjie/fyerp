package com.graly.erp.bj.inv.in;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class BJInLineDialog extends ParentChildEntityBlockDialog {
	private boolean flag;
	public BJInLineDialog(Shell parent) {
        super(parent);
    }
	
	public BJInLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable,boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new BJInLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}
