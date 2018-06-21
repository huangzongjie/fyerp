package com.graly.erp.inv.split;

import java.math.BigDecimal;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.mes.wip.model.Lot;

public class SplitLotDialog extends LotDialog {
	private BigDecimal splitQty;
	private Lot lot;

	public SplitLotDialog(Shell shell) {
		super(shell);
	}
	
	public SplitLotDialog(Shell shell, Lot lot, BigDecimal splitQty){
		this(shell);
		this.lot = lot;
		this.splitQty = splitQty;
	}
	
	protected void createSection(Composite composite) {
		lotSection = new SplitLotSection(table, this, lot, splitQty);
		lotSection.createContents(managedForm, composite);
	}
	
	protected boolean isSureExit() {
		return true;
	}
}
