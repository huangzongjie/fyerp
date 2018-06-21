package com.graly.erp.inv.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

/**
 * 备件ERP，设备实体类
 * */
@Entity
@Table(name="BJ_WIP_EQUIPMENT")
public class BJWipEquipment extends ADUpdatable {
	private static final long serialVersionUID = 1L;

	@Column(name="EQUIPMENT_ID")
	private String equipmentId;//设备ID
	
	@Column(name="EQUIPMENT_NAME")
	private String equipmentName;//设备名称
	
	@Column(name="DESCRIPTION")
	private String description;//描述
	
	@Column(name="PURCHASE_DATE")
	private Date purchaseDate;//采购日期
	
	@Column(name="EQUIPMENT_TYPE")
	private String equipmentType;//设备类型
	
	@Column(name="ADDRESS")
	private String address;//设备存放地点
	
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

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getEquipmentType() {
		return equipmentType;
	}

	public void setEquipmentType(String equipmentType) {
		this.equipmentType = equipmentType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
