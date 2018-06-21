package com.graly.framework.security.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="WIP_MOULD")
public class WIPMould extends ADUpdatable {
	private static final long serialVersionUID = 1L;

	@Column(name="MOULD_ID")
	private String mouldId;
	
	@Column(name="MOULD_NAME")
	private String mouldName;
	
	@Column(name="MAINTENANCE")
	private String maintenance;
	
	@Column(name="MAINTENANCE_HIS")
	private String maintenanceHis;
	
	@ManyToMany(mappedBy="moulds")
	private List<WIPEquipment> equipments;

	public String getMouldId() {
		return mouldId;
	}

	public void setMouldId(String mouldId) {
		this.mouldId = mouldId;
	}

	public String getMouldName() {
		return mouldName;
	}

	public void setMouldName(String mouldName) {
		this.mouldName = mouldName;
	}

	public List<WIPEquipment> getEquipments() {
		return equipments;
	}

	public void setEquipments(List<WIPEquipment> equipments) {
		this.equipments = equipments;
	}

	public String getMaintenance() {
		return maintenance;
	}

	public void setMaintenance(String maintenance) {
		this.maintenance = maintenance;
	}

	public String getMaintenanceHis() {
		return maintenanceHis;
	}

	public void setMaintenanceHis(String maintenanceHis) {
		this.maintenanceHis = maintenanceHis;
	}
}
