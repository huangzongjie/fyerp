package com.graly.erp.inv.adjust.out;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.out.OutLineLotDialog;

public class AdjustOutLineLotDialog extends OutLineLotDialog {

	public AdjustOutLineLotDialog(Shell shell, Object parent, Object child, List<MovementLine> lines, boolean isView) {
		super(shell, parent, child, lines, isView);
	}
	
	protected void createSection(Composite composite) {
		lotSection = new AdjustOutLineLotSection(table, movementOut, outLine, lines, this, isView);
		lotSection.createContents(managedForm, composite);
	}
}
