package com.graly.mes.wiphis.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.graly.framework.activeentity.model.ADUpdatable;

@MappedSuperclass
public abstract class ProcessUnitHis extends ADUpdatable {
	
	@Column(name="HISTORY_SEQ")
	private Long hisSeq;
	
	@Column(name="EQUIPMENT_RRN")
	private Long equipmentRrn;
	
	@Column(name="EQUIPMENT_ID")
	private String equipmentId;
	
	@Column(name="MAIN_QTY")
	private Double mainQty;
	
	@Column(name="SUB_QTY")
	private Double subQty;
	
	@Column(name="OPERATOR_RRN")
	private Long operatorRrn;
	
	@Column(name="OPERATOR_NAME")
	protected String operatorName;

	@Column(name="PARENT_UNIT_RRN")
	private Long parentUnitRrn;

	@Column(name="SUB_UNIT_TYPE")
	protected String subUnitType;
	
	public void setHisSeq(Long hisSeq) {
		this.hisSeq = hisSeq;
	}

	public Long getHisSeq() {
		return hisSeq;
	}

	public void setSubUnitType(String subUnitType) {
		this.subUnitType = subUnitType;
	}

	public String getSubUnitType() {
		return subUnitType;
	}

	public void setEquipmentRrn(Long equipmentRrn) {
		this.equipmentRrn = equipmentRrn;
	}

	public Long getEquipmentRrn() {
		return equipmentRrn;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setMainQty(Double mainQty) {
		this.mainQty = mainQty;
	}

	public Double getMainQty() {
		return mainQty;
	}

	public void setSubQty(Double subQty) {
		this.subQty = subQty;
	}

	public Double getSubQty() {
		return subQty;
	}

	public void setOperatorRrn(Long operatorRrn) {
		this.operatorRrn = operatorRrn;
	}

	public Long getOperatorRrn() {
		return operatorRrn;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	
	public void setParentUnitRrn(Long parentUnitRrn) {
		this.parentUnitRrn = parentUnitRrn;
	}

	public Long getParentUnitRrn() {
		return parentUnitRrn;
	}
	
	
}
