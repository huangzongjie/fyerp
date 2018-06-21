package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_IN_DETAIL")
public class VInDetail extends ADBase {
	private static final long serialVersionUID = 1L;

	@Column(name="DOC_ID")
	private String docId;
	
	@Column(name="MOVMENT_RRN")
	private Long movmentRrn;

	@Column(name="DOC_STATUS")
	private String docStatus;

	@Column(name="DOC_TYPE")
	private String docType;

	@Column(name="IN_TYPE")
	private String inType;
	
	@Column(name="DATE_CREATED")
	private Date dateCreated;

	@Column(name="DATE_APPROVED")
	private Date dateApproved;

	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;

	@Column(name="WAREHOUSE_ID")
	private String warehouseId;

	@Column(name="VENDOR_RRN")
	private Long vendorRrn;

	@Column(name="VENDOR_ID")
	private String vendorId;
	
	@Column(name="VENDOR_NAME")
	private String vendorName;

	@Column(name="USER_CREATED")
	private String userCreated;

	@Column(name="USER_APPROVED")
	private String userApproved;

	@Column(name="USER_IQC")
	private String userIqc;

	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="MATERIAL_RRN")
	private Long materialRrn;

	@Column(name="UOM_ID")
	private String uomId;

	@Column(name="QTY_MOVEMENT")
	private BigDecimal qtyMovement;

	@Column(name="UNIT_PRICE")
	private BigDecimal unitPrice;

	@Column(name="LINE_TOTAL")
	private BigDecimal lineTotal;

	@Column(name="LOCATOR_ID")
	private String locatorId;

	@Column(name="MATERIAL_ID")
	private String materialId;

	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="MO_ID")
	private String moId;
	
	@Column(name="QTY_PRODUCT")
	private BigDecimal qtyProduct;
	
	@Column(name="PO_ID")
	private String poId;
	
	@Column(name="MOVEMENT_DATE")
	private Date movementDate;
	
	@Column(name="WORKCENTER_ID")
	private String workcenterId;

	@Column(name="Kind")
	private String kind;
	

	@Column(name="PURCHASER")
	private String purchaser;
	
	@Column(name="INVOICE_LINE_TOTAL")
	private BigDecimal invoiceLineTotal;
	
	public String getPurchaser() {
		return purchaser;
	}
	

	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
	}


	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(String userCreated) {
		this.userCreated = userCreated;
	}

	public String getUserApproved() {
		return userApproved;
	}

	public void setUserApproved(String userApproved) {
		this.userApproved = userApproved;
	}

	public String getUserIqc() {
		return userIqc;
	}

	public void setUserIqc(String userIqc) {
		this.userIqc = userIqc;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
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

	public String getLocatorId() {
		return locatorId;
	}

	public void setLocatorId(String locatorId) {
		this.locatorId = locatorId;
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

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateApproved() {
		return dateApproved;
	}

	public void setDateApproved(Date dateApproved) {
		this.dateApproved = dateApproved;
	}

	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseRrn(Long warehouseRrn) {
		this.warehouseRrn = warehouseRrn;
	}

	public Long getVendorRrn() {
		return vendorRrn;
	}

	public void setVendorRrn(Long vendorRrn) {
		this.vendorRrn = vendorRrn;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public Long getMovmentRrn() {
		return movmentRrn;
	}

	public void setMovmentRrn(Long movmentRrn) {
		this.movmentRrn = movmentRrn;
	}

	public void setInType(String inType) {
		this.inType = inType;
	}

	public String getInType() {
		return inType;
	}

	public String getMoId() {
		return moId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}

	public String getPoId() {
		return poId;
	}
	
	public void setMoId(String moId) {
		this.moId = moId;
	}

	public Date getMovementDate() {
		return movementDate;
	}

	public void setMovementDate(Date movementDate) {
		this.movementDate = movementDate;
	}

	public BigDecimal getQtyMovement() {
		return qtyMovement;
	}

	public void setQtyMovement(BigDecimal qtyMovement) {
		this.qtyMovement = qtyMovement;
	}

	public BigDecimal getQtyProduct() {
		return qtyProduct;
	}

	public void setQtyProduct(BigDecimal qtyProduct) {
		this.qtyProduct = qtyProduct;
	}

	public String getWorkcenterId() {
		return workcenterId;
	}

	public void setWorkcenterId(String workcenterId) {
		this.workcenterId = workcenterId;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}


	public BigDecimal getInvoiceLineTotal() {
		return invoiceLineTotal;
	}


	public void setInvoiceLineTotal(BigDecimal invoiceLineTotal) {
		this.invoiceLineTotal = invoiceLineTotal;
	}
}
