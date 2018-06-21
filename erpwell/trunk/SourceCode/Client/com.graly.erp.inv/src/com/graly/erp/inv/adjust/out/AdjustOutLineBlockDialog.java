package com.graly.erp.inv.adjust.out;

import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.out.OutLineBlockDialog;
import com.graly.framework.activeentity.model.ADTable;

public class AdjustOutLineBlockDialog extends OutLineBlockDialog {
	public AdjustOutLineBlockDialog(Shell parent) {
        super(parent);
    }
	
	public AdjustOutLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		super(parent, parentTable, whereClause, parentObject, childTable);
	}
	
	public AdjustOutLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable,boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new AdjustOutLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}
