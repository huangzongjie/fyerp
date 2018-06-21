package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_SPARES_MATERIAL")
public class SparesMaterialUse extends ADBase {
	/**
	 * 备件原物料查询实体类
	 */
	private static final long serialVersionUID = 1L;
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name="OBJECT_RRN")
//	private Long objectRrn;
//	
//	@Column(name="ORG_RRN")
//	private Long orgRrn;
	
	@Column(name = "UPDATED")
	private Date updated;
	
	@Column(name = "MOVEMENT_ID")
	private String movementId;
	
	@Column(name = "MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name = "MATERIAL_ID")
	private String materialId;
	
	@Column(name = "MATERIAL_NAME")
	private String materialName;
	
	@Column(name = "QTY")
	private String qty;
	
	@Column(name = "EQUIPMENT_RRN")
	private Long equipmentRrn;
	
	@Column(name = "EQUIPMENT_ID")
	private String equipmentId;
	
	@Column(name = "EQUIPMENT_NAME")
	private String equipmentName;
	
	@Column(name = "REFERENCED_PRICE")
	private BigDecimal referencePrice;
	
	@Column(name = "TOTAL_PRICE")
	private BigDecimal totalPrice;

	public Long getObjectRrn() {
		return objectRrn;
	}

	public void setObjectRrn(Long objectRrn) {
		this.objectRrn = objectRrn;
	}

	public Long getOrgRrn() {
		return orgRrn;
	}

	public void setOrgRrn(Long orgRrn) {
		this.orgRrn = orgRrn;
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

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public Long getEquipmentRrn() {
		return equipmentRrn;
	}

	public void setEquipmentRrn(Long equipmentRrn) {
		this.equipmentRrn = equipmentRrn;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}
	
	public String getEquipmentName() {
		return equipmentName;
	}

	public void setEquipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}

	public BigDecimal getReferencePrice() {
		return referencePrice;
	}

	public void setReferencePrice(BigDecimal referencePrice) {
		this.referencePrice = referencePrice;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getMovementId() {
		return movementId;
	}

	public void setMovementId(String movementId) {
		this.movementId = movementId;
	}
}
