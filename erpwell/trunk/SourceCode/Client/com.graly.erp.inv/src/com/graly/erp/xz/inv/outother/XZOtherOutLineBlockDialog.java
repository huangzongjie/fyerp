package com.graly.erp.xz.inv.outother;

import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.out.OutLineBlockDialog;
import com.graly.framework.activeentity.model.ADTable;

public class XZOtherOutLineBlockDialog extends OutLineBlockDialog {
	public XZOtherOutLineBlockDialog(Shell parent) {
        super(parent);
    }
	
	public XZOtherOutLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		super(parent, parentTable, whereClause, parentObject, childTable);
	}
	
	public XZOtherOutLineBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable,boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
		this.flag = flag;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new XZOtherOutLineEntryBlock(table, getParentObject(), whereClause, childTable, flag);
	}
}
