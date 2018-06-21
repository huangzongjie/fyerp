package com.graly.mes.wip.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADUpdatable;

@MappedSuperclass
public abstract class ProcessUnit extends ADUpdatable{
	
	@Transient
	private ProcessUnit parentProcessUnit;
	
	@Transient
	private List<ProcessUnit> subProcessUnit;
	
	@Column(name="EQUIPMENT_RRN")
	protected Long equipmentRrn;
	
	@Column(name="EQUIPMENT_ID")
	protected String equipmentId;
	
	@Column(name="MAIN_QTY")
	protected Double mainQty;
	
	@Column(name="SUB_QTY")
	protected Double subQty;
	
	@Column(name="OPERATOR_RRN")
	protected Long operatorRrn;
	
	@Column(name="OPERATOR_NAME")
	protected String operatorName;

	@Column(name="PARENT_UNIT_Rrn")
	protected Long parentUnitRrn;
	
	@Column(name="SUB_UNIT_TYPE")
	protected String subUnitType;

	public static String getUnitType(){
		return "";
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

	public void setParentUnitRrn(Long parentUnitRrn) {
		this.parentUnitRrn = parentUnitRrn;
	}

	public Long getParentUnitRrn() {
		return parentUnitRrn;
	}
	
	public void setParentProcessUnit(ProcessUnit parentProcessUnit) {
		this.parentProcessUnit = parentProcessUnit;
	}

	public ProcessUnit getParentProcessUnit() {
		return parentProcessUnit;
	}

	public void setSubProcessUnit(List<ProcessUnit> subProcessUnit) {
		this.subProcessUnit = subProcessUnit;
	}

	public List<ProcessUnit> getSubProcessUnit() {
		return subProcessUnit;
	}

	public void setSubUnitType(String subUnitType) {
		this.subUnitType = subUnitType;
	}

	public String getSubUnitType() {
		return subUnitType;
	}

	public Long getOperatorRrn() {
		return operatorRrn;
	}

	public void setOperatorRrn(Long operatorRrn) {
		this.operatorRrn = operatorRrn;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
