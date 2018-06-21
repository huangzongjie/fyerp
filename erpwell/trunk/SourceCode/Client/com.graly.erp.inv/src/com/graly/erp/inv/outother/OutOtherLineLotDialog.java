package com.graly.erp.inv.outother;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.out.OutLineLotDialog;

public class OutOtherLineLotDialog extends OutLineLotDialog {

	public OutOtherLineLotDialog(Shell shell, Object parent, Object child, boolean isView) {
		super(shell, parent, child, isView);
	}
	
	protected void createSection(Composite composite) {
		lotSection = new OutOtherLintLotSection(movementOut, outLine, table, this, isView);
		lotSection.createContents(managedForm, composite);
	}
}
