package com.graly.erp.wip.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="WIP_WCT_LOT_STORAGE")
public class WCTLotStorage extends ADUpdatable {
	private static final long serialVersionUID = 1L;

	@Column(name="LOT_RRN")
	private Long lotRrn;

	@Column(name="LOT_ID")
	private String lotId;

	@Column(name="QTY_MOVEMENT")
	private BigDecimal qtyMovement;

	@Column(name="MOVEMENT_RRN")
	private Long movementRrn;

	@Column(name="MOVEMENT_ID")
	private String movementId;

	@Column(name="MOVEMENT_LINE_RRN")
	private Long movementLineRrn;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;

	@Column(name="MATERIAL_NAME")
	private String materialName;

	@Column(name="QTY_INITIAL")
	private BigDecimal qtyInitial;
	
	@Column(name="QTY_CURRENT")
	private BigDecimal qtyCurrent;
	
	@Column(name="WORKCENTER_RRN")
	private Long workCenterRrn;

	@Column(name="WORKCENTER_ID")
	private String workCenterId;
	
	@Column(name="RESERVED_FIELD1")
	private String reservedField1;//±£Áô×Ö¶Î£±
	
	@Column(name="RESERVED_FIELD2")
	private String reservedField2;//±£Áô×Ö¶Î£²
	
	@Column(name="RESERVED_FIELD3")
	private String reservedField3;//±£Áô×Ö¶Î£³
	
	@Column(name="RESERVED_FIELD4")
	private String reservedField4;//±£Áô×Ö¶Î£´
	
	@Column(name="RESERVED_FIELD5")
	private String reservedField5;//±£Áô×Ö¶Î£µ
	
	@Column(name="RESERVED_FIELD6")
	private String reservedField6;//±£Áô×Ö¶Î£¶
	
	@Column(name="RESERVED_FIELD7")
	private String reservedField7;//±£Áô×Ö¶Î£·
	
	@Column(name="RESERVED_FIELD8")
	private String reservedField8;//±£Áô×Ö¶Î£¸
	
	@Column(name="RESERVED_FIELD9")
	private String reservedField9;//±£Áô×Ö¶Î£¹
	
	@Column(name="RESERVED_FIELD10")
	private String reservedField10;//±£Áô×Ö¶Î10

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

	public BigDecimal getQtyInitial() {
		return qtyInitial;
	}

	public void setQtyInitial(BigDecimal qtyInitial) {
		this.qtyInitial = qtyInitial;
	}

	public BigDecimal getQtyCurrent() {
		return qtyCurrent;
	}

	public void setQtyCurrent(BigDecimal qtyCurrent) {
		this.qtyCurrent = qtyCurrent;
	}

	public Long getWorkCenterRrn() {
		return workCenterRrn;
	}

	public void setWorkCenterRrn(Long workCenterRrn) {
		this.workCenterRrn = workCenterRrn;
	}

	public String getWorkCenterId() {
		return workCenterId;
	}

	public void setWorkCenterId(String workCenterId) {
		this.workCenterId = workCenterId;
	}

	public String getReservedField1() {
		return reservedField1;
	}

	public void setReservedField1(String reservedField1) {
		this.reservedField1 = reservedField1;
	}

	public String getReservedField2() {
		return reservedField2;
	}

	public void setReservedField2(String reservedField2) {
		this.reservedField2 = reservedField2;
	}

	public String getReservedField3() {
		return reservedField3;
	}

	public void setReservedField3(String reservedField3) {
		this.reservedField3 = reservedField3;
	}

	public String getReservedField4() {
		return reservedField4;
	}

	public void setReservedField4(String reservedField4) {
		this.reservedField4 = reservedField4;
	}

	public String getReservedField5() {
		return reservedField5;
	}

	public void setReservedField5(String reservedField5) {
		this.reservedField5 = reservedField5;
	}

	public String getReservedField6() {
		return reservedField6;
	}

	public void setReservedField6(String reservedField6) {
		this.reservedField6 = reservedField6;
	}

	public String getReservedField7() {
		return reservedField7;
	}

	public void setReservedField7(String reservedField7) {
		this.reservedField7 = reservedField7;
	}

	public String getReservedField8() {
		return reservedField8;
	}

	public void setReservedField8(String reservedField8) {
		this.reservedField8 = reservedField8;
	}

	public String getReservedField9() {
		return reservedField9;
	}

	public void setReservedField9(String reservedField9) {
		this.reservedField9 = reservedField9;
	}

	public String getReservedField10() {
		return reservedField10;
	}

	public void setReservedField10(String reservedField10) {
		this.reservedField10 = reservedField10;
	}
}
