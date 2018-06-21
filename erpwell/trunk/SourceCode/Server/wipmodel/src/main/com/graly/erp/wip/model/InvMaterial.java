package com.graly.erp.wip.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class InvMaterial implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long materialRrn;
	private String materialId;
	private String materialName;
	private BigDecimal qtyMin = BigDecimal.ZERO;
	private BigDecimal qtyMax = BigDecimal.ZERO;
	private BigDecimal qtyInvtory = BigDecimal.ZERO;
	private BigDecimal qtyAllocation = BigDecimal.ZERO;
	private BigDecimal qtyOnHand = BigDecimal.ZERO;
	private BigDecimal qtyTransit = BigDecimal.ZERO;
	private BigDecimal qtyMoLine = BigDecimal.ZERO;
	private BigDecimal qtyReceive = BigDecimal.ZERO;
	private BigDecimal qtyWip = BigDecimal.ZERO;
	private BigDecimal qtyMinProduct = BigDecimal.ZERO;
	
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
	
	public BigDecimal getQtyMin() {
		return qtyMin;
	}
	
	public void setQtyMin(BigDecimal qtyMin) {
		this.qtyMin = qtyMin;
	}
	
	public BigDecimal getQtyMax() {
		return qtyMax;
	}
	
	public void setQtyMax(BigDecimal qtyMax) {
		this.qtyMax = qtyMax;
	}
	
	public BigDecimal getQtyInvtory() {
		return qtyInvtory;
	}
	
	public void setQtyInvtory(BigDecimal qtyInvtory) {
		this.qtyInvtory = qtyInvtory;
	}
	
	public BigDecimal getQtyAllocation() {
		return qtyAllocation;
	}
	
	public void setQtyAllocation(BigDecimal qtyAllocation) {
		this.qtyAllocation = qtyAllocation;
	}
	
	public BigDecimal getQtyOnHand() {
		return qtyOnHand;
	}
	
	public void setQtyOnHand(BigDecimal qtyOnHand) {
		this.qtyOnHand = qtyOnHand;
	}
	
	public void setQtyTransit(BigDecimal qtyTransit) {
		this.qtyTransit = qtyTransit;
	}

	public BigDecimal getQtyTransit() {
		return qtyTransit;
	}
	
	public void setQtyMoLine(BigDecimal qtyMoLine) {
		this.qtyMoLine = qtyMoLine;
	}

	public BigDecimal getQtyMoLine() {
		return qtyMoLine;
	}
	
	public BigDecimal getQtyReceive() {
		return qtyReceive;
	}
	
	public void setQtyReceive(BigDecimal qtyReceive) {
		this.qtyReceive = qtyReceive;
	}

	public void setQtyWip(BigDecimal qtyWip) {
		this.qtyWip = qtyWip;
	}

	public BigDecimal getQtyWip() {
		return qtyWip;
	}
	
	public void setQtyMinProduct(BigDecimal qtyMinProduct) {
		this.qtyMinProduct = qtyMinProduct;
	}

	public BigDecimal getQtyMinProduct() {
		return qtyMinProduct;
	}

}
