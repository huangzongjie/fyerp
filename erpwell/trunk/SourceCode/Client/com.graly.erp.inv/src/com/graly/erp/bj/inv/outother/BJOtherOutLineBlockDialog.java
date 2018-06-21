package com.graly.erp.bj.inv.outother;

import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.out.OutLineBlockDialog;
import com.graly.framework.activeentity.model.ADTable;

public class BJOtherOutLineBlockDialog extends OutLineBlockDialog {
	public BJOtherOutLineBlockDialog(Shell parent) {
        super(parent);
    }
	
	public BJOtherOutLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		super(parent, parentTable, whereClause, parentObject, childTable);
	}
	
	public BJOtherOutLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable,boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new BJOtherOutLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}
