package com.graly.erp.inv.racklot;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.base.ui.util.Message;

public class QtyTrfSetupDialog extends QtySetupDialog {

	public QtyTrfSetupDialog(Shell parentShell) {
		super(parentShell);
	}

	

	@Override
	protected String getMovementQtyLabel() {
		return Message.getString("inv.trs_qty");
	}
}
