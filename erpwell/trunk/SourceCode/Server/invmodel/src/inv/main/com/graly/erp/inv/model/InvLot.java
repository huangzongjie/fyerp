package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="V_INV_LOT")
public class InvLot extends ADUpdatable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="MATERIAL_ID")
	protected String materialId;
	
	@Column(name="LOT_ID")
	protected String lotId;
	
	@Column(name="DOC_ID")
	protected String docId;
	
	@Column(name="DATE_CREATED")
	protected Date dateCreated;
	
	@Column(name="QTY_MOVEMENT")
	protected BigDecimal qtyMovement;
	
	@Column(name="DOC_TYPE")
	protected String docType;
	
	@Column(name="MOVEMENT_RRN")
	protected Long movementRrn;
	
	

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getLotId() {
		return lotId;
	}

	public void setLotId(String lotId) {
		this.lotId = lotId;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public BigDecimal getQtyMovement() {
		return qtyMovement;
	}

	public void setQtyMovement(BigDecimal qtyMovement) {
		this.qtyMovement = qtyMovement;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public Long getMovementRrn() {
		return movementRrn;
	}

	public void setMovementRrn(Long movementRrn) {
		this.movementRrn = movementRrn;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	
	

}
