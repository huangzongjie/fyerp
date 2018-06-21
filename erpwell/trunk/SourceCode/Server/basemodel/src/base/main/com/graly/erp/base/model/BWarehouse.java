package com.graly.erp.base.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="INV_WAREHOUSE")
public class BWarehouse extends ADUpdatable {

	private static final long serialVersionUID = 1L;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="WAREHOUSE_TYPE")
	private String warehouseType;
	
	@Column(name="DEFAULT_LOCATOR_RRN")
	private Long defaultLocatorRrn;
	
	@Column(name="IS_MRP")
	private String isMrp;	
	
	@Column(name="IS_COSTING")
	private String isCosting;
	
	@Column(name="IS_VIRTUAL")
	private String isVirtual;
	
	@Column(name="IS_DEFAULT")
	private String isDefault;
	
	@Column(name="IS_WRITE_OFF")
	private String isWriteOff;

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWarehouseType() {
		return warehouseType;
	}

	public void setWarehouseType(String warehouseType) {
		this.warehouseType = warehouseType;
	}

	public Long getDefaultLocatorRrn() {
		return defaultLocatorRrn;
	}

	public void setDefaultLocatorRrn(Long defaultLocatorRrn) {
		this.defaultLocatorRrn = defaultLocatorRrn;
	}

	public Boolean getIsMrp(){
		return "Y".equalsIgnoreCase(this.isMrp) ? true : false; 
	}

	public void setIsMrp(Boolean isMrp) {
		this.isMrp = isMrp ? "Y" : "N";
	}

	public Boolean getIsCosting() {
		return "Y".equalsIgnoreCase(this.isCosting) ? true : false; 
	}
	
	public void setIsCosting(Boolean isCosting) {
		this.isCosting = isCosting ? "Y" : "N";
	}

	public String getIsVirtual() {
		return isVirtual;
	}

	public void setIsVirtual(String isVirtual) {
		this.isVirtual = isVirtual;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public String getIsDefault() {
		return isDefault;
	}
	
	public Boolean getIsWriteOff() {
		return "Y".equalsIgnoreCase(this.isWriteOff) ? true : false; 
	}
	
	public void setIsWriteOff(Boolean isWriteOff) {
		this.isWriteOff = isWriteOff ? "Y" : "N";
	}

}
