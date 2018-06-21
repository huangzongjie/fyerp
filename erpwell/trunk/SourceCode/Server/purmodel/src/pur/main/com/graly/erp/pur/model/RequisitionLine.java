package com.graly.erp.pur.model;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.DocumentationLine;
import com.graly.erp.vdm.model.Vendor;

@Entity
@Table(name="PUR_REQUISITION_LINE")
public class RequisitionLine extends DocumentationLine {
	
	@Column(name="REQUISITION_RRN")
	private Long requisitionRrn;

	@Column(name="REQUISITION_ID")
	private String requisitionId;
	
	@Column(name="QTY_MPS")
	private BigDecimal qtyMPS;
	
	@Column(name="QTY_THEORY")
	private BigDecimal qtyTheory;
	
	@Column(name="QTY_INVENTORY")
	private BigDecimal qtyInventoty;
	
	@Column(name="LEAD_TIME")
	private Long leadTime;
	
	@Column(name="VENDOR_RRN")
	private Long vendorRrn;
	
	@ManyToOne
	@JoinColumn(name = "VENDOR_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Vendor vendor;
	
	@Column(name="REF_VENDOR_RRN")
	private Long refVendorRrn;
	
	@Column(name="REF_UNIT_PRICE")
	private BigDecimal refUnitPrice;
	
	@Column(name="QTY_ORDERED")
	private BigDecimal qtyOrdered;
	
	@Column(name="QTY_HANDON")
	private BigDecimal qtyHandOn;//所有仓库的库存
	
	@Column(name="QTY_DIFFERENCE")
	private BigDecimal qtyDifference;
	
	@Column(name="QTY_TRANSIT")
	private BigDecimal qtyTransit;
	
	@Column(name="QTY_MIN")
	private BigDecimal qtyMin;
	
	@Column(name="QTY_ECONOMIC_SIZE")
	private BigDecimal qtyEconomicSize;
	
	@Column(name="QTY_INCREASE_SIZE")
	private BigDecimal qtyIncreaseSize;
	
	@Column(name="QTY_ALLOCATION")
	private BigDecimal qtyAllocation;
	
	@Column(name="PRICE_LOWEST")
	private BigDecimal priceLowest;
	
	@Column(name="PRICE_AVERAGE")
	private BigDecimal priceAverage;
	
	@Column(name="PRICE_LAST")
	private BigDecimal priceLast;
	
	@Transient
	private BigDecimal qtyNeed;
	
	@Column(name="PURCHASER")
	private String purchaser;

	@Transient
	private String path;
	
	@Transient
	private Long pathLevel;
	
	@Transient
	private BigDecimal advanceRatio;
	
	@Transient
	private Long moBomRrn;
	
	@Column(name="ADVANCE_PAYMENT")
	private BigDecimal advancePayment;
	
	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="QTY_HANDON2")
	private BigDecimal qtyHandOn2;//只统计制造良品车间和环保良品车间
	
	@Column(name="PACKAGE_SPEC")//包装规格
	private String packageSpec;
	
	@Column(name="XZ_USER_RRN")//行政用户objectRrn
	private String xzUserRrn;
	
	@Column(name="XZ_USER_NAME")//行政用户名
	private String xzUserName;
	
	@Column(name="XZ_DEPARTMENT")//行政部门
	private String xzDepartment;
	
	@Column(name="XZ_COMPANY")//行政公司
	private String xzCompany;
	
	@Column(name="PRODUCT_NO")//原能产品货号
	private String productNo;
	
	public String getPackageSpec() {
		return packageSpec;
	}

	public void setPackageSpec(String packageSpec) {
		this.packageSpec = packageSpec;
	}

	public Long getRequisitionRrn() {
		return requisitionRrn;
	}

	public void setRequisitionRrn(Long requisitionRrn) {
		this.requisitionRrn = requisitionRrn;
	}

	public void setRequisitionId(String requisitionId) {
		this.requisitionId = requisitionId;
	}

	public String getRequisitionId() {
		return requisitionId;
	}

	public BigDecimal getQtyMPS() {
		return qtyMPS;
	}

	public void setQtyMPS(BigDecimal qtyMPS) {
		this.qtyMPS = qtyMPS;
	}

	public BigDecimal getQtyTheory() {
		return qtyTheory;
	}

	public void setQtyTheory(BigDecimal qtyTheory) {
		this.qtyTheory = qtyTheory;
	}

	public void setQtyInventoty(BigDecimal qtyInventoty) {
		this.qtyInventoty = qtyInventoty;
	}

	public BigDecimal getQtyInventoty() {
		return qtyInventoty;
	}
	
	public Long getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(Long leadTime) {
		this.leadTime = leadTime;
	}

	public Long getVendorRrn() {
		return vendorRrn;
	}

