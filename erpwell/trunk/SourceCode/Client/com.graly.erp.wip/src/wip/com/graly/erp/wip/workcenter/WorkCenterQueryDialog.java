package com.graly.erp.wip.workcenter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;

public class WorkCenterQueryDialog extends EntityQueryDialog {

	public WorkCenterQueryDialog(Shell parent, EntityTableManager tableManager,
			IRefresh refresh) {
		super(parent, tableManager, refresh);
	}
	
	@Override
	
	protected void createDialogForm(Composite composite) {
		if(getTableManager() != null){
			queryForm = new QueryFormCalReset(composite, SWT.NONE, getTableManager().getADTable());
			queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		}
	}
}
