package com.graly.erp.inv.out.adjust.sell;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.out.adjust.ByLotAdjustOutDialog;

public class SellAdjustDialog extends ByLotAdjustOutDialog {

	public SellAdjustDialog(Shell shell) {
		super(shell);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void createSection(Composite composite) {
		lotSection = new SellByLotAdjustOutSection(table, this);
		lotSection.createContents(managedForm, composite);
	}
	

}
