package com.graly.erp.inv.out.adjust.outwarehouse;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.out.adjust.ByLotAdjustOutDialog;

public class OutWarehouseAdjustDialog extends ByLotAdjustOutDialog {

	public OutWarehouseAdjustDialog(Shell shell) {
		super(shell);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void createSection(Composite composite) {
		lotSection = new OutWarehouseByLotAdjustOutSection(table, this);
		lotSection.createContents(managedForm, composite);
	}
	

}
