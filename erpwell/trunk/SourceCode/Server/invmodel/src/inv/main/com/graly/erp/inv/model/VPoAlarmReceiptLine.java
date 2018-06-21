package com.graly.erp.inv.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;


@Entity
@Table(name="V_PO_ALARM_RECEIPTLINE")
public class VPoAlarmReceiptLine extends ADUpdatable{
	private static final long serialVersionUID = 1L;
	
	@Column(name="RECEIPT_RRN")
	private Long receiptRrn;
	
	@Column(name="RECEIPT_ID")
	private String receiptId;
	
	@Column(name="LINE_NO")
	private Long lineNo;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="PO_LINE_RRN")
	private Long poLineRrn;
	
	@Column(name="PO_LINE_QTY")
	private BigDecimal poLineQty;
	
	@Column(name="PO_ID")
	private String poId;
	
	@Column(name="QTY_RECEIPT")
	private BigDecimal qtyReceipt;
	
	@Column(name="UNIT_PRICE")
	private BigDecimal unitPrice;
	
	@Column(name="LINE_TOTAL")
	private BigDecimal lineTotal;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="LINE_STATUS")
	private String lineStatus;
	
	@Column(name="IS_IQC")
	private String isIqc = "N";
	
	@Column(name="VENDOR_ID")
	private String vendorId;
	
	@Column(name="VENDOR_NAME")
	private String vendorName;

	public Long getReceiptRrn() {
		return receiptRrn;
	}

	public void setReceiptRrn(Long receiptRrn) {
		this.receiptRrn = receiptRrn;
	}

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

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

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public Long getPoLineRrn() {
		return poLineRrn;
	}

	public void setPoLineRrn(Long poLineRrn) {
		this.poLineRrn = poLineRrn;
	}

	public BigDecimal getPoLineQty() {
		return poLineQty;
	}

	public void setPoLineQty(BigDecimal poLineQty) {
		this.poLineQty = poLineQty;
	}

	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}

	public BigDecimal getQtyReceipt() {
		return qtyReceipt;
	}

	public void setQtyReceipt(BigDecimal qtyReceipt) {
		this.qtyReceipt = qtyReceipt;
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

	public String getIsIqc() {
		return isIqc;
	}

	public void setIsIqc(String isIqc) {
		this.isIqc = isIqc;
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

}
