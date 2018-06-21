package com.graly.erp.inv.workshop.delivery;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;

public class WorkShopDelLotTrsDialog extends WorkShopDeliveryLineLotDialog {

	public WorkShopDelLotTrsDialog(Shell shell, ADTable table) {
		super(shell, table);
	}

	public WorkShopDelLotTrsDialog(Shell shell) {
		super(shell);
	}	
	
	protected void createSection(Composite composite) {
		lotSection = new WorkShopDelLotTrsSection(table, this);
		lotSection.createContents(managedForm, composite);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
	}

}
