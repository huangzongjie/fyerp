package com.graly.erp.po.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="V_POLINE_RECEIPT_DATE")
public class VPoLineReceiptDate extends ADUpdatable{
	private static final long serialVersionUID = 1L;

	@Column(name="LINE_NO")
	private Long lineNo;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="QTY")
	private BigDecimal qty = new BigDecimal("0.0");
	
	@Column(name="DATE_START")
	private Date dateStart;
	
	@Column(name="DATE_END")
	private Date dateEnd;
	
	@Column(name="UNIT_PRICE")
	private BigDecimal unitPrice = new BigDecimal("0.0");
	
	@Column(name="LINE_TOTAL")
	private BigDecimal lineTotal = new BigDecimal("0.0");
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="LINE_STATUS")
	private String lineStatus;
	
	@Column(name="PO_RRN")
	private Long poRrn;

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

	@Column(name="MATERIAL_NAME")//收货日期
	private String materialName;
	
	@Column(name="MATERIAL_ID")//收货日期
	private String materialId;
	
	@Transient
	private BigDecimal qtyReceived;//已收货总数(包括不合格退货的)
	
	@Transient
	private BigDecimal qtyIqced;//已检验数
	
	@Transient
	private BigDecimal qtyIqcing;//正在检验的数

	public Long getLineNo() {
		return lineNo;
	}

	public void setLineNo(Long lineNo) {
		this.lineNo = lineNo;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

	public Date getDateStart() {
		return dateStart;
	}

	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}

	public Date getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getLineTotal() {
		return lineTotal;
	}

	public void setLineTotal(BigDecimal lineTotal) {
		this.lineTotal = lineTotal;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLineStatus() {
		return lineStatus;
	}

	public void setLineStatus(String lineStatus) {
		this.lineStatus = lineStatus;
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

	public String getPurchaser() {
		return purchaser;
	}

	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
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

	public Long getRequisitionLineRrn() {
		return requisitionLineRrn;
	}

	public void setRequisitionLineRrn(Long requisitionLineRrn) {
		this.requisitionLineRrn = requisitionLineRrn;
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

	public String getIsPreApproved() {
		return isPreApproved;
	}

	public void setIsPreApproved(String isPreApproved) {
		this.isPreApproved = isPreApproved;
	}

	public Date getDatePreApproved() {
		return datePreApproved;
	}

	public void setDatePreApproved(Date datePreApproved) {
		this.datePreApproved = datePreApproved;
	}

	public String getPreApproved() {
		return preApproved;
	}

	public void setPreApproved(String preApproved) {
		this.preApproved = preApproved;
	}

	public String getUrgency() {
		return urgency;
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}

	public String getDateHisPromised() {
		return dateHisPromised;
	}

	public void setDateHisPromised(String dateHisPromised) {
		this.dateHisPromised = dateHisPromised;
	}

	public BigDecimal getQtyLoss() {
		return qtyLoss;
	}

	public void setQtyLoss(BigDecimal qtyLoss) {
		this.qtyLoss = qtyLoss;
	}

	public String getPackageSpec() {
		return packageSpec;
	}

	public void setPackageSpec(String packageSpec) {
		this.packageSpec = packageSpec;
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

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}
	
}
