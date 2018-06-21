package com.graly.erp.product.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="CANA_BOM_REQUEST")
public class CanaBomRequest implements Serializable {
private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="SERIAL_NUMBER")//相当于Material中的materialId
	private String  serialNumber;
	
	@Column(name="MATERIAL_ID") 
	private String  materialId;
	
	@Column(name="MATERIAL_NAME")
	private String  materialName;
	
	@Column(name="BOM_ID") 
	private String  bomId;//子物料id

	@Column(name="UOM")
	private String  uom;
	
	@Column(name="QTY_UNIT")
	private String  qtyUnit;
	
//	@Column(name="IS_APPROVED")
//	private String  isApproved;
	
	@Column(name="IS_IMPORTED_TO_ERP")
	private String  isImportedToErp;

	@Column(name="APPROVE_STATUS")
	private String  approveStatus;
	
	@Column(name="STATUS")
	private String  status;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
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

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public String getQtyUnit() {
		return qtyUnit;
	}

	public void setQtyUnit(String qtyUnit) {
		this.qtyUnit = qtyUnit;
	}

//	public String getIsApproved() {
//		return isApproved;
//	}
//
//	public void setIsApproved(String isApproved) {
//		this.isApproved = isApproved;
//	}

	public String getIsImportedToErp() {
		return isImportedToErp;
	}

	public void setIsImportedToErp(String isImportedToErp) {
		this.isImportedToErp = isImportedToErp;
	}

	public String getBomId() {
		return bomId;
	}

	public void setBomId(String bomId) {
		this.bomId = bomId;
	}

	public String getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
