package com.graly.erp.wip.model;


import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
//4+4外购件运算结果 视图 添加 交货期等数据
//4+4用起来后，3+3就没必要进行使用
@Entity
@Table(name="V_REP_PO_RESULT2")
public class VRepScheResult2 extends ADUpdatable{
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="JSQTY")
	private BigDecimal jsqty;
	
	@Column(name="WJSQTY")
	private BigDecimal wjsqty;
	
	@Column(name="QTY")
	private BigDecimal qty;
	
	@Column(name="QTY2")
	private BigDecimal qty2;
	
	@Column(name="QTY3")
	private BigDecimal qty3;
	
	@Column(name="QTY4")
	private BigDecimal qty4;
	
	@Column(name="QTY5")
	private BigDecimal qty5;
	
	@Column(name="QTY6")
	private BigDecimal qty6;
	
	@Column(name="QTY7")
	private BigDecimal qty7;
	@Column(name="QTY8")
	private BigDecimal qty8;
	@Column(name="QTY9")
	private BigDecimal qty9;
	@Column(name="QTY10")
	private BigDecimal qty10;
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
	
	@Column(name="DATE_PROMISED")
	private Date datePromised;
	
	@Column(name="COMMENTS")
	private String comments;
	
	@Column(name="QTY_USE")
	private BigDecimal qtyUse;//已分配数
	
	@Column(name="QTY_QIANLIAO")
	private BigDecimal qtyLingLiao;//已分配数
	
	@Column(name="SHOW_TYPE")
	private String showType;
	
	@Column(name="HAS_PROBLEM")
	private String hasProblem;
	

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

	public BigDecimal getJsqty() {
		return jsqty;
	}

	public void setJsqty(BigDecimal jsqty) {
		this.jsqty = jsqty;
	}

	public BigDecimal getWjsqty() {
		return wjsqty;
	}

	public void setWjsqty(BigDecimal wjsqty) {
		this.wjsqty = wjsqty;
	}

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

	public BigDecimal getQty2() {
		return qty2;
	}

	public void setQty2(BigDecimal qty2) {
		this.qty2 = qty2;
	}

	public BigDecimal getQty3() {
		return qty3;
	}

	public void setQty3(BigDecimal qty3) {
		this.qty3 = qty3;
	}

	public BigDecimal getQty4() {
		return qty4;
	}

	public void setQty4(BigDecimal qty4) {
		this.qty4 = qty4;
	}

	public BigDecimal getQty5() {
		return qty5;
	}

	public void setQty5(BigDecimal qty5) {
		this.qty5 = qty5;
	}

	public BigDecimal getQty6() {
		return qty6;
	}

	public void setQty6(BigDecimal qty6) {
		this.qty6 = qty6;
	}

	public BigDecimal getQty7() {
		return qty7;
	}

	public void setQty7(BigDecimal qty7) {
		this.qty7 = qty7;
	}

	public BigDecimal getQty8() {
		return qty8;
	}

	public void setQty8(BigDecimal qty8) {
		this.qty8 = qty8;
	}

	public BigDecimal getQty9() {
		return qty9;
	}

	public void setQty9(BigDecimal qty9) {
		this.qty9 = qty9;
	}

	public BigDecimal getQty10() {
		return qty10;
	}

	public void setQty10(BigDecimal qty10) {
		this.qty10 = qty10;
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

	public BigDecimal getQtyUse() {
		return qtyUse;
	}

	public void setQtyUse(BigDecimal qtyUse) {
		this.qtyUse = qtyUse;
	}

	public BigDecimal getQtyLingLiao() {
		return qtyLingLiao;
	}

	public void setQtyLingLiao(BigDecimal qtyLingLiao) {
		this.qtyLingLiao = qtyLingLiao;
	}

	public String getShowType() {
		return showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public String getHasProblem() {
		return hasProblem;
	}

	public void setHasProblem(String hasProblem) {
		this.hasProblem = hasProblem;
	}
	
}
