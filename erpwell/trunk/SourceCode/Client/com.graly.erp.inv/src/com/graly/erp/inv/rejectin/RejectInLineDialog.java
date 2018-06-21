package com.graly.erp.inv.rejectin;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.otherin.OtherInLineDialog;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Message;

public class RejectInLineDialog extends OtherInLineDialog {

	public RejectInLineDialog(Shell parent, ADTable parentTable,
			String whereClause, Object parentObject, ADTable childTable,
			boolean flag) {
		super(parent, parentTable, whereClause, parentObject, childTable, flag);
	}

	public RejectInLineDialog(Shell parent) {
		super(parent);
	}
	
	protected void createBlock(ADTable adTable) {
		block = new RejectInLineBlock(table, getParentObject(), whereClause, childTable, flag);
	}
	
	// 重载实现标题的提示信息的改变inv.reject_in
	@Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite)super.createDialogArea(parent);
        try{
        	String dialogTitle = String.format(Message.getString("common.editor"),
					Message.getString("inv.reject_in"));
			setTitle(dialogTitle);
		} catch (Exception e){
		}
		return composite;
    }
}
