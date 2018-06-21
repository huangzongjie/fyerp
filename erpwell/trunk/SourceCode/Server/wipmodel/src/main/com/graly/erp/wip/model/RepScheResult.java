package com.graly.erp.wip.model;


import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="REP_SCHE_RESULT")
public class RepScheResult extends ADBase{
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="TEN_QTY")
	private BigDecimal tenQty;
	
	@Column(name="SEVEN_QTY")
	private BigDecimal sevenQty;
	
	@Column(name="ONHAND_QTY")
	private BigDecimal onhandQty;
	
	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="QTY_MIN")
	private String qtyMin;
	
	@Column(name="PURCHASE")
	private String purchase;
	 
	@Column(name="VENDOR_ID")
	private String vendorId;
	
	@Column(name="VENDOR_NAME")
	private String vendorName;
	
	@Column(name="REFERENCED_PRICE")
	private BigDecimal referencedPrice;

	@Column(name="QTY_TRANSIT")
	private BigDecimal qtyTransit;//在途
	
	@Column(name="QTY_ALLOCATION")
	private BigDecimal qtyAllocation;//已分配数

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

	public BigDecimal getTenQty() {
		return tenQty;
	}

	public void setTenQty(BigDecimal tenQty) {
		this.tenQty = tenQty;
	}

	public BigDecimal getSevenQty() {
		return sevenQty;
	}

	public void setSevenQty(BigDecimal sevenQty) {
		this.sevenQty = sevenQty;
	}

	public BigDecimal getOnhandQty() {
		return onhandQty;
	}

	public void setOnhandQty(BigDecimal onhandQty) {
		this.onhandQty = onhandQty;
	}

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public String getQtyMin() {
		return qtyMin;
	}

	public void setQtyMin(String qtyMin) {
		this.qtyMin = qtyMin;
	}

	public String getPurchase() {
		return purchase;
	}

	public void setPurchase(String purchase) {
		this.purchase = purchase;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public BigDecimal getReferencedPrice() {
		return referencedPrice;
	}

	public void setReferencedPrice(BigDecimal referencedPrice) {
		this.referencedPrice = referencedPrice;
	}

	public BigDecimal getQtyTransit() {
		return qtyTransit;
	}

	public void setQtyTransit(BigDecimal qtyTransit) {
		this.qtyTransit = qtyTransit;
	}

	public BigDecimal getQtyAllocation() {
		return qtyAllocation;
	}

	public void setQtyAllocation(BigDecimal qtyAllocation) {
		this.qtyAllocation = qtyAllocation;
	}
 
}
