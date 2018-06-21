package com.graly.erp.inv.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="SAL_WAREHOUSE_MAP")
public class WarehouseMap extends ADBase {
	
	@Column(name="SAL_WAREHOUSE_ID")
	private String salWarehouseId;
	
	@Column(name="ERP_WAREHOUSE_ID")
	private String erpWarehouseId;

	public void setSalWarehouseId(String salWarehouseId) {
		this.salWarehouseId = salWarehouseId;
	}

	public String getSalWarehouseId() {
		return salWarehouseId;
	}

	public void setErpWarehouseId(String erpWarehouseId) {
		this.erpWarehouseId = erpWarehouseId;
	}

	public String getErpWarehouseId() {
		return erpWarehouseId;
	}



}
