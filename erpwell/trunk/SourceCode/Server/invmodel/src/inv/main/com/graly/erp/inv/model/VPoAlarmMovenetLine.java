package com.graly.erp.inv.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="V_PO_ALARM_MOVEMENT_LINE")
public class VPoAlarmMovenetLine extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="MOVEMENT_RRN")
	private Long movementRrn;
	
	@Column(name="MOVEMENT_ID")
	private String movementId;
	
	@Column(name="LINE_NO")
	private Long lineNo;
	
	@Column(name="LOCATOR_RRN")
	private Long locatorRrn;
	
	@Column(name="LOCATOR_ID")
	private String locatorId;
	
	@Column(name="VENDOR_RRN")
	private Long vendorRrn;
	
	@Column(name="VENDOR_ID")
	private String vendorId;
	
	@Column(name="VENDOR_NAME")
	private String vendorName;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;

	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="QTY_MOVEMENT")
	private BigDecimal qtyMovement;
	
	@Column(name="IQC_LINE_RRN")
	private Long iqcLineRrn;

	@Column(name="PO_LINE_RRN")
	private Long poLineRrn;
	
	@Column(name="MO_LINE_RRN")
	private Long moLineRrn;
	
	@Column(name="LINE_STATUS")
	private String lineStatus;

	@Column(name="UNIT_PRICE")
	private BigDecimal unitPrice;
	
	@Column(name="LINE_TOTAL")
	private BigDecimal lineTotal;
	
	@Column(name="VAT_RATE")
	private BigDecimal vatRate;
	
	@Column(name="ASSESS_UNIT_PRICE")
	private BigDecimal assessUnitPrice;
	
	@Column(name="ASSESS_LINE_TOTAL")
	private BigDecimal assessLineTotal;
	
	@Column(name="INVOICE_UNIT_PRICE")
	private BigDecimal invoiceUnitPrice;
	
	@Column(name="INVOICE_LINE_TOTAL")
	private BigDecimal invoiceLineTotal;
	
	@Column(name="LOT_TYPE")
	private String lotType;
	
	@Column(name="PO_LINE_QTY")
	private String poLineQty;
	
	@Column(name="PO_ID")
	private String poId;
	
	@Column(name="PO_LINE_QTY_DELIVERED")
	private String poLineQtyDelivered;
	
	@Column(name="PO_LINE_QTY_TESTED")
	private String poLineQtyTested;
	
	@Column(name="PO_LINE_QTY_IN")
	private String poLineQtyIn;
	
	@Column(name="PO_LINE_QTY_REJECTED")
	private String poLineQtyRejected;
	
	@Column(name="PO_LINE_QTY_QUALIFIED")
	private String poLineQtyQualified;
	
	@Column(name="PO_LINE_CREATE_BY")
	private Long poLineCreateBy;
	
	@Column(name="NEW_VENDOR_ID")
	private String newVendorId;
	
	@Column(name="NEW_VENDOR_NAME")
	private String newVendorName;
	
	public Long getMovementRrn() {
		return movementRrn;
	}

	public void setMovementRrn(Long movementRrn) {
		this.movementRrn = movementRrn;
	}

	public String getMovementId() {
		return movementId;
	}

	public void setMovementId(String movementId) {
		this.movementId = movementId;
	}

	public Long getLineNo() {
		return lineNo;
	}

	public void setLineNo(Long lineNo) {
		this.lineNo = lineNo;
	}

	public void setLocatorRrn(Long locatorRrn) {
		this.locatorRrn = locatorRrn;
	}

	public Long getLocatorRrn() {
		return locatorRrn;
	}

	public void setLocatorId(String locatorId) {
		this.locatorId = locatorId;
	}

	public String getLocatorId() {
		return locatorId;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getMaterialId() {
		return materialId;
	}
	
	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public String getUomId() {
		return uomId;
	}

	public BigDecimal getQtyMovement() {
		return qtyMovement;
	}

	public void setQtyMovement(BigDecimal qtyMovement) {
		this.qtyMovement = qtyMovement;
	}

	public void setIqcLineRrn(Long iqcLineRrn) {
		this.iqcLineRrn = iqcLineRrn;
	}


	public Long getIqcLineRrn() {
		return iqcLineRrn;
	}

	public void setLineStatus(String lineStatus) {
		this.lineStatus = lineStatus;
	}

	public String getLineStatus() {
		return lineStatus;
	}
	

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setLineTotal(BigDecimal lineTotal) {
		this.lineTotal = lineTotal;
	}

	public BigDecimal getLineTotal() {
		return lineTotal;
	}
	
	public void setPoLineRrn(Long poLineRrn) {
		this.poLineRrn = poLineRrn;
	}

	public Long getPoLineRrn() {
		return poLineRrn;
	}

	public void setMoLineRrn(Long moLineRrn) {
		this.moLineRrn = moLineRrn;
	}

	public Long getMoLineRrn() {
		return moLineRrn;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setVatRate(BigDecimal vatRate) {
		this.vatRate = vatRate;
	}

	public BigDecimal getVatRate() {
		return vatRate;
	}

	public void setAssessUnitPrice(BigDecimal assessUnitPrice) {
		this.assessUnitPrice = assessUnitPrice;
	}

	public BigDecimal getAssessUnitPrice() {
		return assessUnitPrice;
	}

	public void setAssessLineTotal(BigDecimal assessLineTotal) {
		this.assessLineTotal = assessLineTotal;
	}

	public BigDecimal getAssessLineTotal() {
		return assessLineTotal;
	}

	public void setInvoiceUnitPrice(BigDecimal invoiceUnitPrice) {
		this.invoiceUnitPrice = invoiceUnitPrice;
	}

	public BigDecimal getInvoiceUnitPrice() {
		return invoiceUnitPrice;
	}

	public void setInvoiceLineTotal(BigDecimal invoiceLineTotal) {
		this.invoiceLineTotal = invoiceLineTotal;
	}

	public BigDecimal getInvoiceLineTotal() {
		return invoiceLineTotal;
	}

	public String getLotType() {
		return lotType;
	}

	public void setLotType(String lotType) {
		this.lotType = lotType;
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

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getPoLineQty() {
		return poLineQty;
	}

	public void setPoLineQty(String poLineQty) {
		this.poLineQty = poLineQty;
	}

	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}

	public String getPoLineQtyDelivered() {
		return poLineQtyDelivered;
	}

	public void setPoLineQtyDelivered(String poLineQtyDelivered) {
		this.poLineQtyDelivered = poLineQtyDelivered;
	}

	public String getPoLineQtyTested() {
		return poLineQtyTested;
	}

	public void setPoLineQtyTested(String poLineQtyTested) {
		this.poLineQtyTested = poLineQtyTested;
	}

	public String getPoLineQtyIn() {
		return poLineQtyIn;
	}

	public void setPoLineQtyIn(String poLineQtyIn) {
		this.poLineQtyIn = poLineQtyIn;
	}

	public String getPoLineQtyRejected() {
		return poLineQtyRejected;
	}

	public void setPoLineQtyRejected(String poLineQtyRejected) {
		this.poLineQtyRejected = poLineQtyRejected;
	}

	public String getPoLineQtyQualified() {
		return poLineQtyQualified;
	}

	public void setPoLineQtyQualified(String poLineQtyQualified) {
		this.poLineQtyQualified = poLineQtyQualified;
	}

	public Long getPoLineCreateBy() {
		return poLineCreateBy;
	}

	public void setPoLineCreateBy(Long poLineCreateBy) {
		this.poLineCreateBy = poLineCreateBy;
	}

	public String getNewVendorId() {
		return newVendorId;
	}

	public void setNewVendorId(String newVendorId) {
		this.newVendorId = newVendorId;
	}

	public String getNewVendorName() {
		return newVendorName;
	}

	public void setNewVendorName(String newVendorName) {
		this.newVendorName = newVendorName;
	}
	
	
}
