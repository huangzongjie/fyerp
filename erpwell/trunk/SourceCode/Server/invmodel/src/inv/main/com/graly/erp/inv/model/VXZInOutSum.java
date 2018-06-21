package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_XZ_IN_OUT_SUM")
public class VXZInOutSum extends ADBase{
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="DOC_ID")
	private String docId ;
	
	@Column(name="DOC_STATUS")
	private String docStatus;

	@Column(name="DOC_TYPE")
	private String docType;
	
	@Column(name="MOVEMENT_TYPE")
	private String movementType;
	
	@Column(name="DATE_APPROVED")
	private Date dateApproved;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
 
	@Column(name="QTY_MOVEMENT")
	private BigDecimal qtyMovement;
	
	@Column(name="XZ_USER_NAME")
	private String xzUserName;
	
	@Column(name="XZ_DEPARTMENT")
	private String xzDepartment;

	@Column(name="XZ_COMPANY")
	private String xzCompany;
	
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

	public String getMovementType() {
		return movementType;
	}

	public void setMovementType(String movementType) {
		this.movementType = movementType;
	}

	public Date getDateApproved() {
		return dateApproved;
	}

	public void setDateApproved(Date dateApproved) {
		this.dateApproved = dateApproved;
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

}
