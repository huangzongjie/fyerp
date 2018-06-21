package com.graly.erp.inv.transfer.hy.dpk;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;

public class DpkLotTrsDialog extends DpkTransferLineLotDialog {

	public DpkLotTrsDialog(Shell shell, ADTable table) {
		super(shell, table);
	}

	public DpkLotTrsDialog(Shell shell) {
		super(shell);
	}	
	
	protected void createSection(Composite composite) {
		lotSection = new DpkLotTrsSection(table, this);
		lotSection.createContents(managedForm, composite);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
	}

}
