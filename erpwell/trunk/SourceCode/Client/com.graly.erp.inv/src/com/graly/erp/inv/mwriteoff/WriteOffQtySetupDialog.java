package com.graly.erp.inv.mwriteoff;

import java.math.BigDecimal;

import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.out.OutQtySetupDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.mes.wip.model.Lot;

public class WriteOffQtySetupDialog extends OutQtySetupDialog {
	
	public WriteOffQtySetupDialog(Shell parentShell, MovementLine outLine, Lot lot, Warehouse warehouse) {
		super(parentShell, outLine, lot, warehouse);
	}
	
	//数量可以为负，也可以大于批次数量
	protected boolean validate() {
		try {
			setErrorMessage(null);
			BigDecimal inputQty = new BigDecimal(txtOutQty.getText());
			if(BigDecimal.ZERO.compareTo(inputQty) == 0) {
				setErrorMessage(String.format(Message.getString("common.largerthan"),
						getMovementQtyLabel(), inputQty.toString()));
				return false;
			}
		} catch(Exception e) {
			setErrorMessage(Message.getString("common.input_error"));
		}
		return false;
	}

}
