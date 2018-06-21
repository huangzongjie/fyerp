package com.graly.erp.inv.otherin;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.model.MovementIn;

public class ByLotInDialog extends OtherInLotDialog {

	public ByLotInDialog(Shell shell, MovementIn.InType inType) {
		super(shell);
		this.inType = inType;
	}
	
	protected void createSection(Composite composite) {
		lotSection = new ByLotInSection(table, this, inType);
		lotSection.createContents(managedForm, composite);
	}
}
