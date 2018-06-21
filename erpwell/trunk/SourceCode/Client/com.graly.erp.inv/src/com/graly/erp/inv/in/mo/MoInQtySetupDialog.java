package com.graly.erp.inv.in.mo;

import java.math.BigDecimal;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.otherin.InQtySetupDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.mes.wip.model.Lot;

public class MoInQtySetupDialog extends InQtySetupDialog {
	private BigDecimal inQty = BigDecimal.ZERO;

	public MoInQtySetupDialog(Shell parentShell, MovementLine outLine, Lot lot, 
			Warehouse warehouse) {
		super(parentShell, outLine, lot, warehouse);
	}
	
	// 如果为Material或Batch类型，则从核销仓库中获得可以入库的数量
	protected void setCurrentQtyOrStorageQty(Text text) {
		if(text == null) return;
		inQty = getQtyOnhand();
		text.setText(inQty.toString());
//		text.setText(lot.getQtyTransaction().toString());
	}

	// 当前数量(即可入库数量)=工作令已完成数量-工作令已入库数量
	protected void setQtyContent() {
		if(BigDecimal.ZERO.compareTo(inQty) == 0) {
			inQty = getQtyOnhand();
			txtOutQty.setText(inQty.toString());			
		} else {
			txtOutQty.setText(inQty.toString());
		}
	}
	
	// 验证输入的入库数量必须小于批次的当前数量
	protected boolean validate() {
		try {
			setErrorMessage(null);
			BigDecimal inputQty = new BigDecimal(txtOutQty.getText());
			if(BigDecimal.ZERO.compareTo(inputQty) == 0) {
				setErrorMessage(String.format(Message.getString("common.largerthan"),
						Message.getString("inv.in_qty"), inputQty.toString()));
				return false;
			} else if(inQty.compareTo(inputQty) < 0) {
				setErrorMessage(String.format(Message.getString("common.lessthan"),
						Message.getString("inv.in_qty"), inQty.toString()));
				return false;
			}
			return true;
		} catch(Exception e) {
			setErrorMessage(Message.getString("common.input_error"));
		}
		return false;
	}
}
