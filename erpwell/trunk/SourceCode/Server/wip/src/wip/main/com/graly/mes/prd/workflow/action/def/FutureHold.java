package com.graly.mes.prd.workflow.action.def;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("H")
public class FutureHold extends FutureAction {
	
	@Column(name = "HOLD_CODE")
	private String holdCode;

	@Column(name = "HOLD_REASON")
	private String holdReason;

	@Column(name = "HOLD_PWD")
	private String holdPwd;

	public String getHoldCode() {
		return holdCode;
	}

	public void setHoldCode(String holdCode) {
		this.holdCode = holdCode;
	}

	public String getHoldReason() {
		return holdReason;
	}

	public void setHoldReason(String holdReason) {
		this.holdReason = holdReason;
	}

	public String getHoldPwd() {
		return holdPwd;
	}

	public void setHoldPwd(String holdPwd) {
		this.holdPwd = holdPwd;
	}
}
