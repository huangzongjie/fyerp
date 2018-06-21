package com.graly.erp.inv.adjust.out;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.out.OutLineLotDialog;

public class ByLotOutDialog extends OutLineLotDialog {
	
	public ByLotOutDialog(Shell shell) {
		super(shell);
	}
	
	protected void createSection(Composite composite) {
		lotSection = new ByLotOutSection(table, this);
		lotSection.createContents(managedForm, composite);
	}
}
