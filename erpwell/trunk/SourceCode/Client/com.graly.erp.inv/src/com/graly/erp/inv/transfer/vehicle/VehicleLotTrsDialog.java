package com.graly.erp.inv.transfer.vehicle;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.transfer.LotTrsDialog;
import com.graly.framework.activeentity.model.ADTable;

public class VehicleLotTrsDialog extends LotTrsDialog {
	protected String targetWarehouseId;
	protected String sourceWarehouseId;
	protected String transType;

	public VehicleLotTrsDialog(Shell shell, String transType, String sourceWarehouseId, String targetWarehouseId) {
		super(shell);
		this.targetWarehouseId = targetWarehouseId;
		this.sourceWarehouseId = sourceWarehouseId;
		this.transType = transType;
	}

	public VehicleLotTrsDialog(Shell shell, ADTable table) {
		super(shell, table);
	}

	protected void createSection(Composite composite) {
		lotSection = new VehicleLotTrsSection(table, this, transType, sourceWarehouseId,targetWarehouseId);
		lotSection.createContents(managedForm, composite);
	}
}