package com.graly.erp.inv.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="INV_MOVEMENT_WORKSHOP_LINE_LOT")
public class MovementWorkShopLineLot extends ADUpdatable {

	private static final long serialVersionUID = 1L;
	
	@Column(name="MOVEMENT_RRN")
	private Long movementRrn;
	
	@Column(name="MOVEMENT_ID")
	private String movementId;

	@Column(name="MOVEMENT_LINE_RRN")
	private Long movementLineRrn;
	
	@Column(name="LOT_RRN")
	private Long lotRrn;
	
	@Column(name="LOT_ID")
	private String lotId;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="QTY_MOVEMENT")
	private BigDecimal qtyMovement;
	
	@ManyToOne
	@JoinColumn(name = "MATERIAL_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material material;
	
	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

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
	
	public Long getMovementLineRrn() {
		return movementLineRrn;
	}
	
	public void setMovementLineRrn(Long movementLineRrn) {
		this.movementLineRrn = movementLineRrn;
	}
	
	public Long getLotRrn() {
		return lotRrn;
	}
	
	public void setLotRrn(Long lotRrn) {
		this.lotRrn = lotRrn;
	}
	
	public String getLotId() {
		return lotId;
	}
	public void setLotId(String lotId) {
		this.lotId = lotId;
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
	
}
