package com.graly.erp.wip.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="WIP_WCT_MOVMT_LINE")
public class WCTMovementLine extends ADUpdatable {
	private static final long serialVersionUID = 1L;

	@Column(name="LINE_NO")
	private Long lineNo;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;

	@Column(name="MATERIAL_ID")
	private String materialId;

	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="MOVEMENT_RRN")
	private Long movementRrn;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="MOVEMENT_RRN", referencedColumnName="OBJECT_RRN", insertable=false, updatable=false)
	private WCTMovement movement;
	
	@Transient
	private List<WCTMovementLineLot> lineLots;
	
	@Column(name="QTY_MOVEMENT")
	private BigDecimal qtyMovement;

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

	public BigDecimal getQtyMovement() {
		return qtyMovement;
	}

	public void setQtyMovement(BigDecimal qtyMovement) {
		this.qtyMovement = qtyMovement;
	}

	public Long getMovementRrn() {
		return movementRrn;
	}

	public void setMovementRrn(Long movementRrn) {
		this.movementRrn = movementRrn;
	}

	public WCTMovement getMovement() {
		return movement;
	}

	public void setMovement(WCTMovement movement) {
		this.movement = movement;
	}

	public List<WCTMovementLineLot> getLineLots() {
		return lineLots;
	}

	public void setLineLots(List<WCTMovementLineLot> lineLots) {
		this.lineLots = lineLots;
	}
}
