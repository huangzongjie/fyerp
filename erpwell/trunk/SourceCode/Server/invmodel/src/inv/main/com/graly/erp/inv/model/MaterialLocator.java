package com.graly.erp.inv.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="INV_MATERIAL_LOCATOR")
public class MaterialLocator extends ADUpdatable {
	private static final long serialVersionUID = 1L;

	@Column(name="MATERIAL_RRN")
	private Long materialRrn;

	@Column(name="MATERIAL_ID")
	private String materialId;

	@Column(name="MATERIAL_NAME")
	private String materialName;

	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;

	@Column(name="WAREHOUSE_ID")
	private String warehouseId;

	@Column(name="LOCATOR_RRN")
	private Long locatorRrn;
	
	@Column(name="LOCATOR_ID")
	private String locatorId;

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseRrn(Long warehouseRrn) {
		this.warehouseRrn = warehouseRrn;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getLocatorId() {
		return locatorId;
	}

	public void setLocatorId(String locatorId) {
		this.locatorId = locatorId;
	}

	public Long getLocatorRrn() {
		return locatorRrn;
	}

	public void setLocatorRrn(Long locatorRrn) {
		this.locatorRrn = locatorRrn;
	}
}
