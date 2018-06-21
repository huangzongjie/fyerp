package com.graly.erp.inv.in;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class InLineDialog extends ParentChildEntityBlockDialog {
	private boolean flag;
	public InLineDialog(Shell parent) {
        super(parent);
    }
	
	public InLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable,boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new InLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}
