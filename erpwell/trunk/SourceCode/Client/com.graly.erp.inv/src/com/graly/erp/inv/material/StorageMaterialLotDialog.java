package com.graly.erp.inv.material;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.model.VStorageMaterial;

public class StorageMaterialLotDialog extends LotDialog {
	private VStorageMaterial selectedLine;
	public StorageMaterialLotDialog(Shell shell) {
		super(shell);
	}

	public StorageMaterialLotDialog(Shell shell, VStorageMaterial selectedLine) {
		super(shell);
		this.selectedLine = selectedLine;
	}

	protected void createSection(Composite composite) {
		lotSection = new StorageMaterialLotSection(table, this, selectedLine);
		lotSection.createContents(managedForm, composite);
	}
	
	protected boolean isSureExit() {
		return true;
	}
}
