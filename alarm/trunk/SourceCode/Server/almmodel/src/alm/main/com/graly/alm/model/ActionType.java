package com.graly.alm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="ALM_ACTION_TYPE")
public class ActionType extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	public static final String ACTION_TYPE_PANEL = "Panel";
	public static final String ACTION_TYPE_MAIL = "Mail";
	public static final String ACTION_TYPE_SMS = "SMS";
	
	@Column(name="ACTION_TYPE_ID")
	private String actionTypeId;
	
	@Column(name="PROGRAM_ID")
	private String programId;
	
	@Column(name="PARAM1_DEFAULT")
	private String parm1Default;
	
	@Column(name="PARAM2_DEFAULT")
	private String parm2Defaul;
	
	@Column(name="PARAM3_DEFAULT")
	private String parm3Defaul;
	
	@Column(name="PARAM4_DEFAULT")
	private String parm4Defaul;
	
	@Column(name="PARAM5_DEFAULT")
	private String parm5Defaul;
	
	@Column(name="IS_REPEAT_DEFAULT")
	private String isRepeat;
	
	@Column(name="REPEAT_INTERVAL_DEFAULT")
	private Long repeatIntervalDefault;

	public String getActionTypeId() {
		return actionTypeId;
	}

	public void setActionTypeId(String actionTypeId) {
		this.actionTypeId = actionTypeId;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getParm1Default() {
		return parm1Default;
	}

	public void setParm1Default(String parm1Default) {
		this.parm1Default = parm1Default;
	}

	public String getParm2Defaul() {
		return parm2Defaul;
	}

	public void setParm2Defaul(String parm2Defaul) {
		this.parm2Defaul = parm2Defaul;
	}

	public String getParm3Defaul() {
		return parm3Defaul;
	}

	public void setParm3Defaul(String parm3Defaul) {
		this.parm3Defaul = parm3Defaul;
	}

	public String getParm4Defaul() {
		return parm4Defaul;
	}

	public void setParm4Defaul(String parm4Defaul) {
		this.parm4Defaul = parm4Defaul;
	}

	public String getParm5Defaul() {
		return parm5Defaul;
	}

	public void setParm5Defaul(String parm5Defaul) {
		this.parm5Defaul = parm5Defaul;
	}

	public Boolean getIsRepeat() {
		return "Y".equals(this.isRepeat);
	}

	public void setIsRepeat(Boolean isRepeat) {
		this.isRepeat = isRepeat ? "Y" : "N";
	}

	public Long getRepeatIntervalDefault() {
		return repeatIntervalDefault;
	}

	public void setRepeatIntervalDefault(Long repeatIntervalDefault) {
		this.repeatIntervalDefault = repeatIntervalDefault;
	}
}
