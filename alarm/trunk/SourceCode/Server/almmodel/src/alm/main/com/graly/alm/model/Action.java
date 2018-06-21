package com.graly.alm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="ALM_ACTION")
public class Action extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="ALARM_DEFINITION_RRN")
	private Long alarmDefinitionRrn;
	
	@Column(name="ACTION_TYPE_RRN")
	private Long actionTypeRrn;
	
	@ManyToOne
	@JoinColumn(name = "ACTION_TYPE_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private ActionType actionType;
	
	@Column(name="PARAM1")
	private String param1;
	
	@Column(name="PARAM2")
	private String param2;
	
	@Column(name="PARAM3")
	private String param3;
	
	@Column(name="PARAM4")
	private String param4;
	
	@Column(name="PARAM5")
	private String param5;
	
	@Column(name="IS_REPEAT")//是否重复发送
	private String isRepeat;
	
	@Column(name="REPEAT_INTERVAL")
	private Long repeatInterval;//间隔时间
	
	@Transient
	protected String actionTypeId;
	
	public Long getAlarmDefintionRrn() {
		return alarmDefinitionRrn;
	}

	public void setAlarmDefintionRrn(Long alarmDefinitionRrn) {
		this.alarmDefinitionRrn = alarmDefinitionRrn;
	}

	public Long getActionTypeRrn() {
		return actionTypeRrn;
	}

	public void setActionTypeRrn(Long actionTypeRrn) {
		this.actionTypeRrn = actionTypeRrn;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	public String getParam3() {
		return param3;
	}

	public void setParam3(String param3) {
		this.param3 = param3;
	}

	public String getParam4() {
		return param4;
	}

	public void setParam4(String param4) {
		this.param4 = param4;
	}

	public String getParam5() {
		return param5;
	}

	public void setParam5(String param5) {
		this.param5 = param5;
	}

	public Boolean getIsRepeat() {
		return "Y".equals(this.isRepeat);
	}

	public void setIsRepeat(Boolean isRepeat) {
		this.isRepeat = isRepeat ? "Y" : "N";
	}

	public Long getRepeatInterval() {
		return repeatInterval;
	}

	public void setRepeatInterval(Long repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	public String getActionTypeId() {
		if(actionTypeId != null){
			return actionTypeId;
		}
		if (this.getActionType() != null) {
			return this.getActionType().getActionTypeId();
		}
		return "";
	}

	public void setActionTypeId(String actionTypeId) {
		this.actionTypeId = actionTypeId;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public ActionType getActionType() {
		return actionType;
	}
}
