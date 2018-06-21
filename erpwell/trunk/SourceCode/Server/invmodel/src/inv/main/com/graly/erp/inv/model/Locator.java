package com.graly.erp.inv.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="INV_LOCATOR")
public class Locator extends ADUpdatable {

	/**
	 * Jerry Wan
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name="LOCATOR_ID")
	private String locatorId;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@Column(name="IS_DEFAULT")
	private String isDefault;
	
	@Column(name="X")
	private String x;
	
	@Column(name="Y")
	private String y;
	
	@Column(name="Z")
	private String z;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;

	public String getLocatorId() {
		return locatorId;
	}

	public void setLocatorId(String locatorId) {
		this.locatorId = locatorId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseRrn(Long whrehouseRrn) {
		this.warehouseRrn = whrehouseRrn;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getZ() {
		return z;
	}

	public void setZ(String z) {
		this.z = z;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}	
}
