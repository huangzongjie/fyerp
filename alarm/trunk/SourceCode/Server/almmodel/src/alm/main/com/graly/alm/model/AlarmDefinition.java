package com.graly.alm.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="ALM_ALARM_DEFINITION")
public class AlarmDefinition extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="ALARM_ID")
	private String alarmId;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="OBJECT_TYPE")
	private String objectType;
	
	@Column(name="ALARM_TYPE")
	private String alarmType;
	
	@Column(name="OBJECT_FILTER")
	private String objectFileter;
	
	@Column(name="IS_ENABLE")
	private String isEnable;
	
	@Column(name="IS_NEED_CLOSE")
	private String isNeedClose;
	
	@Column(name="SEVERITY")
	private String severity;
	
	@Column(name="PRIORITY")
	private Long priority;
	
	@Column(name="DEFAULT_ALARM_TEXT")
	private String defaultAlarmText;
	
	@Column(name="USER_OWNER")
	private String userOwner;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@OrderBy(value = "created ASC")
	@JoinColumn(name = "ALARM_RRN", referencedColumnName = "OBJECT_RRN")
	private List<Action> actions;

	public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

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

	public void setObjectFileter(String objectFileter) {
		this.objectFileter = objectFileter;
	}

	public String getObjectFileter() {
		return objectFileter;
	}
	
	public Boolean getIsEnable() {
		return "Y".equals(this.isEnable);
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable ? "Y" : "N";
	}

	public Boolean getIsNeedClose() {
		return "Y".equals(this.isNeedClose);
	}

	public void setIsNeedClose(Boolean isNeedClose) {
		this.isNeedClose = isNeedClose ? "Y" : "N";
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public Long getPriority() {
		return priority;
	}

	public void setPriority(Long priority) {
		this.priority = priority;
	}

	public String getDefaultAlarmText() {
		return defaultAlarmText;
	}

	public void setDefaultAlarmText(String defaultAlarmText) {
		this.defaultAlarmText = defaultAlarmText;
	}

	public String getUserOwner() {
		return userOwner;
	}

	public void setUserOwner(String userOwner) {
		this.userOwner = userOwner;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public List<Action> getActions() {
		return actions;
	}

}
