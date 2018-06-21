package com.graly.erp.inv.transfer.vehicle;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.transfer.LotTrsSection;
import com.graly.erp.inv.transfer.TransferLineLotDialog;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.runtime.Framework;

public class VehicleLotTrsSection extends LotTrsSection {
	public static final String SHIP_TYPE = "车辆领料";
	public static final String BACKSHIP_TYPE = "车辆领料";
	protected String targetWarehouseId;
	protected String sourceWarehouseId;
	protected String transType;

	public VehicleLotTrsSection(ADTable adTable,
			TransferLineLotDialog parentDialog, String transType, String sourceWarehouseId,String targetWarehouseId) {
		super(adTable, parentDialog);
		this.targetWarehouseId = targetWarehouseId;
		this.sourceWarehouseId = sourceWarehouseId;
		this.transType = transType;
	}
	
	@Override
	protected void createPreWarehouseContent(Composite parent,
			FormToolkit toolkit) throws Exception {
		super.createPreWarehouseContent(parent, toolkit);
		INVManager invManager = Framework.getService(INVManager.class);
		Warehouse warehouse = invManager.getWarehouseById(sourceWarehouseId, Env.getOrgRrn());
		if(warehouse != null && warehouse.getObjectRrn() != null){
			preRefField.setValue(warehouse.getObjectRrn());
			preRefField.refresh();
		}
	}
	
	@Override
	protected void createDestWarehouseContent(Composite parent,
			FormToolkit toolkit) throws Exception {
		super.createDestWarehouseContent(parent, toolkit);
		INVManager invManager = Framework.getService(INVManager.class);
		Warehouse warehouse = invManager.getWarehouseById(targetWarehouseId, Env.getOrgRrn());
		if(warehouse != null && warehouse.getObjectRrn() != null){
			destRefField.setValue(warehouse.getObjectRrn());
			destRefField.refresh();
		}
	}

	@Override
	protected void createTrsTypeContent(Composite parent, FormToolkit toolkit)
			throws Exception {
		super.createTrsTypeContent(parent, toolkit);
		trsTypeField.setValue(transType);
		trsTypeField.setEnabled(false);
		trsTypeField.refresh();
	}
}