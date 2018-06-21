package com.graly.erp.wip.model;


import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;
//3+3外购件运算结果 视图 添加 交货期等数据
@Entity
@Table(name="V_REP_PO_RESULT")
public class VRepScheResult extends ADBase{
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

	@Column(name="ONE_QTY")
	private BigDecimal oneQty;//已分配数
	
	@Column(name="TWO_QTY")
	private BigDecimal twoQty;//已分配数
	
	@Column(name="THREE_QTY")
	private BigDecimal threeQty;//已分配数
	
	@Column(name="FOUR_QTY")
	private BigDecimal fourQty;//已分配数
	
	@Column(name="FIVE_QTY")
	private BigDecimal fiveQty;//已分配数
	
	@Column(name="SIX_QTY")
	private BigDecimal sixQty;//已分配数
	
	@Column(name="SUB_QTY1")
	private BigDecimal subQty1;//已分配数
	
	@Column(name="SUB_QTY2")
	private BigDecimal subQty2;//已分配数
	
	@Column(name="DATE_PROMISED")
	private Date datePromised;
	
	
	@Column(name="COMMENTS")
	private String comments;
	
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

	public BigDecimal getOneQty() {
		return oneQty;
	}

	public void setOneQty(BigDecimal oneQty) {
		this.oneQty = oneQty;
	}

	public BigDecimal getTwoQty() {
		return twoQty;
	}

	public void setTwoQty(BigDecimal twoQty) {
		this.twoQty = twoQty;
	}

	public BigDecimal getThreeQty() {
		return threeQty;
	}

	public void setThreeQty(BigDecimal threeQty) {
		this.threeQty = threeQty;
	}

	public BigDecimal getFourQty() {
		return fourQty;
	}

	public void setFourQty(BigDecimal fourQty) {
		this.fourQty = fourQty;
	}

	public BigDecimal getFiveQty() {
		return fiveQty;
	}

	public void setFiveQty(BigDecimal fiveQty) {
		this.fiveQty = fiveQty;
	}

	public BigDecimal getSixQty() {
		return sixQty;
	}

	public void setSixQty(BigDecimal sixQty) {
		this.sixQty = sixQty;
	}

	public BigDecimal getSubQty1() {
		return subQty1;
	}

	public void setSubQty1(BigDecimal subQty1) {
		this.subQty1 = subQty1;
	}

	public BigDecimal getSubQty2() {
		return subQty2;
	}

	public void setSubQty2(BigDecimal subQty2) {
		this.subQty2 = subQty2;
	}

	public Date getDatePromised() {
		return datePromised;
	}

	public void setDatePromised(Date datePromised) {
		this.datePromised = datePromised;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
}
