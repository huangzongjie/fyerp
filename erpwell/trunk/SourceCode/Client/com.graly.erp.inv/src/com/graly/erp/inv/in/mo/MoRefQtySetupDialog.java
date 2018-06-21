package com.graly.erp.inv.in.mo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.Warehouse;
import com.graly.framework.base.ui.util.Message;
import com.graly.mes.wip.model.Lot;

public class MoRefQtySetupDialog extends MoInQtySetupDialog {

	public MoRefQtySetupDialog(Shell parentShell, MovementLine outLine,
			Lot lot, Warehouse warehouse) {
		super(parentShell, outLine, lot, warehouse);
	}

	// 库存数量
	protected void createCurrentQtyOrStorageQtyLabel(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, Message.getString("inv.qty_storage"), SWT.NULL);		
	}
	
	// 退库数量
	@Override
	protected String getMovementQtyLabel() {
		return Message.getString("inv.refund_qty");
	}
}
