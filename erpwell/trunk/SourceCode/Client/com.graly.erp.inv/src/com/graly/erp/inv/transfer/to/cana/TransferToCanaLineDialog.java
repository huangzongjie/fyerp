package com.graly.erp.inv.transfer.to.cana;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;

public class TransferToCanaLineDialog extends ParentChildEntityBlockDialog {
	private boolean flag;

	public TransferToCanaLineDialog(Shell parent) {
        super(parent);
    }
	
	public TransferToCanaLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		this(parent, parentTable, whereClause, parentObject, childTable, false);
	}
	
	public TransferToCanaLineDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable, boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new TransferToCanaLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}
