package com.graly.erp.inv.adjust.out;

import java.math.BigDecimal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.out.OutQtySetupDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.mes.wip.model.Lot;

public class AdjustOutQtySetupDialog extends OutQtySetupDialog {

	public AdjustOutQtySetupDialog(Shell parentShell, MovementLine outLine, Lot lot,
			Warehouse warehouse) {
		super(parentShell, outLine, lot, warehouse);
	}
	
	protected void createCurrentQtyOrStorageQtyLabel(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, Message.getString("inv.lot_qty"), SWT.NULL);		
	}
	
	// 设置库存数量(出库调拨时)，或当前数量(入库时Lot的当前数量)
	protected void setCurrentQtyOrStorageQty(Text text) {
		if(text == null) return;
		if(lot.getQtyCurrent() != null && BigDecimal.ZERO.compareTo(lot.getQtyCurrent()) != 0) {
			text.setText(lot.getQtyCurrent().toString());
		}
	}
	
	protected void setQtyContent() {
		// 如果Lot当前数量大于出库单行数量,则初始值为出库单行数量,否则初始值为Lot当前数量
		if(movementLine != null && lot.getQtyCurrent() != null
				&& lot.getQtyCurrent().compareTo(movementLine.getQtyMovement()) > 0) {
			txtOutQty.setText(movementLine.getQtyMovement().toString());
		}
	}
	
	// 验证输入的入库数量必须小于批次的当前数量
	protected boolean validate() {
		try {
			setErrorMessage(null);
			return true;
		} catch(Exception e) {
			setErrorMessage(Message.getString("common.input_error"));
		}
		return false;
	}
}
