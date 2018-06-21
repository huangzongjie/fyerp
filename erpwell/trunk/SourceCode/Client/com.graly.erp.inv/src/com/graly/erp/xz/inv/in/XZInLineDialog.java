package com.graly.erp.xz.inv.in;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class XZInLineDialog extends ParentChildEntityBlockDialog {
	private boolean flag;
	public XZInLineDialog(Shell parent) {
        super(parent);
    }
	
	public XZInLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable,boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new XZInLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}
