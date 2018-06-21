package com.graly.erp.inv.workshop.delivery;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class WorkShopDeliveryLineDialog extends ParentChildEntityBlockDialog {
	private boolean flag;

	public WorkShopDeliveryLineDialog(Shell parent) {
        super(parent);
    }
	
	public WorkShopDeliveryLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		this(parent, parentTable, whereClause, parentObject, childTable, false);
	}
	
	public WorkShopDeliveryLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable, boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new WorkShopDeliveryLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}
