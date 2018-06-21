package com.graly.erp.inv.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

/**
 * ����ERP���豸ʵ����
 * */
@Entity
@Table(name="BJ_WIP_EQUIPMENT")
public class BJWipEquipment extends ADUpdatable {
	private static final long serialVersionUID = 1L;

	@Column(name="EQUIPMENT_ID")
	private String equipmentId;//�豸ID
	
	@Column(name="EQUIPMENT_NAME")
	private String equipmentName;//�豸����
	
	@Column(name="DESCRIPTION")
	private String description;//����
	
	@Column(name="PURCHASE_DATE")
	private Date purchaseDate;//�ɹ�����
	
	@Column(name="EQUIPMENT_TYPE")
	private String equipmentType;//�豸����
	
	@Column(name="ADDRESS")
	private String address;//�豸��ŵص�
	
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
