package com.graly.erp.wip.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="PMC_CF_ZZJ_DETAIL_SUM")
public class PmcZzjResultDetail extends ADUpdatable{
	private static final long serialVersionUID = 1L;
	
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
	
	@Column(name="PROCESS_NAME")
	private String processName;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="ONHAND_QTY")
	private BigDecimal onhandQty;
	
//	@Column(name="UOM_ID")
//	private String uomId;
//	
//	@Column(name="QTY_MIN")
//	private String qtyMin;
//	
//	@Column(name="PURCHASE")
//	private String purchase;
//	 
//	@Column(name="VENDOR_ID")
//	private String vendorId;
//	
//	@Column(name="VENDOR_NAME")
//	private String vendorName;
//	
//	@Column(name="REFERENCED_PRICE")
//	private BigDecimal referencedPrice;

	@Column(name="QTY_TRANSIT")
	private BigDecimal qtyTransit;//��;
	
	@Column(name="QTY_ALLOCATION")
	private BigDecimal qtyAllocation;//�ѷ�����
	 
	@Column(name="SHOW_TYPE")
	private String showType;
	
	@Column(name="PARENT_PROCESS_NAME")
	private String parentProcessName;
	
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

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public BigDecimal getQty10() {
		return qty10;
	}

	public void setQty10(BigDecimal qty10) {
		this.qty10 = qty10;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public BigDecimal getOnhandQty() {
		return onhandQty;
	}

	public void setOnhandQty(BigDecimal onhandQty) {
		this.onhandQty = onhandQty;
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
	 
	public String getShowType() {
		return showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	public String getParentProcessName() {
		return parentProcessName;
	}

	public void setParentProcessName(String parentProcessName) {
		this.parentProcessName = parentProcessName;
	}
}
