package com.graly.erp.base.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="INV_STORAGE")
public class Storage extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;

	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="MATERIAL_RRN",insertable=false,updatable=false)
	private Material material;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="WAREHOUSE_RRN",insertable=false,updatable=false)
	private BWarehouse warehouse;
	
	@Column(name="QTY_ONHAND")
	private BigDecimal qtyOnhand = BigDecimal.ZERO;
	
	@Column(name="QTY_ALLOCATION")
	private BigDecimal qtyAllocation = BigDecimal.ZERO;
	
	@Column(name="QTY_TRANSIT")
	private BigDecimal qtyTransit = BigDecimal.ZERO;

	@Column(name="QTY_WRITE_OFF")
	private BigDecimal qtyWriteOff = BigDecimal.ZERO;
	
	@Column(name="QTY_DIFF")
	private BigDecimal qtyDiff = BigDecimal.ZERO;//差异数,用来维护系统中库存与实际库存的差异数,系统库存+差异数=实际库存
	
	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseRrn(Long warehouseRrn) {
		this.warehouseRrn = warehouseRrn;
	}

	public BigDecimal getQtyOnhand() {
		return qtyOnhand;
	}

	public void setQtyOnhand(BigDecimal qtyOnhand) {
		this.qtyOnhand = qtyOnhand;
	}

	public BigDecimal getQtyAllocation() {
		return qtyAllocation;
	}

	public void setQtyAllocation(BigDecimal qtyReserved) {
		this.qtyAllocation = qtyReserved;
	}

	public BigDecimal getQtyTransit() {
		return qtyTransit;
	}

	public void setQtyTransit(BigDecimal qtyOrdered) {
		this.qtyTransit = qtyOrdered;
	}

	public void setQtyWriteOff(BigDecimal qtyWriteOff) {
		this.qtyWriteOff = qtyWriteOff;
	}

	public BigDecimal getQtyWriteOff() {
		return qtyWriteOff;
	}

	public BigDecimal getQtyDiff() {
		return qtyDiff;
	}

	public void setQtyDiff(BigDecimal qtyDiff) {
		this.qtyDiff = qtyDiff;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
	
	public String getMaterialId(){
		return material == null ? "" : material.getMaterialId();
	}
	
	public void setMaterialName(String name){
		
	}
	
	public String getMaterialName(){
		return material == null ? "" : material.getName();
	}
	
	public void setWarehouseId(String id){
		
	}
	
	public String getWarehouseId(){
		return warehouse == null ? "" : warehouse.getWarehouseId();
	}
}
