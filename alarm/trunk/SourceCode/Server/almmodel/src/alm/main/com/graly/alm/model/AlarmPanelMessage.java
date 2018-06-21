package com.graly.alm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="ALM_PANEL_MESSAGE")
public class AlarmPanelMessage extends ADUpdatable{
	
	private static final long serialVersionUID = 1L;
	
	public static String STATE_OPEN = "OPEN";
	public static String STATE_CLOSE = "CLOSE ";
	public static String STATE_DELETE = "DELETE";
	
	@Column(name="USER_RRN")
	private Long userRrn;
	
	@Column(name="ALARM_HIS_RRN")
	private Long alarmHisRrn;

	@OneToOne
	@JoinColumn(name = "ALARM_HIS_RRN", referencedColumnName = "OBJECT_RRN",  insertable = false, updatable = false)
	private AlarmHis alarmHis;
	
	@Column(name="STATE")
	private String state;
	
	public Long getUserRrn() {
		return userRrn;
	}

	public void setUserRrn(Long userRrn) {
		this.userRrn = userRrn;
	}

	public Long getAlarmHisRrn() {
		return alarmHisRrn;
	}

	public void setAlarmHisRrn(Long alarmHisRrn) {
		this.alarmHisRrn = alarmHisRrn;
	}

	public String getSeverity() {
		return alarmHis != null ? alarmHis.getSeverity() : "";
	}

	public String getAlarmId() {
		return alarmHis != null ? alarmHis.getAlarmId() : "";
	}
	
	public String getAlarmText() {
		return alarmHis != null ? alarmHis.getAlarmDesc() : "";
	}
	
	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public AlarmHis getAlarmHis() {
		return alarmHis;
	}

	public void setAlarmHis(AlarmHis alarmHis) {
		this.alarmHis = alarmHis;
	}

}
