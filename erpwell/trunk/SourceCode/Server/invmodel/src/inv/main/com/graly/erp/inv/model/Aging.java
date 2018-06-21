package com.graly.erp.inv.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.erp.base.model.Storage;
import com.graly.framework.activeentity.model.ADUpdatable;

/**
 * ’À¡‰ø‚¥Ê±Ì
 * */
@Entity
@Table(name="INV_AGING")
public class Aging extends ADUpdatable{
	private static final long serialVersionUID = 1L;
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;

	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="QTY")
	private BigDecimal qty;
	
	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="AGING_MONTH_COUNT")
	private Long agingMonthCount;
	
	@Column(name="AGING_MONTH")
	private String agingMonth;

	public Aging(){
		
	}
	
	public Aging(Storage storage){
		this.setMaterialRrn(storage.getMaterialRrn());
		this.setMaterialId(storage.getMaterialId());
		this.setMaterialName(storage.getMaterialName());
		this.setWarehouseId(storage.getWarehouseId());
		this.setWarehouseRrn(storage.getWarehouseRrn());
		this.setQty(storage.getQtyOnhand());
	}
	
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

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
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

	public Long getAgingMonthCount() {
		return agingMonthCount;
	}

	public void setAgingMonthCount(Long agingMonthCount) {
		this.agingMonthCount = agingMonthCount;
	}

	public String getAgingMonth() {
		return agingMonth;
	}

	public void setAgingMonth(String agingMonth) {
		this.agingMonth = agingMonth;
	}
}
