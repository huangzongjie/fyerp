package com.graly.erp.inv.mo.consume;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.framework.activeentity.model.ADTable;

public class MaterialWriteOffDialog extends LotDialog{
	public static final String TableName = "WriteOffMovement";
	public MaterialWriteOffDialog(Shell shell, ADTable table) {
		super(shell, table);
	}

	public MaterialWriteOffDialog(Shell shell) {
		super(shell);
	}	
	protected void createSection(Composite composite) {
		lotSection = new MaterialWriteSection(table, this);
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
