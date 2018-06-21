package com.graly.erp.inv.workshop.unqualified;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;

public class UnqualifiedLotTrsDialog extends UnqualifiedLineLotDialog {

	public UnqualifiedLotTrsDialog(Shell shell, ADTable table) {
		super(shell, table);
	}

	public UnqualifiedLotTrsDialog(Shell shell) {
		super(shell);
	}	
	
	protected void createSection(Composite composite) {
		lotSection = new UnqualifiedLotTrsSection(table, this);
		lotSection.createContents(managedForm, composite);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
	}

}
