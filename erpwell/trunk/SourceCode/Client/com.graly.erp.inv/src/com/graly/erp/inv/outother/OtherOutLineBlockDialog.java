package com.graly.erp.inv.outother;

import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.out.OutLineBlockDialog;
import com.graly.framework.activeentity.model.ADTable;

public class OtherOutLineBlockDialog extends OutLineBlockDialog {
	public OtherOutLineBlockDialog(Shell parent) {
        super(parent);
    }
	
	public OtherOutLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		super(parent, parentTable, whereClause, parentObject, childTable);
	}
	
	public OtherOutLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable,boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new OtherOutLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}
