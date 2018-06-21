package com.graly.framework.security.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="WIP_EQUIPMENT")
public class WIPEquipment extends ADUpdatable {
	private static final long serialVersionUID = 1L;

	@Column(name="EQUIPMENT_ID")
	private String equipmentId;
	
	@Column(name="EQUIPMENT_NAME")
	private String equipmentName;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH)
	@JoinTable(name="WIP_EQUIPMENT_MOULD",
	joinColumns={@JoinColumn(name="EQUIPMENT_RRN",referencedColumnName="OBJECT_RRN", insertable = false, updatable = false)},
	inverseJoinColumns={@JoinColumn(name="MOULD_RRN", referencedColumnName="OBJECT_RRN",insertable = false, updatable = false)})
	private List<WIPMould> moulds;
	
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

	public List<WIPMould> getMoulds() {
		return moulds;
	}

	public void setMoulds(List<WIPMould> moulds) {
		this.moulds = moulds;
	}
}
