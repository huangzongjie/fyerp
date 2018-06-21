package com.graly.erp.inv.transfer.to.cana;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;

public class LotTrsDialog extends TransferToCanaLineLotDialog {

	public LotTrsDialog(Shell shell, ADTable table) {
		super(shell, table);
	}

	public LotTrsDialog(Shell shell) {
		super(shell);
	}	
	
	protected void createSection(Composite composite) {
		lotSection = new LotTrsSection(table, this);
		lotSection.createContents(managedForm, composite);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
	}

}
