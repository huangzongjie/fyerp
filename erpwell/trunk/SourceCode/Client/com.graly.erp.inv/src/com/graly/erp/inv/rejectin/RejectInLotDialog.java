package com.graly.erp.inv.rejectin;

import java.util.List;

import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.otherin.OtherInLotDialog;

public class RejectInLotDialog extends OtherInLotDialog {

	public RejectInLotDialog(Shell shell, MovementIn in,
			MovementLine movementInLine, List<MovementLine> lines,
			boolean isView) {
		super(shell, in, movementInLine, lines, isView);
	}

	public RejectInLotDialog(Shell shell, MovementIn in,
			MovementLine movementInLine) {
		super(shell, in, movementInLine);
	}

	public RejectInLotDialog(Shell shell) {
		super(shell);
	}

}
