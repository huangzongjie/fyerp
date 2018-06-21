package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
/*调拨功能:采购入库单查询视图*/
@Entity
@Table(name="V_TRANSFER_INV_MOVEMENT")
public class TransferMovement extends ADUpdatable {

	private static final long serialVersionUID = 1L;

	@Column(name="MOVEMENT_ID")
	private String movementId;	
 
	@Column(name="MOVEMENT_RRN")
	private Long movementRrn;
	
	@Column(name="uom_id")
	private String uomId;
	
	@Column(name="QTY_MOVEMENT")
	private BigDecimal qtyMovement;
	
	@Column(name="LINE_STATUS")
	private String lineStatus;
	
	@Column(name="PO_RRN")
	private Long poRrn;
	
	@Column(name="PO_ID")
	private String poId;
	
	@Column(name="RECEIPT_RRN")
	private Long receiptRrn;
	
	@Column(name="RECEIPT_ID")
	private String receiptId;	
 
	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="IQC_RRN")
	private Long iqcRrn;
	
	@Column(name="IQC_ID")
	private String iqcId;
	
	@Column(name="DATE_CREATED")
	private Date dateCreated;
	
	@Column(name="VENDOR_RRN")
	private Long vendorRrn;
	
	@Column(name="VENDOR_ID")
	private String vendorId;
	
	@Column(name="COMPANY_NAME")
	private String companyName;
	
	@Column(name="BEING_TRANSFER")
	private String beingTransfer;
	
	@Column(name="BARCODE")
	private String barCode;

	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="LOT_TYPE")
	private String lotType;
	
	@Column(name="USER_CREATED")
	private String userCreated;
	
	
	public String getMovementId() {
		return movementId;
	}

	public void setMovementId(String movementId) {
		this.movementId = movementId;
	}

	public Long getMovementRrn() {
		return movementRrn;
	}

	public void setMovementRrn(Long movementRrn) {
		this.movementRrn = movementRrn;
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

	public String getLineStatus() {
		return lineStatus;
	}

	public void setLineStatus(String lineStatus) {
		this.lineStatus = lineStatus;
	}

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

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
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

	public Long getIqcRrn() {
		return iqcRrn;
	}

	public void setIqcRrn(Long iqcRrn) {
		this.iqcRrn = iqcRrn;
	}

	public String getIqcId() {
		return iqcId;
	}

	public void setIqcId(String iqcId) {
		this.iqcId = iqcId;
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getBeingTransfer() {
		return beingTransfer;
	}

	public void setBeingTransfer(String beingTransfer) {
		this.beingTransfer = beingTransfer;
	}

	public String getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(String userCreated) {
		this.userCreated = userCreated;
	}

	public String getLotType() {
		return lotType;
	}

	public void setLotType(String lotType) {
		this.lotType = lotType;
	}
	
}
