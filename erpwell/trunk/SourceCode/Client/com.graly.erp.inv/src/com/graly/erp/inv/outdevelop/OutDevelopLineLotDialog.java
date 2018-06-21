package com.graly.erp.inv.outdevelop;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.out.OutLineLotDialog;
import com.graly.erp.inv.outother.OutOtherLintLotSection;

public class OutDevelopLineLotDialog extends OutLineLotDialog {

	public OutDevelopLineLotDialog(Shell shell, Object parent, Object child, boolean isView) {
		super(shell, parent, child, isView);
	}
	
	protected void createSection(Composite composite) {
		lotSection = new OutDevelopLineLotSection(movementOut, outLine, table, this, isView);
		lotSection.createContents(managedForm, composite);
	}
}