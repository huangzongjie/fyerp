package com.graly.erp.inv.workshop.services.storage;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class ServicesStorageLineDialog extends ParentChildEntityBlockDialog {
	private boolean flag;

	public ServicesStorageLineDialog(Shell parent) {
        super(parent);
    }
	
	public ServicesStorageLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		this(parent, parentTable, whereClause, parentObject, childTable, false);
	}
	
	public ServicesStorageLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable, boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new ServicesStorageLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}
