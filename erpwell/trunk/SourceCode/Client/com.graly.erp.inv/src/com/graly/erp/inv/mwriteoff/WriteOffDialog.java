package com.graly.erp.inv.mwriteoff;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;

public class WriteOffDialog extends LotDialog {

	public static final String TableName = "INVMovementLineLot";
	
	public WriteOffDialog(Shell shell, ADTable table) {
		super(shell, table);
	}

	public WriteOffDialog(Shell shell) {
		super(shell);
	}	
	
	protected void createSection(Composite composite) {
		lotSection = new WriteOffSection(table, this);
		lotSection.createContents(managedForm, composite);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
	}

	public String getADTableName() {
		return TableName;
	}
	
}
