package com.graly.erp.inv.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_USER_WAREHOUSE")
public class VUserWarehouse extends ADBase {
	private static final long serialVersionUID = 1L;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;	
	
	@Column(name="WAREHOUSE_TYPE")
	private String warehouseType;	
	
	@Column(name="IS_VIRTUAL")
	private String isVirtual;	
	
	@Column(name="DESCRIPTION")
	private String description;	
	
	@Column(name="USER_RRN")
	private Long userRrn;
	
	@Column(name="IS_DEFAULT")
	private String isDefault;

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseType() {
		return warehouseType;
	}

	public void setWarehouseType(String warehouseType) {
		this.warehouseType = warehouseType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getUserRrn() {
		return userRrn;
	}

	public void setUserRrn(Long userRrn) {
		this.userRrn = userRrn;
	}

	public String getIsVirtual() {
		return isVirtual;
	}

	public void setIsVirtual(String isVirtual) {
		this.isVirtual = isVirtual;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}
	
}
