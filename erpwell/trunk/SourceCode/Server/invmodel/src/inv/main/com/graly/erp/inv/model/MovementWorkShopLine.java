package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADUpdatable;
import com.graly.mes.wip.model.Lot;

@Entity
@Table(name="INV_MOVEMENT_WORKSHOP_LINE")
public class MovementWorkShopLine extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="MOVEMENT_RRN")
	private Long movementRrn;
	
	@Column(name="MOVEMENT_ID")
	private String movementId;
	
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
	
	@Column(name="QTY_MOVEMENT")
	private BigDecimal qtyMovement;
	
	@Column(name="MO_LINE_RRN")
	private Long moLineRrn;
	
	@Column(name="LINE_STATUS")
	private String lineStatus;
	
	@Column(name="LOT_TYPE")
	private String lotType;
	
	@Transient
	private List<Lot> lots;
	
	@Transient
	private List<MovementWorkShopLineLot> movementWorkShopLots;
	
	
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


	public void setLineStatus(String lineStatus) {
		this.lineStatus = lineStatus;
	}

	public String getLineStatus() {
		return lineStatus;
	}
	
	
	public void setLots(List<Lot> lots) {
		this.lots = lots;
	}

	public List<Lot> getLots() {
		return lots;
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

	public List<MovementWorkShopLineLot> getMovementWorkShopLots() {
		return movementWorkShopLots;
	}

	public void setMovementWorkShopLots(
			List<MovementWorkShopLineLot> movementWorkShopLots) {
		this.movementWorkShopLots = movementWorkShopLots;
	}

	public String getLotType() {
		return lotType;
	}

	public void setLotType(String lotType) {
		this.lotType = lotType;
	}

}
