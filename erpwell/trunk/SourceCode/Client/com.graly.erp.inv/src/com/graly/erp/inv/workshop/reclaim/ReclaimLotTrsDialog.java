package com.graly.erp.inv.workshop.reclaim;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;

public class ReclaimLotTrsDialog extends WorkShopReclaimLineLotDialog {

	public ReclaimLotTrsDialog(Shell shell, ADTable table) {
		super(shell, table);
	}

	public ReclaimLotTrsDialog(Shell shell) {
		super(shell);
	}	
	
	protected void createSection(Composite composite) {
		lotSection = new ReclaimLotTrsSection(table, this);
		lotSection.createContents(managedForm, composite);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
	}

}
