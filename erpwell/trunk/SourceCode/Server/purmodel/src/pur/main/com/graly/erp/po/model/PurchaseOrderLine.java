package com.graly.erp.po.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.DocumentationLine;

@Entity
@Table(name="PUR_PO_LINE")
public class PurchaseOrderLine extends DocumentationLine {
	private static final long serialVersionUID = 1L;
	public static final String URGENCY_NORMAL = "正常";
	public static final String URGENCY_URGENT = "紧急";
	
	@Column(name="PO_RRN")
	private Long poRrn;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="PO_RRN",insertable=false,updatable=false)
	private PurchaseOrder po;

	@Column(name="PO_ID")
	private String poId;
	
	@Column(name="PURCHASER")
	private String purchaser;
	
	@Column(name="QTY_DELIVERED")
	private BigDecimal qtyDelivered;
	
	@Column(name="QTY_IN")
	private BigDecimal qtyIn;
	
	@Column(name="QTY_REJECTED")
	private BigDecimal qtyRejected;
	
	@Column(name="QTY_TESTED")
	private BigDecimal qtyTested;
	
	@Column(name="QTY_QUALIFIED")
	private BigDecimal qtyQualified;
	
	@Column(name="QTY_INVENTORY")
	private BigDecimal qtyInventoty;
	
	@Column(name="DATE_PROMISED")
	private Date datePromised;
	
	@Column(name="DATE_DELIVERED")
	private Date dateDelivered;

	@Column(name="REQUISITION_LINE_RRN")
	private Long requisitionLineRrn;

	@Column(name="REF_VENDOR_RRN")
	private Long refVendorRrn;
	
	@Column(name="REF_UNIT_PRICE")
	private BigDecimal refUnitPrice;
	
	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;

	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="IS_PREAPPROVED")
	private String isPreApproved = "N";//默认是false
	
	@Column(name="DATE_PREAPPROVED")
	private Date datePreApproved;
	
	@Column(name="PREAPPROVED")
	private String preApproved;
	
	@Column(name="URGENCY")//紧急度
	private String urgency = "正常";//默认是正常
	
	@Column(name="DATE_HISPROMISED")
	private String dateHisPromised;
	
	@Transient
	private String firstProcurement;
	
	@Transient
	private BigDecimal qtyReceived;//已收货总数(包括不合格退货的)
	
	@Transient
	private BigDecimal qtyIqced;//已检验数
	
	@Transient
	private BigDecimal qtyIqcing;//正在检验的数
	
	@Column(name="QTY_LOSS")
	private BigDecimal qtyLoss = BigDecimal.ZERO; //损耗 默认0
	
	@Column(name="PACKAGE_SPEC")//包装规格
	private String packageSpec;
	
	@Column(name="BARCODE")//条码
	private String barCode;
	
	@Column(name="RECEIPT_DATE")//收货日期
	private Date receiptDate;
	
	@Column(name="RECEIPT_DATE_HOUR")//收货日期
	private String receiptDateHour;
	
	@Column(name="XZ_USER_RRN")//行政用户objectRrn
	private String xzUserRrn;
	
	@Column(name="XZ_USER_NAME")//行政用户名
	private String xzUserName;
	
	@Column(name="XZ_DEPARTMENT")//行政部门
	private String xzDepartment;
	
	@Column(name="XZ_COMPANY")//行政公司
	private String xzCompany;
	
	@Column(name="IS_INSPECTION_FREE")//是否免检,默认否
	private String isInspectionFree;
	
	@Column(name="PRODUCT_NO")//原能产品货号
	private String productNo;

	public String getPackageSpec() {
		return packageSpec;
	}

	public void setPackageSpec(String packageSpec) {
		this.packageSpec = packageSpec;
	}

	public BigDecimal getQtyLoss() {
		return qtyLoss;
	}

	public void setQtyLoss(BigDecimal qtyLoss) {
		this.qtyLoss = qtyLoss;
	}
	
	public Long getPoRrn() {
		return poRrn;
	}

	public void setPoRrn(Long poRrn) {
		this.poRrn = poRrn;
	}

	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}

	public BigDecimal getQtyDelivered() {
		return qtyDelivered;
	}

	public void setQtyDelivered(BigDecimal qtyDelivered) {
		this.qtyDelivered = qtyDelivered;
	}

	public BigDecimal getQtyIn() {
		return qtyIn;
	}

	public void setQtyIn(BigDecimal qtyIn) {
		this.qtyIn = qtyIn;
	}

	public BigDecimal getQtyRejected() {
		return qtyRejected;
	}

	public void setQtyRejected(BigDecimal qtyRejected) {
		this.qtyRejected = qtyRejected;
	}

	public BigDecimal getQtyTested() {
		return qtyTested;
	}

	public void setQtyTested(BigDecimal qtyTested) {
		this.qtyTested = qtyTested;
	}

	public BigDecimal getQtyQualified() {
		return qtyQualified;
	}

	public void setQtyQualified(BigDecimal qtyQualified) {
		this.qtyQualified = qtyQualified;
	}

	public BigDecimal getQtyInventoty() {
		return qtyInventoty;
	}

	public void setQtyInventoty(BigDecimal qtyInventoty) {
		this.qtyInventoty = qtyInventoty;
	}


	public Long getRequisitionLineRrn() {
		return requisitionLineRrn;
	}

	public void setRequisitionLineRrn(Long requisitionLineRrn) {
		this.requisitionLineRrn = requisitionLineRrn;
	}

	public Date getDatePromised() {
		return datePromised;
	}

	public void setDatePromised(Date datePromised) {
		this.datePromised = datePromised;
	}

	public Date getDateDelivered() {
		return dateDelivered;
	}

	public void setDateDelivered(Date dateDelivered) {
		this.dateDelivered = dateDelivered;
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

	public String getPurchaser() {
		return purchaser;
	}

	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
	}

	public Boolean getIsPreApproved() {
		return "Y".equalsIgnoreCase(isPreApproved) ? true : false;
	}

	public void setIsPreApproved(Boolean isPreApproved) {
		this.isPreApproved = isPreApproved ? "Y" : "N";
	}

	public String getPreApproved() {
		return preApproved;
	}

	public void setPreApproved(String preApproved) {
		this.preApproved = preApproved;
	}

	public Date getDatePreApproved() {
		return datePreApproved;
	}

	public void setDatePreApproved(Date datePreApproved) {
		this.datePreApproved = datePreApproved;
	}

	public String getUrgency() {
		return urgency;
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}

	public Boolean getFirstProcurement() {
		return !getMaterial().getHasPurchased();
	}

	public PurchaseOrder getPo() {
		return po;
	}

	public void setPo(PurchaseOrder po) {
		this.po = po;
	}
	
	public String getVendorId(){
		return po == null ? "" : po.getVendorId();
	}
	
	public String getVendorName(){
		return po == null ? "" : po.getFormerVendorName();
	}
	
	public String getDateHisPromised() {
		return dateHisPromised;
	}

	public void setDateHisPromised(String dateHisPromised) {
		this.dateHisPromised = dateHisPromised;
	}

	public BigDecimal getQtyReceived() {
		return qtyReceived;
	}

	public void setQtyReceived(BigDecimal qtyReceived) {
		this.qtyReceived = qtyReceived;
	}

	public BigDecimal getQtyIqced() {
		return qtyIqced;
	}

	public void setQtyIqced(BigDecimal qtyIqced) {
		this.qtyIqced = qtyIqced;
	}

	public BigDecimal getQtyIqcing() {
		return qtyIqcing;
	}

	public void setQtyIqcing(BigDecimal qtyIqcing) {
		this.qtyIqcing = qtyIqcing;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getReceiptDateHour() {
		return receiptDateHour;
	}

	public void setReceiptDateHour(String receiptDateHour) {
		this.receiptDateHour = receiptDateHour;
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
	
	public Boolean getIsInspectionFree() {
		return "Y".equalsIgnoreCase(isInspectionFree) ? true : false;
	}

	public void setIsInspectionFree(Boolean isInspectionFree) {
		this.isInspectionFree = isInspectionFree ? "Y" : "N";
	}
	
	public String getProductNo() {
		return productNo;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}
}
