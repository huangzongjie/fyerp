package com.graly.erp.pur.po.down;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;


public class PoDownLineBlockDialog extends ParentChildEntityBlockDialog  {
	private boolean flag=false;
	public PoDownLineBlockDialog(Shell parent) {
        super(parent);
    }
	
	public PoDownLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		super(parent, parentTable, whereClause, parentObject, childTable);
	}
	
	public PoDownLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable,boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag=flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new PoDownLineEntityBlock(table, getParentObject(), whereClause, childTable,flag);
	}
	
}
