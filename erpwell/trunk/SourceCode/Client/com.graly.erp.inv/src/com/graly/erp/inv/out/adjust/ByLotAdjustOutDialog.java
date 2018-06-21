package com.graly.erp.inv.out.adjust;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.out.OutLineLotDialog;

public class ByLotAdjustOutDialog extends OutLineLotDialog {
	
	public ByLotAdjustOutDialog(Shell shell) {
		super(shell);
	}
	
	protected void createSection(Composite composite) {
		lotSection = new ByLotAdjustOutSection(table, this);
		lotSection.createContents(managedForm, composite);
	}
}
