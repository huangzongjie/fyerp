package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADBase;
@Entity
@Table(name="V_PIN_DETAIL")
public class VPinDetail extends ADBase {
	private static final long serialVersionUID = 1L;

	@Column(name="DOC_ID")
	private String docId;
	
	@Column(name="MOVEMENT_RRN")
	private Long movmentRrn;
	 
	@Column(name="DOC_STATUS")
	private String docStatus;
	 
	@Column(name="DOC_TYPE")
	private String docType;
	 
	@Column(name="DATE_CREATED")
	private Date dateCreated;
	 
	@Column(name="DATE_APPROVED")
	private Date dateApproved;
	
	@Column(name="DATE_WRITE_OFF")
	private Date dateWriteOff;

	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="VENDOR_RRN")
	private Long vendorRrn;
	 
	@Column(name="VENDOR_ID")
	private String vendorId;
	
	@Column(name="COMPANY_NAME")
	private String companyName;
	 
	@Column(name="USER_CREATED")
	private String userCreated;
	 
	@Column(name="USER_APPROVED")
	private String userApproved;
	 
	@Column(name="USER_IQC")
	private String userIqc;
	 
	@Column(name="DESCRIPTION")
	private String description;
	 
	@ManyToOne
	@JoinColumn(name = "MATERIAL_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material material;
	 
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
	
	@Column(name="ASSESS_LINE_TOTAL")
	private BigDecimal assessLineTotal;
	
	@Column(name="INVOICE_LINE_TOTAL")
	private BigDecimal invoiceLineTotal;
//	 
//	@Column(name="MATERIAL_ID")
//	private String materialId;
//	 
//	@Column(name="MATERIAL_NAME")
//	private String materialName;

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

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
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

	public BigDecimal getQtyMovement() {
		return qtyMovement;
	}

	public void setQtyMovement(BigDecimal qtyMovement) {
		this.qtyMovement = qtyMovement;
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
		if(material != null)
			return material.getMaterialId();
		return null;
	}

	public String getMaterialName() {
		if(material != null)
			return material.getName();
		return null;
	}

	public Long getMovmentRrn() {
		return movmentRrn;
	}

	public void setMovmentRrn(Long movmentRrn) {
		this.movmentRrn = movmentRrn;
	}

	public Date getDateWriteOff() {
		return dateWriteOff;
	}

	public void setDateWriteOff(Date dateWriteOff) {
		this.dateWriteOff = dateWriteOff;
	}

	public Long getVendorRrn() {
		return vendorRrn;
	}

	public void setVendorRrn(Long vendorRrn) {
		this.vendorRrn = vendorRrn;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public BigDecimal getAssessLineTotal() {
		return assessLineTotal;
	}

	public void setAssessLineTotal(BigDecimal assessLineTotal) {
		this.assessLineTotal = assessLineTotal;
	}

	public BigDecimal getInvoiceLineTotal() {
		return invoiceLineTotal;
	}

	public void setInvoiceLineTotal(BigDecimal invoiceLineTotal) {
		this.invoiceLineTotal = invoiceLineTotal;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}
