package com.graly.erp.inv.transfer.vehicle;

import org.eclipse.ui.PartInitException;

import com.graly.erp.inv.transfer.TransferEditor;

public class VehicleTransferEditor extends TransferEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.transfer.vehicle.VehicleTransferEditor";
	
	@Override
	protected void addPages() {
		try {
			setEditorTitle("≥µ¡æ¡Ï¡œ");
			page = new VehicleTransferEntryPage(this, "", "");
			addPage(page);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setFocus() {
		page.setFocus();
	}
}
