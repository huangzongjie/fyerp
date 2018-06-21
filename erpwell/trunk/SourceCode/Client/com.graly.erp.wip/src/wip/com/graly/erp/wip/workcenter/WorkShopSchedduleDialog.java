package com.graly.erp.wip.workcenter;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.ManagedForm;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Message;
/**
 * @author Administrator
 * ≥µº‰≈≈≥Ã
 */
public class WorkShopSchedduleDialog extends ParentChildEntityBlockDialog {
	protected ManagedForm managedForm;
	
	public WorkShopSchedduleDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		super(parent, parentTable, whereClause, parentObject, childTable);
	}
	
	public WorkShopSchedduleDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable,boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
	}
	
	protected void createBlock(ADTable adTable) {
		EntityTableManager tableManager = new EntityTableManager(adTable);
		tableManager.setStyle(SWT.CHECK | SWT.FULL_SELECTION);
		block = new WorkShopScheduleEntityBlock(tableManager,whereClause, getParentObject());
	}
	
 
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.cancel"), false);
	}
}
