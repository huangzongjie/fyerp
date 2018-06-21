package com.graly.erp.base.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="BAS_CODE_WAREHOUSE")
public class CodeWarehouse extends ADBase {
	private static final long serialVersionUID = 1L;

	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@OneToOne
	@JoinColumn(name="WAREHOUSE_RRN", insertable=false, updatable=false)
	private BWarehouse warehouse;
	
	@Column(name = "HOUSE_CODE")
	private String houseCode;

	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseRrn(Long warehouseRrn) {
		this.warehouseRrn = warehouseRrn;
	}

	public BWarehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(BWarehouse warehouse) {
		this.warehouse = warehouse;
	}

	public String getHouseCode() {
		return houseCode;
	}

	public void setHouseCode(String houseCode) {
		this.houseCode = houseCode;
	}
}
