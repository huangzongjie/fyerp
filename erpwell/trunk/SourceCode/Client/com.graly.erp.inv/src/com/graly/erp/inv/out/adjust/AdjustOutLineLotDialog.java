package com.graly.erp.inv.out.adjust;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.out.OutLineLotDialog;

public class AdjustOutLineLotDialog extends OutLineLotDialog {

	public AdjustOutLineLotDialog(Shell shell, Object parent, Object child, boolean isView) {
		super(shell, parent, child, isView);
	}
	
	protected void createSection(Composite composite) {
		lotSection = new AdjustOutLotSection(movementOut, outLine, table, this, isView);
		lotSection.createContents(managedForm, composite);
	}
}
