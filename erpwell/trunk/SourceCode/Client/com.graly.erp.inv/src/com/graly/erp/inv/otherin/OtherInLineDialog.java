package com.graly.erp.inv.otherin;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class OtherInLineDialog extends ParentChildEntityBlockDialog {
	protected boolean flag;
	
	public OtherInLineDialog(Shell parent) {
        super(parent);
    }
	
	public OtherInLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable, boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new OtherInLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}
