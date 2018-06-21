package com.graly.alm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="ALM_ALARM_TYPE")
public class AlarmType extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	public static final String OBJECT_TYPE_MATERIAL = "Material";
	public static final String OBJECT_TYPE_RECEIPT = "Receipt";
	public static final String OBJECT_TYPE_IQC = "Iqc";
	
	public static final String ALARM_TYPE_MIN_INVENTORY = "MinInventory";
	public static final String ALARM_TYPE_RECEIPT_APPROVE = "ReceiptApprove";
	public static final String ALARM_TYPE_IQC_APPROVE = "IqcApprove";
	
	@Column(name="OBJECT_TYPE")
	private String objectType;
	
	@Column(name="ALARM_TYPE")
	private String alarmType;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
