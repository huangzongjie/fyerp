package com.graly.erp.inv.adjust.in;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class AdjustInLineDialog extends ParentChildEntityBlockDialog {
	protected boolean flag;
	
	public AdjustInLineDialog(Shell parent) {
        super(parent);
    }
	
	public AdjustInLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable, boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new AdjustInLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}
