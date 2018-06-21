package com.graly.alm.model;

import java.util.Date;

import com.graly.framework.activeentity.model.ADBase;

public class AlarmMessage extends ADBase {
	
	private static final long serialVersionUID = 1L;
	
	private String objectType;
	
	private String alarmType;
	
	private String objectId;
	
	private Date dateAlarm;
	
	private String alarmText;

	private Long refRrn;
	
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

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public Date getDateAlarm() {
		return dateAlarm;
	}

	public void setDateAlarm(Date dateAlarm) {
		this.dateAlarm = dateAlarm;
	}

	public String getAlarmText() {
		return alarmText;
	}

	public void setAlarmText(String alarmText) {
		this.alarmText = alarmText;
	}

	public void setRefRrn(Long refRrn) {
		this.refRrn = refRrn;
	}

	public Long getRefRrn() {
		return refRrn;
	}

}
