package com.graly.erp.inv.transfer.hy.dpk;

import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.out.OutQtySetupDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.mes.wip.model.Lot;

public class DpkTrsQtySetupDialog extends OutQtySetupDialog {
	
	public DpkTrsQtySetupDialog(Shell parentShell, MovementLine outLine, Lot lot, Warehouse warehouse) {
		super(parentShell, outLine, lot, warehouse);
	}
	
	@Override
	protected String getMovementQtyLabel() {
		return Message.getString("inv.trs_qty");
	}
}
