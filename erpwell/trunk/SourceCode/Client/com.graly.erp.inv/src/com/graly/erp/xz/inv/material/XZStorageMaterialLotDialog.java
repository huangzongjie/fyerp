package com.graly.erp.xz.inv.material;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.model.VStorageMaterial;

public class XZStorageMaterialLotDialog extends LotDialog {
	private VStorageMaterial selectedLine;
	public XZStorageMaterialLotDialog(Shell shell) {
		super(shell);
	}

	public XZStorageMaterialLotDialog(Shell shell, VStorageMaterial selectedLine) {
		super(shell);
		this.selectedLine = selectedLine;
	}

	protected void createSection(Composite composite) {
		lotSection = new XZStorageMaterialLotSection(table, this, selectedLine);
		lotSection.createContents(managedForm, composite);
	}
	
	protected boolean isSureExit() {
		return true;
	}
}
