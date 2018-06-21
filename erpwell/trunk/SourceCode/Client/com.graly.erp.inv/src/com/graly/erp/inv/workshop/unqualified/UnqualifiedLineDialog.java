package com.graly.erp.inv.workshop.unqualified;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class UnqualifiedLineDialog extends ParentChildEntityBlockDialog {
	private boolean flag;

	public UnqualifiedLineDialog(Shell parent) {
        super(parent);
    }
	
	public UnqualifiedLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		this(parent, parentTable, whereClause, parentObject, childTable, false);
	}
	
	public UnqualifiedLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable, boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new UnqualifiedLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}
