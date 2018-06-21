package com.graly.mes.wiphis.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="WIPHIS_SBD")
public class LotHisSBD extends ADUpdatable{

	@Column(name="WIPHIS_RRN")
	private Long wipHisRrn;
	
	@Column(name="TRANS_TYPE")
	private String transType;
	
	@Column(name="LOT_RRN")
	private Long lotRrn;
	
	@Column(name="LOT_ID")
	private String lotId;
	
	@Column(name="EQUIPMENT_RRN")
	private Long equipmentRrn;
	
	@Column(name="EQUIPMENT_ID")
	private String equipmentId;

	@Column(name="COMPONENT_RRN")
	private Long componentRrn;
	
	@Column(name="COMPONENT_ID")
	private String componentId;

	@Column(name="ACTION_CODE")
	private String actionCode;
	
	@Column(name="MAIN_QTY")
	private Double mainQty;
	
	@Column(name="SUB_QTY")
	private Double subQty;
	
	public void setWipHisRrn(Long wipHisRrn) {
		this.wipHisRrn = wipHisRrn;
	}

	public Long getWipHisRrn() {
		return wipHisRrn;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getTransType() {
		return transType;
	}

	public void setLotRrn(Long lotRrn) {
		this.lotRrn = lotRrn;
	}

	public Long getLotRrn() {
		return lotRrn;
	}
	
	public void setLotId(String lotId) {
		this.lotId = lotId;
	}

	public String getLotId() {
		return lotId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentRrn(Long equipmentRrn) {
		this.equipmentRrn = equipmentRrn;
	}

	public Long getEquipmentRrn() {
		return equipmentRrn;
	}

	public void setComponentRrn(Long componentRrn) {
		this.componentRrn = componentRrn;
	}

	public Long getComponentRrn() {
		return componentRrn;
	}
	
	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	public String getActionCode() {
		return actionCode;
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
}
