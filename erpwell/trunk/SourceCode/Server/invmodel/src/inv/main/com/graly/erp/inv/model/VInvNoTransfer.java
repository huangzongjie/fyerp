package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="V_INV_NO_TRANSFER")
public class VInvNoTransfer extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="MOVEMENT_ID")
	private String movementId;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;

	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="QTY_MOVEMENT")
	private BigDecimal qtyMovement;
	
	@Column(name="PO_LINE_RRN")
	private Long poLineRrn;
	
	@Column(name="RECEIPT_ID")
	private String receiptId;
	
	@Column(name="QTY_RECEIPT")
	private BigDecimal qtyReceipt;
 
	@Column(name="DATE_APPROVED")
	private Date dateApproved;
	
	@Column(name="RECEIPT_DATE_APPROVED")
	private Date receiptDateApproved;
	
	@Column(name="TRF_MOVEMENT_ID")
	private String trfMovementId;
	
	@Column(name="TRF_QTY_MOVEMENT")
	private BigDecimal trfQtyMovement;
	
	@Column(name="TRF_DATE_APPROVED")
	private Date trfDateApproved;
	
	@Column(name="TRF_MATERIAL_RRN")
	private Long trfMaterialRrn;

	public String getMovementId() {
		return movementId;
	}

	public void setMovementId(String movementId) {
		this.movementId = movementId;
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

	public BigDecimal getQtyMovement() {
		return qtyMovement;
	}

	public void setQtyMovement(BigDecimal qtyMovement) {
		this.qtyMovement = qtyMovement;
	}

	public Long getPoLineRrn() {
		return poLineRrn;
	}

	public void setPoLineRrn(Long poLineRrn) {
		this.poLineRrn = poLineRrn;
	}

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

	public BigDecimal getQtyReceipt() {
		return qtyReceipt;
	}

	public void setQtyReceipt(BigDecimal qtyReceipt) {
		this.qtyReceipt = qtyReceipt;
	}

	public Date getDateApproved() {
		return dateApproved;
	}

	public void setDateApproved(Date dateApproved) {
		this.dateApproved = dateApproved;
	}

	public Date getReceiptDateApproved() {
		return receiptDateApproved;
	}

	public void setReceiptDateApproved(Date receiptDateApproved) {
		this.receiptDateApproved = receiptDateApproved;
	}

	public String getTrfMovementId() {
		return trfMovementId;
	}

	public void setTrfMovementId(String trfMovementId) {
		this.trfMovementId = trfMovementId;
	}

	public BigDecimal getTrfQtyMovement() {
		return trfQtyMovement;
	}

	public void setTrfQtyMovement(BigDecimal trfQtyMovement) {
		this.trfQtyMovement = trfQtyMovement;
	}

	public Date getTrfDateApproved() {
		return trfDateApproved;
	}

	public void setTrfDateApproved(Date trfDateApproved) {
		this.trfDateApproved = trfDateApproved;
	}

	public Long getTrfMaterialRrn() {
		return trfMaterialRrn;
	}

	public void setTrfMaterialRrn(Long trfMaterialRrn) {
		this.trfMaterialRrn = trfMaterialRrn;
	}

	
	
}
