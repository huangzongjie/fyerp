package com.graly.erp.inv.outdevelop;

import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.out.OutLineBlockDialog;
import com.graly.erp.inv.outother.OtherOutLineEntryBlock;
import com.graly.framework.activeentity.model.ADTable;

public class DevelopOutLineBlockDialog extends OutLineBlockDialog {
	public DevelopOutLineBlockDialog(Shell parent) {
        super(parent);
    }
	
	public DevelopOutLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		super(parent, parentTable, whereClause, parentObject, childTable);
	}
	
	public DevelopOutLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable,boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new DevelopOutLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}