	public void setVendorRrn(Long vendorRrn) {
		this.vendorRrn = vendorRrn;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public String getVendorId() {
		return vendor != null ? vendor.getVendorId() : "";
	}
	
	public String getVendorName() {
		return vendor != null ? vendor.getCompanyName() : "";
	}
	
	public Long getRefVendorRrn() {
		return refVendorRrn;
	}

	public void setRefVendorRrn(Long refVendorRrn) {
		this.refVendorRrn = refVendorRrn;
	}

	public BigDecimal getRefUnitPrice() {
		return refUnitPrice;
	}

	public void setRefUnitPrice(BigDecimal refUnitPrice) {
		this.refUnitPrice = refUnitPrice;
	}

	public BigDecimal getQtyOrdered() {
		return qtyOrdered;
	}

	public void setQtyOrdered(BigDecimal qtyOrdered) {
		this.qtyOrdered = qtyOrdered;
	}

	public BigDecimal getQtyHandOn() {
		return qtyHandOn;
	}

	public void setQtyHandOn(BigDecimal qtyHandOn) {
		this.qtyHandOn = qtyHandOn;
	}

	public BigDecimal getQtyDifference() {
		return qtyDifference;
	}

	public void setQtyDifference(BigDecimal qtyDifference) {
		this.qtyDifference = qtyDifference;
	}

	public BigDecimal getQtyTransit() {
		return qtyTransit;
	}

	public void setQtyTransit(BigDecimal qtyTransit) {
		this.qtyTransit = qtyTransit;
	}

	public BigDecimal getQtyMin() {
		return qtyMin;
	}

	public void setQtyMin(BigDecimal qtyMin) {
		this.qtyMin = qtyMin;
	}

	public BigDecimal getQtyEconomicSize() {
		return qtyEconomicSize;
	}

	public void setQtyEconomicSize(BigDecimal qtyEconomicSize) {
		this.qtyEconomicSize = qtyEconomicSize;
	}

	public BigDecimal getQtyIncreaseSize() {
		return qtyIncreaseSize;
	}

	public void setQtyIncreaseSize(BigDecimal qtyIncreaseSize) {
		this.qtyIncreaseSize = qtyIncreaseSize;
	}

	public String getPurchaser() {
		return purchaser;
	}

	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPathLevel(Long pathLevel) {
		this.pathLevel = pathLevel;
	}

	public Long getPathLevel() {
		return pathLevel;
	}

	public BigDecimal getAdvancePayment() {
		return advancePayment;
	}

	public void setAdvancePayment(BigDecimal advancePayment) {
		this.advancePayment = advancePayment;
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

	public BigDecimal getAdvanceRatio() {
		return advanceRatio;
	}

	public void setAdvanceRatio(BigDecimal advanceRatio) {
		this.advanceRatio = advanceRatio;
	}

	public BigDecimal getQtyNeed() {
		return qtyNeed;
	}

	public void setQtyNeed(BigDecimal qtyNeed) {
		this.qtyNeed = qtyNeed;
	}

	public void setQtyAllocation(BigDecimal qtyAllocation) {
		this.qtyAllocation = qtyAllocation;
	}

	public BigDecimal getQtyAllocation() {
		return qtyAllocation;
	}

	public BigDecimal getPriceLowest() {
		return priceLowest;
	}

	public void setPriceLowest(BigDecimal priceLowest) {
		this.priceLowest = priceLowest;
	}

	public BigDecimal getPriceAverage() {
		return priceAverage;
	}

	public void setPriceAverage(BigDecimal priceAverage) {
		this.priceAverage = priceAverage;
	}

	public BigDecimal getPriceLast() {
		return priceLast;
	}

	public void setPriceLast(BigDecimal priceLast) {
		this.priceLast = priceLast;
	}

	public void setMoBomRrn(Long moBomRrn) {
		this.moBomRrn = moBomRrn;
	}

	public Long getMoBomRrn() {
		return moBomRrn;
	}

	public BigDecimal getQtyHandOn2() {
		return qtyHandOn2;
	}

	public void setQtyHandOn2(BigDecimal qtyHandOn2) {
		this.qtyHandOn2 = qtyHandOn2;
	}

	public String getXzUserRrn() {
		return xzUserRrn;
	}

	public void setXzUserRrn(String xzUserRrn) {
		this.xzUserRrn = xzUserRrn;
	}

	public String getXzUserName() {
		return xzUserName;
	}

	public void setXzUserName(String xzUserName) {
		this.xzUserName = xzUserName;
	}

	public String getXzDepartment() {
		return xzDepartment;
	}

	public void setXzDepartment(String xzDepartment) {
		this.xzDepartment = xzDepartment;
	}

	public String getXzCompany() {
		return xzCompany;
	}

	public void setXzCompany(String xzCompany) {
		this.xzCompany = xzCompany;
	}

	public String getProductNo() {
		return productNo;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}
	
}
