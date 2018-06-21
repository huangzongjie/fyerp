package com.graly.alm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_ALM_ALARM_TYPE")
public class VAlarmType extends ADBase{
	private static final long serialVersionUID = 1L;
	
	@Column(name="OBJECT_TYPE")
	private String objectType;
	
	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
}
