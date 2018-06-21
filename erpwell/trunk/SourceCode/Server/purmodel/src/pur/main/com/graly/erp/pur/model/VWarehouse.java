package com.graly.erp.pur.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_PUR_WAREHOUSE")
public class VWarehouse extends ADBase{
	private static final long serialVersionUID = 1L;
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}
}
