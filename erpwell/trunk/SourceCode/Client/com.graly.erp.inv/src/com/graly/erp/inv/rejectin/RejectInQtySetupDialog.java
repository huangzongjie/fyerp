package com.graly.erp.inv.rejectin;

import java.math.BigDecimal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.otherin.InQtySetupDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.mes.wip.model.Lot;

public class RejectInQtySetupDialog extends InQtySetupDialog {

	public RejectInQtySetupDialog(Shell parentShell, MovementLine outLine,
			Lot lot, Warehouse warehouse) {
		super(parentShell, outLine, lot, warehouse);
	}
	
	//重载实现不显示库存数量
	protected void createContent(Composite parent, FormToolkit toolkit) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 13;
		// Lot ID
		toolkit.createLabel(parent, Message.getString("inv.lotid"), SWT.NULL);
		Text txtLotId = toolkit.createText(parent, lot.getLotId(), SWT.BORDER | SWT.READ_ONLY);
		txtLotId.setBackground(txtLotId.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		txtLotId.setLayoutData(gd);
		
		// Material ID
		toolkit.createLabel(parent, Message.getString("pdm.material_id"), SWT.NULL);
		Text txtMaterialId = toolkit.createText(parent, lot.getMaterialId(), SWT.BORDER | SWT.READ_ONLY);
		txtMaterialId.setBackground(txtLotId.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		txtMaterialId.setLayoutData(gd);
		
		// Out Quantity
		toolkit.createLabel(parent, getMovementQtyLabel(), SWT.NULL);
		txtOutQty = toolkit.createText(parent, "", SWT.BORDER);
		txtOutQty.setLayoutData(gd);
		txtOutQty.setTextLimit(12);
		setQtyContent();
		txtOutQty.setFocus();
		txtOutQty.selectAll();
	}

	@Override
	protected void createCurrentQtyOrStorageQtyLabel(FormToolkit toolkit,
			Composite parent) {
//		toolkit.createLabel(parent, Message.getString("inv.qty_storage"), SWT.NULL);
	}

	@Override
	protected void setCurrentQtyOrStorageQty(Text text) {
	}

	@Override
	protected String getMovementQtyLabel() {
		return Message.getString("inv.res_qty");
	}

	@Override
	protected void setQtyContent() {
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
			}
			//不检查重新入库数量必须小于Lot的当前数量
//			else if(lot.getQtyCurrent().compareTo(inputQty) < 0) {
//				setErrorMessage(String.format(Message.getString("common.lessthan"),
//						Message.getString("inv.in_qty"), lot.getQtyCurrent().toString()));
//				return false;
//			}
			return true;
		} catch(Exception e) {
			setErrorMessage(Message.getString("common.input_error"));
		}
		return false;
	}
}
