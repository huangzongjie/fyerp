package com.graly.erp.inv.workshop.requisition;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;

public class WorkShopReqLotTrsDialog extends WorkShopRequestionLineLotDialog {

	public WorkShopReqLotTrsDialog(Shell shell, ADTable table) {
		super(shell, table);
	}

	public WorkShopReqLotTrsDialog(Shell shell) {
		super(shell);
	}	
	
	protected void createSection(Composite composite) {
		lotSection = new WorkShopReqLotTrsSection(table, this);
		lotSection.createContents(managedForm, composite);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
	}

}
