package com.graly.erp.inv.out.adjust.inwaehouser;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.out.adjust.ByLotAdjustOutDialog;

public class InWarehouseAdjustDialog extends ByLotAdjustOutDialog {

	public InWarehouseAdjustDialog(Shell shell) {
		super(shell);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void createSection(Composite composite) {
		lotSection = new InWarehouseByLotAdjustOutSection(table, this);
		lotSection.createContents(managedForm, composite);
	}
	

}
