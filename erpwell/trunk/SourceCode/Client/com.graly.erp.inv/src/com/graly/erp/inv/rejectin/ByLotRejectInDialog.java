package com.graly.erp.inv.rejectin;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.otherin.ByLotInDialog;

public class ByLotRejectInDialog extends ByLotInDialog {
	
	public ByLotRejectInDialog(Shell shell, MovementIn.InType inType) {
		super(shell, inType);
	}
	
	protected void createSection(Composite composite) {
		lotSection = new ByLotRejectInSection(table, this, inType);
		lotSection.createContents(managedForm, composite);
	}
}
