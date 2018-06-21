package com.graly.erp.wip.model;


import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;
//��ʷ ��¼��һ�������
@Entity
@Table(name="REP_MATERIAL_PO_RESULT2")
public class RepScheResult2his extends ADBase{
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
	private BigDecimal qtyTransit;//��;
	
	@Column(name="QTY_ALLOCATION")
	private BigDecimal qtyAllocation;//�ѷ�����

	@Column(name="ONE_QTY")
	private BigDecimal oneQty;//�ѷ�����
	
	@Column(name="TWO_QTY")
	private BigDecimal twoQty;//�ѷ�����
	
	@Column(name="THREE_QTY")
	private BigDecimal threeQty;//�ѷ�����
	
	@Column(name="FOUR_QTY")
	private BigDecimal fourQty;//�ѷ�����
	
	@Column(name="FIVE_QTY")
	private BigDecimal fiveQty;//�ѷ�����
	
	@Column(name="SIX_QTY")
	private BigDecimal sixQty;//�ѷ�����
	
	@Column(name="SCHEDULE_DATE2")
	private Date scheduleDate2;//�ѷ�����
	
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

	public Date getScheduleDate2() {
		return scheduleDate2;
	}

	public void setScheduleDate2(Date scheduleDate2) {
		this.scheduleDate2 = scheduleDate2;
	}
}
