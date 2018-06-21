package com.graly.framework.security.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="WIP_WORKCENTER")
public class WorkCenter extends ADUpdatable {
	private static final long serialVersionUID = 1L;

	@Column(name="NAME")
	private String name;

	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@Column(name="MANPOWER")
	private Long manpower;
	
	@Column(name="WEEK_TYPE")
	private String weekType;
	
	@ManyToMany(mappedBy="workCenters")
	private List<ADUser> users;
	
	@Column(name="MUST_EQP1")
	private String mustEqp1 = "N";
	
	@Column(name="MUST_EQP2")
	private String mustEqp2 = "N";
	
	@Column(name="MUST_MOLD1")
	private String mustMold1 = "N";
	
	@Column(name="MUST_MOLD2")
	private String mustMold2 = "N";
	
	@Column(name="MUST_MOLD3")
	private String mustMold3 = "N";
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="WIP_WORKCENTER_EQUIPMENT",
	joinColumns={@JoinColumn(name="WORKCENTER_RRN", referencedColumnName = "OBJECT_RRN")},
	inverseJoinColumns={@JoinColumn(name="EQUIPMENT_RRN", referencedColumnName = "OBJECT_RRN")}
	)
	private List<WIPEquipment> equipments;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseRrn(Long warehouseRrn) {
		this.warehouseRrn = warehouseRrn;
	}

	public Long getManpower() {
		return manpower;
	}

	public void setManpower(Long manpower) {
		this.manpower = manpower;
	}

	public void setWeekType(String weekType) {
		this.weekType = weekType;
	}

	public String getWeekType() {
		return weekType;
	}

	public List<ADUser> getUsers() {
		return users;
	}

	public boolean getMustEqp1() {
		return "Y".equals(mustEqp1) ? true : false;
	}

	public void setMustEqp1(boolean mustEqp1) {
		this.mustEqp1 = (mustEqp1 ? "Y" : "N");
	}

	public boolean getMustEqp2() {
		return "Y".equals(mustEqp2) ? true : false;
	}

	public void setMustEqp2(boolean mustEqp2) {
		this.mustEqp2 = (mustEqp2 ? "Y" : "N");
	}

	public boolean getMustMold1() {
		return "Y".equals(mustMold1) ? true : false;
	}

	public void setMustMold1(boolean mustMold1) {
		this.mustMold1 = (mustMold1 ? "Y" : "N");;
	}

	public boolean getMustMold2() {
		return "Y".equals(mustMold2) ? true : false;
	}

	public void setMustMold2(boolean mustMold2) {
		this.mustMold2 = (mustMold2 ? "Y" : "N");;
	}

	public boolean getMustMold3() {
		return "Y".equals(mustMold3) ? true : false;
	}

	public void setMustMold3(boolean mustMold3) {
		this.mustMold3 = (mustMold3 ? "Y" : "N");;
	}

	public List<WIPEquipment> getEquipments() {
		return equipments;
	}

	public void setEquipments(List<WIPEquipment> equipments) {
		this.equipments = equipments;
	}
}
