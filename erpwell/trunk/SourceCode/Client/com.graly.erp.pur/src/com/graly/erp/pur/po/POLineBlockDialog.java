package com.graly.erp.pur.po;

import org.eclipse.swt.widgets.Shell;
import com.graly.erp.pur.request.ColorEntityTableManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class POLineBlockDialog extends ParentChildEntityBlockDialog {
	private boolean flag=false;
	public POLineBlockDialog(Shell parent) {
        super(parent);
    }
	
	public POLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		super(parent, parentTable, whereClause, parentObject, childTable);
	}
	
	public POLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable,boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag=flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new POLineEntityBlock(table, getParentObject(), whereClause, childTable,flag);
	}
}