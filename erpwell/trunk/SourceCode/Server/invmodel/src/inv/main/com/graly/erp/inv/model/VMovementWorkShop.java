package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="V_INV_MOVEMENT_WORKSHOP")
public class VMovementWorkShop extends ADUpdatable  {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="DOC_ID")
	private String docId;
	
	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "DOC_STATUS")
	private String docStatus;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="TARGET_WAREHOUSE_ID")
	private String targetWarehouseId;
	
	@Column(name="MO_ID")
	private String moId;
	
	@Column(name="USER_CREATED")
	private String userCreated;//¥¥Ω®»À
	
	@Column(name="USER_APPROVED")
	private String userApproved;
	
	@Column(name="DATE_APPROVED")
	private Date dateApproved;
	
	@Column(name="TRS_TYPE")
	private String trsType;
	
	@Column(name="MOVEMENT_ID")
	private String movementId;
	  
	@Column(name="MATERIAL_ID")
	private String materialId;

	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="QTY_MOVEMENT")
	private BigDecimal qtyMovement;
	 
	
	@Column(name="LOT_TYPE")
	private String lotType;


	public String getDocId() {
		return docId;
	}


	public void setDocId(String docId) {
		this.docId = docId;
	}


	public String getDocType() {
		return docType;
	}


	public void setDocType(String docType) {
		this.docType = docType;
	}


	public String getDocStatus() {
		return docStatus;
	}


	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getWarehouseId() {
		return warehouseId;
	}


	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}


	public String getTargetWarehouseId() {
		return targetWarehouseId;
	}


	public void setTargetWarehouseId(String targetWarehouseId) {
		this.targetWarehouseId = targetWarehouseId;
	}


	public String getMoId() {
		return moId;
	}


	public void setMoId(String moId) {
		this.moId = moId;
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


	public Date getDateApproved() {
		return dateApproved;
	}


	public void setDateApproved(Date dateApproved) {
		this.dateApproved = dateApproved;
	}


	public String getTrsType() {
		return trsType;
	}


	public void setTrsType(String trsType) {
		this.trsType = trsType;
	}


	public String getMovementId() {
		return movementId;
	}


	public void setMovementId(String movementId) {
		this.movementId = movementId;
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


	public BigDecimal getQtyMovement() {
		return qtyMovement;
	}


	public void setQtyMovement(BigDecimal qtyMovement) {
		this.qtyMovement = qtyMovement;
	}


	public String getLotType() {
		return lotType;
	}


	public void setLotType(String lotType) {
		this.lotType = lotType;
	}
}

