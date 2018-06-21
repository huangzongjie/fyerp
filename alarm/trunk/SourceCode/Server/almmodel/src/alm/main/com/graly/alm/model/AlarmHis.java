package com.graly.alm.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="ALM_ALARM_HIS")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="ALARM_CLASS", discriminatorType = DiscriminatorType.STRING, length = 1)
@DiscriminatorValue("T")
public class AlarmHis extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	public static String STATE_OPEN = "OPEN";
	public static String STATE_CLOSE = "CLOSE ";
	
	@Column(name="ALARM_RRN")
	private Long alarmRrn;
	
	@Column(name="ALARM_ID")
	private String alarmId;
	
	@Column(name="ALARM_DESC")
	private String alarmDesc;
	
	@Column(name="OBJECT_TYPE")
	private String objectType;
	
	@Column(name="ALARM_TYPE")
	private String alarmType;
	
	@Column(name="OBJECT_ID")
	private String objectId;
	
	@Column(name="IS_NEED_CLOSE")
	private String isNeedClose;
	
	@Column(name="SEVERITY")
	private String severity;
	
	@Column(name="PRIORITY")
	private Long priority;
	
	@Column(name="DATE_ALARM")
	private Date dateAlarm;
	
	@Column(name="DATE_CLOSE")
	private Date dateClose;
	
	@Column(name="USER_OWNER")
	private String userOwner;

	@Column(name="USER_CLOSE")
	private String userClose;
	
	@Column(name="ALARM_STATE")
	private String state;
	
	@Column(name="ALARM_TEXT")
	private String alarmText;
	
	@Column(name="COMMENTS")
	private String comments;

	@Column(name="REF_RRN")
	private Long refRrn;
	
	public AlarmHis() {
		
	}
	
	public AlarmHis(AlarmDefinition alarmDf) {
		this.alarmRrn = alarmDf.getObjectRrn();
		this.alarmId = alarmDf.getAlarmId();
		this.alarmDesc = alarmDf.getDescription();
		this.objectType = alarmDf.getObjectType();
		this.alarmType = alarmDf.getAlarmType();
		this.setIsNeedClose(alarmDf.getIsNeedClose());
		this.severity = alarmDf.getSeverity();
		this.priority = alarmDf.getPriority();
		this.userOwner = alarmDf.getUserOwner();
	}


	public void setAlarmRrn(Long alarmRrn) {
		this.alarmRrn = alarmRrn;
	}

	public Long getAlarmRrn() {
		return alarmRrn;
	}
	
	public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	public String getAlarmDesc() {
		return alarmDesc;
	}

	public void setAlarmDesc(String alarmDesc) {
		this.alarmDesc = alarmDesc;
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

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
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

	public Date getDateAlarm() {
		return dateAlarm;
	}

	public void setDateAlarm(Date dateAlarm) {
		this.dateAlarm = dateAlarm;
	}

	public Date getDateClose() {
		return dateClose;
	}

	public void setDateClose(Date dateClose) {
		this.dateClose = dateClose;
	}

	public String getUserOwner() {
		return userOwner;
	}

	public void setUserOwner(String userOwner) {
		this.userOwner = userOwner;
	}

	public String getUserClose() {
		return userClose;
	}

	public void setUserClose(String userClose) {
		this.userClose = userClose;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setAlarmText(String alarmText) {
		this.alarmText = alarmText;
	}

	public String getAlarmText() {
		return alarmText;
	}
	
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}


	public void setRefRrn(Long refRrn) {
		this.refRrn = refRrn;
	}


	public Long getRefRrn() {
		return refRrn;
	}



}
